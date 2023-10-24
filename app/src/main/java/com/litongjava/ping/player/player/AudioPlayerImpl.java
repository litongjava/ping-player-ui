package com.litongjava.ping.player.player;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.jfinal.aop.AopManager;
import com.litongjava.ping.player.revicer.NoisyAudioStreamReceiver;
import com.litongjava.ping.player.storage.db.MusicDatabase;
import com.litongjava.ping.player.storage.db.entity.SongEntity;
import com.litongjava.ping.player.storage.preferences.ConfigPreferences;
import com.litongjava.ping.player.test.TestSongEntity;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class AudioPlayerImpl implements AudioPlayer {
  private Logger log = LoggerFactory.getLogger(this.getClass());
  private static final long TIME_UPDATE = 300L;
  private MutableLiveData<List<SongEntity>> _playlist = new MutableLiveData<>();
  private MutableLiveData<SongEntity> _currentSong = new MutableLiveData<>();
  private PlayState _playState = PlayState.IDLE;
  private long _playProgress = 0;
  private int _bufferingPercent = 0;


  private MusicDatabase db;

  private MediaPlayer mediaPlayer;
  private MediaSessionManager mediaSessionManager;
  private AudioFocusManager audioFocusManager;
  private NoisyAudioStreamReceiver noisyReceiver = Aop.get(NoisyAudioStreamReceiver.class);
  private IntentFilter noisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

  private ScheduledExecutorService scheduledExecutor;
  private ScheduledFuture<?> updateProgressFuture;


  public AudioPlayerImpl(MusicDatabase db) {
    this.db = db;
    mediaPlayer = new MediaPlayer();

  }

  @Override
  public LiveData<List<SongEntity>> getPlaylist() {
    return _playlist;
  }

  @Override
  public LiveData<SongEntity> getCurrentSong() {
    return _currentSong;
  }

  @Override
  public PlayState getPlayState() {
    return _playState;
  }

  @Override
  public Long getPlayProgress() {
    return _playProgress;
  }

  @Override
  public Integer getBufferingPercent() {
    return _bufferingPercent;
  }

  @MainThread
  @Override
  public void addAndPlay(SongEntity... songs) {
    new AddAndPlayTask().execute(songs);
  }

  /**
   * 处理__playlist为空的情况
   *
   * @return
   */
  private List<SongEntity> getNewList() {
    List<SongEntity> newPlaylist = null;
    log.info("__playlist:{}", _playlist);
    if (_playlist == null) {
      newPlaylist = new ArrayList<>();
    } else {
      List<SongEntity> currentList = _playlist.getValue();
      if (currentList == null) {
        newPlaylist = new ArrayList<>();
      } else {
        newPlaylist = new ArrayList<>(currentList);
      }
    }
    return newPlaylist;
  }

  private void updateDb(List<SongEntity> newPlaylist) {
    ExecutorService ioExecutor = Executors.newSingleThreadExecutor();
    try {
      ioExecutor.submit(() -> {
        db.playlistDao().clear();
        db.playlistDao().insertAll(newPlaylist);
      });
    } finally {
      ioExecutor.shutdown();
    }
  }


  @MainThread
  @Override
  public void replaceAll(List<SongEntity> songList, SongEntity song) {
    // Launch a new task on the main thread
    Executors.newSingleThreadExecutor().submit(() -> {
      // Execute database operations on an IO thread
      try {
        Executors.newSingleThreadExecutor().submit(() -> {
          db.playlistDao().clear();
          db.playlistDao().insertAll(songList);
          return null;
        }).get(); // Wait for the IO operations to complete
      } catch (ExecutionException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      _playlist.postValue(songList);
      _currentSong.postValue(song);
      play(song);
    });
  }


  @MainThread
  @Override
  public void play(SongEntity song) {

    List<SongEntity> playlist = _playlist.getValue();
    if (playlist == null || playlist.isEmpty()) {
      return;
    }
    if (song != null && !playlist.contains(song)) {
      return;
    }
    SongEntity playSong = song == null ? playlist.get(0) : song;
    _currentSong.setValue(playSong);
    _playProgress = 0L;
    _bufferingPercent = 0;
    _playState = PlayState.PREPARING;

    PlayService.showNotification(Utils.getApp(), true, playSong);

    if (mediaSessionManager == null) {
      initMediaSessionManager();

    }
    mediaSessionManager.updateMetaData(playSong);
    mediaSessionManager.updatePlaybackState();
    if (playSong.isLocal()) {
      realPlay(playSong);
    } else {
      ToastUtils.showLong("Only support load music");
    }
  }

  private void initMediaSessionManager() {
    mediaSessionManager = new MediaSessionManager(Utils.getApp(), Aop.get(AudioPlayer.class));
    AopManager.me().addSingletonObject(mediaSessionManager);
  }

  private void initAudioFocusManager() {
    audioFocusManager = new AudioFocusManager(Utils.getApp(), Aop.get(AudioPlayer.class));
    AopManager.me().addSingletonObject(audioFocusManager);
  }

  public void realPlay(SongEntity playSong) {
    String path = playSong.getPath();
    log.info("play:{}", path);
    // 设置数据源为你的mp3文件的路径
//    MediaPlayer mediaPlayer = new MediaPlayer();
//    try {
//      mediaPlayer.setDataSource(path);
//      mediaPlayer.prepare(); // 准备播放
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    mediaPlayer.start();   // 开始播放


    if (mediaPlayer == null) {
      mediaPlayer = new MediaPlayer();
    }
    try {
      mediaPlayer.reset();
      mediaPlayer.setDataSource(path);
      mediaPlayer.prepare();
      mediaPlayer.start();
      _playState = PlayState.PLAYING;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @MainThread
  @Override
  public void delete(SongEntity song) {
    Executors.newSingleThreadExecutor().submit(() -> {
      List<SongEntity> playlist = new ArrayList<>(_playlist.getValue());
      int index = playlist.indexOf(song);
      if (index < 0) return;
      playlist.remove(index);
      _playlist.postValue(playlist);

      // Execute database operations on an IO thread
      try {
        Executors.newSingleThreadExecutor().submit(() -> {
          db.playlistDao().delete(song);
          return null;
        }).get(); // Wait for the IO operations to complete
      } catch (ExecutionException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      if (song.equals(_currentSong.getValue())) {
        int newIndex = Math.max(index - 1, 0);
        _currentSong.postValue(playlist.size() > newIndex ? playlist.get(newIndex) : null);

        PlayState currentState = _playState;
        if ((currentState == PlayState.PLAYING || currentState == PlayState.PREPARING)
          && !playlist.isEmpty()) {
          next();
        } else {
          stopPlayer();
        }
      }
    });
  }

  @MainThread
  @Override
  public void playPause() {
    PlayState currentState = _playState;
    if (currentState == PlayState.PREPARING) {
      stopPlayer();
    } else if (currentState == PlayState.PLAYING) {
      pausePlayer(true);
    } else if (currentState == PlayState.PAUSE) {
      startPlayer();
    } else {
      play(_currentSong.getValue());
    }
  }


  @MainThread
  @Override
  public void startPlayer() {
    log.info("_playState:{}", _playState);
    if (_playState != PlayState.PREPARING && _playState != PlayState.PAUSE) {
      return;
    }

    if (audioFocusManager == null) {
      audioFocusManager = new AudioFocusManager(Utils.getApp(), Aop.get(AudioPlayer.class));
      AopManager.me().addSingletonObject(audioFocusManager);
    }
    if (audioFocusManager.requestAudioFocus()) {
      mediaPlayer.start();
      _playState = PlayState.PLAYING;

      // Assuming you have a method to replace the coroutine functionality
      startUpdateProgressJob();

      PlayService.showNotification(Utils.getApp(), true, _currentSong.getValue());
      if (mediaSessionManager == null) {
        initMediaSessionManager();
      }
      mediaSessionManager.updatePlaybackState();
      Utils.getApp().registerReceiver(noisyReceiver, noisyFilter);
    }
  }


  private void startUpdateProgressJob() {
    if (scheduledExecutor == null) {
      scheduledExecutor = Executors.newScheduledThreadPool(1);
    }

    // Cancel any existing task
    if (updateProgressFuture != null && !updateProgressFuture.isDone()) {
      updateProgressFuture.cancel(true);
    }

    // Schedule the task to run periodically
    updateProgressFuture = scheduledExecutor.scheduleAtFixedRate(() -> {
      if (_playState == PlayState.PLAYING) {
        _playProgress = mediaPlayer.getCurrentPosition();
      }
    }, 0, TIME_UPDATE, TimeUnit.MILLISECONDS);
  }


  private void onPlayError() {
    stopPlayer();
    ToastUtils.showLong("失败");
  }

  @MainThread
  @Override
  public void stopPlayer() {
    if (_playState == PlayState.IDLE) {
      return;
    }
    pausePlayer(true);
    mediaPlayer.reset();
    _playState = PlayState.IDLE;
  }

  @MainThread
  @Override
  public void next() {
    List<SongEntity> playlist = _playlist.getValue();
    if (playlist == null || playlist.isEmpty()) {
      return;
    }

    PlayMode mode = PlayMode.valueOf(ConfigPreferences.getPlayMode());

    if (mode == PlayMode.Shuffle) {
      play(playlist.get(new Random().nextInt(playlist.size())));
    } else if (mode == PlayMode.Single) {
      play(_currentSong.getValue());
    } else if (mode == PlayMode.Loop) {
      int position = playlist.indexOf(_currentSong.getValue()) + 1;
      if (position >= playlist.size()) {
        position = 0;
      }
      play(playlist.get(position));
    }
  }


  @MainThread
  @Override
  public void prev() {
    List<SongEntity> playlist = _playlist.getValue();
    if (playlist == null || playlist.isEmpty()) {
      return;
    }

    PlayMode mode = PlayMode.valueOf(ConfigPreferences.getPlayMode());

    if (mode == PlayMode.Shuffle) {
      play(playlist.get(new Random().nextInt(playlist.size())));
    } else if (mode == PlayMode.Single) {
      play(_currentSong.getValue());
    } else if (mode == PlayMode.Loop) {
      int position = playlist.indexOf(_currentSong.getValue()) - 1;
      if (position < 0) {
        position = playlist.size() - 1;
      }
      play(playlist.get(position));
    }
  }


  /**
   * 跳转到指定的时间位置
   *
   * @param msec 时间
   */
  @MainThread
  @Override
  public void seekTo(int msec) {
    PlayState currentState = _playState;
    if (currentState == PlayState.PLAYING || currentState == PlayState.PAUSE) {
      mediaPlayer.seekTo(msec);
      mediaSessionManager.updatePlaybackState();
      _playProgress = (long) msec;
    }
  }


  @MainThread
  @Override
  public void setVolume(float leftVolume, float rightVolume) {
    mediaPlayer.setVolume(leftVolume, rightVolume);
  }


  @MainThread
  @Override
  public long getAudioPosition() {
    PlayState currentState = _playState;
    if (currentState == PlayState.PLAYING || currentState == PlayState.PAUSE) {
      return (long) mediaPlayer.getCurrentPosition();
    } else {
      return 0;
    }
  }


  @MainThread
  @Override
  public int getAudioSessionId() {
    return mediaPlayer.getAudioSessionId();
  }


  @MainThread
  @Override
  public void pausePlayer(boolean abandonAudioFocus) {
    if (_playState != PlayState.PREPARING) {
      return;
    }
    mediaPlayer.pause();
    _playState = (PlayState.PAUSE);

    SongEntity currentSong = _currentSong.getValue();
    if (currentSong != null) {
      PlayService.showNotification(Utils.getApp(), false, currentSong);
    } else {
      PlayService.cancelNotification(Utils.getApp());
    }
    mediaSessionManager.updatePlaybackState();
    try {
      Utils.getApp().unregisterReceiver(noisyReceiver);
    } catch (Exception e) {
      log.info("unregisterReceiver noisyReceiver fail:{}", e.getMessage());
    }

    if (abandonAudioFocus) {
      if (audioFocusManager == null) {
        initAudioFocusManager();
      }
      audioFocusManager.abandonAudioFocus();
    }
  }

  private class AddAndPlayTask extends AsyncTask<SongEntity, Void, List<SongEntity>> {

    @Override
    protected List<SongEntity> doInBackground(SongEntity... songs) {
      List<SongEntity> newPlaylist = Arrays.asList(songs);
      updateDb(newPlaylist); // 假设 updateDb 不会更新UI
      return newPlaylist;
    }

    @Override
    protected void onPostExecute(List<SongEntity> newPlaylist) {
      _playlist.setValue(newPlaylist); // 在主线程上更新UI
      play(_playlist.getValue().get(0));
    }
  }
}
