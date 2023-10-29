package com.litongjava.ping.player.player;

import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class AudioPlayerImpl implements AudioPlayer {
  private final Logger log = LoggerFactory.getLogger(this.getClass());
  private static final long TIME_UPDATE = 300L;
  private MutableLiveData<List<SongEntity>> _playlist = new MutableLiveData<>();
  private MutableLiveData<SongEntity> _currentSong = new MutableLiveData<>();
  private MutableLiveData<PlayState> _playState = new MutableLiveData<>(PlayState.IDLE);
  private MutableLiveData<Integer> _playProgress = new MutableLiveData<>(0);
  private MutableLiveData<Integer> _bufferingPercent = new MutableLiveData<>(0);


  private MusicDatabase db;

  private MediaPlayer mediaPlayer = Aop.get(MediaPlayer.class);
  private MediaSessionManager mediaSessionManager;
  private AudioFocusManager audioFocusManager;
  private NoisyAudioStreamReceiver noisyReceiver = Aop.get(NoisyAudioStreamReceiver.class);
  private IntentFilter noisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

  private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
  private ScheduledFuture<?> updateProgressFuture;


  public AudioPlayerImpl(MusicDatabase db) {
    this.db = db;
    initMediaSessionManager();
    initAudioFocusManager();
  }

  private void initMediaSessionManager() {
    mediaSessionManager = new MediaSessionManager(Utils.getApp(), this);
    AopManager.me().addSingletonObject(mediaSessionManager);
  }

  private void initAudioFocusManager() {
    audioFocusManager = new AudioFocusManager(Utils.getApp(), this);
    AopManager.me().addSingletonObject(audioFocusManager);
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
  public LiveData<PlayState> getPlayState() {
    return _playState;
  }

  @Override
  public MutableLiveData<Integer> getPlayProgress() {
    return _playProgress;
  }

  @Override
  public LiveData<Integer> getBufferingPercent() {
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

  private void clearDb() {
    ExecutorService ioExecutor = Executors.newSingleThreadExecutor();
    try {
      ioExecutor.submit(() -> {
        db.playlistDao().clear();
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
    _playProgress.setValue(0);
    _bufferingPercent.setValue(0);
    _playState.setValue(PlayState.PREPARING);

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
      //调用startPlayer进行播放
      startPlayer();
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
      if (index > 0) {
        SongEntity removed = playlist.remove(index);
        _playlist.postValue(playlist);
        // Execute database operations on an IO thread
        updateDb(removed);
        updatePlay(removed, playlist, index);
      }
    });
  }

  @Override
  public void delete(Long songId) {
    Executors.newSingleThreadExecutor().submit(() -> {
      List<SongEntity> playlist = new ArrayList<>(_playlist.getValue());
      int index = 0;
      for (int i = 0; i < playlist.size(); i++) {
        if (playlist.get(i).getSongId() == songId) {
          index = i;
        }
      }

      if (index > 0) {
        SongEntity removed = playlist.remove(index);
        _playlist.postValue(playlist);
        // Execute database operations on an IO thread
        updateDb(removed);
        updatePlay(removed, playlist, index);
      }

    });
  }

  private void updatePlay(SongEntity song, List<SongEntity> playlist, int index) {
    if (song.equals(_currentSong.getValue())) {
      int newIndex = Math.max(index - 1, 0);
      _currentSong.postValue(playlist.size() > newIndex ? playlist.get(newIndex) : null);

      PlayState currentState = _playState.getValue();
      if ((currentState == PlayState.PLAYING || currentState == PlayState.PREPARING)
        && !playlist.isEmpty()) {
        next();
      } else {
        stopPlayer();
      }
    }
  }

  private void updateDb(SongEntity song) {
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
  }

  @MainThread
  @Override
  public void playPause() {
    PlayState currentPlayState = _playState.getValue();
    if (currentPlayState == PlayState.PREPARING) {
      log.info("stop");
      stopPlayer();
    } else if (currentPlayState == PlayState.PLAYING) {
      log.info("pause");
      pausePlayer(true);
    } else if (currentPlayState == PlayState.PAUSE) {
      log.info("start");
      startPlayer();
    } else {
      log.info("play");
      play(_currentSong.getValue());
    }
  }


  @MainThread
  @Override
  public void startPlayer() {
    log.info("start player _playState:{}", _playState);
    if (_playState.getValue() != PlayState.PREPARING && _playState.getValue() != PlayState.PAUSE) {
      return;
    }


    boolean requestAudioFocus = audioFocusManager.requestAudioFocus();
    log.info("requestAudioFocus:{}", requestAudioFocus);
    if (requestAudioFocus) {
      mediaPlayer.start();
      _playState.setValue(PlayState.PLAYING);

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
    log.info("startUpdateProgressJob:{}", updateProgressFuture);
    // Cancel any existing task
    if (updateProgressFuture != null && !updateProgressFuture.isDone()) {
      updateProgressFuture.cancel(true);
    }

//    updateProgressFuture = scheduledExecutor.scheduleWithFixedDelay(() -> {
//      int currentPosition = mediaPlayer.getCurrentPosition();
//      log.info("currentPosition:{}", currentPosition);
//      _playProgress.setValue(currentPosition);
//    }, 0, TIME_UPDATE, TimeUnit.MILLISECONDS);
    final int delay = 1000;
    Runnable updateProgressTask = new Runnable() {
      @Override
      public void run() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
          int currentPosition = mediaPlayer.getCurrentPosition();
          //log.info("currentPosition:{}", currentPosition);
          _playProgress.setValue(currentPosition);
        }
        Aop.get(Handler.class).postDelayed(this, TIME_UPDATE);
      }
    };
    Aop.get(Handler.class).postDelayed(updateProgressTask, TIME_UPDATE);

  }


  private void onPlayError() {
    stopPlayer();
    ToastUtils.showLong("失败");
  }

  @MainThread
  @Override
  public void stopPlayer() {
    if (_playState.getValue() == PlayState.IDLE) {
      return;
    }
    pausePlayer(true);
    mediaPlayer.reset();
    _playState.setValue(PlayState.IDLE);
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
    if (_playState.getValue() == PlayState.PLAYING || _playState.getValue() == PlayState.PAUSE) {
      mediaPlayer.seekTo(msec);
      mediaSessionManager.updatePlaybackState();
      _playProgress.setValue(msec);
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
    if (_playState.getValue() == PlayState.PLAYING || _playState.getValue() == PlayState.PAUSE) {
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

  @Override
  public void clearPlayList() {
    _playlist.setValue(new ArrayList<>());
    this.clearDb();

  }

  @MainThread
  @Override
  public void pausePlayer(boolean abandonAudioFocus) {
    log.info("play state:{}", _playState);
    if (_playState.getValue() != PlayState.PLAYING) {
      return;
    }
    mediaPlayer.pause();
    _playState.setValue(PlayState.PAUSE);

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
