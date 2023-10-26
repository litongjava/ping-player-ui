package com.litongjava.ping.player.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;


import com.litongjava.android.utils.acp.AcpUtils;
import com.litongjava.android.utils.toast.ToastUtils;
import com.litongjava.android.view.inject.annotation.FindViewById;
import com.litongjava.android.view.inject.annotation.FindViewByIdLayout;
import com.litongjava.android.view.inject.utils.ViewInjectUtils;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.ping.player.lrc.LrcCache;
import com.litongjava.ping.player.model.CommonResult;
import com.litongjava.ping.player.player.AudioPlayer;
import com.litongjava.ping.player.player.PlayMode;
import com.litongjava.ping.player.player.PlayState;
import com.litongjava.ping.player.storage.db.entity.SongEntity;
import com.litongjava.ping.player.storage.preferences.ConfigPreferences;
import com.litongjava.ping.player.ui.R;
import com.litongjava.ping.player.ui.widget.AlbumCoverView;
import com.litongjava.ping.player.utils.ImageLoadCallback;
import com.litongjava.ping.player.utils.ImageUtils;
import com.litongjava.ping.player.utils.TimeUtils;
import com.mylhyl.acp.AcpListener;
import com.xiaoleilu.hutool.log.LogFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import jp.wasabeef.blurry.Blurry;
import me.wcy.lrcview.LrcView;

import static com.litongjava.ping.player.player.PlayMode.Loop;
import static com.litongjava.ping.player.player.PlayMode.Shuffle;
import static com.litongjava.ping.player.player.PlayMode.Single;

@FindViewByIdLayout(R.layout.activity_playing)
public class PlayingActivity extends AppCompatActivity {
  private static final String TAG = "PlayingActivity";
  private static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";

  @FindViewById(R.id.iv_back)
  private ImageView ivBack;
  @FindViewById(R.id.ivPlayingBg)
  private ImageView ivPlayingBg;
  @FindViewById(R.id.sb_volume)
  private SeekBar sbVolume;
  @FindViewById(R.id.album_cover_view)
  private AlbumCoverView albumCoverView;
  @FindViewById(R.id.lrcLayout)
  private LinearLayout lrcLayout;
  @FindViewById(R.id.lrc_view)
  private LrcView lrcView;
  @FindViewById(R.id.iv_mode)
  private ImageView ivMode;
  @FindViewById(R.id.iv_prev)
  private ImageView ivPrev;
  @FindViewById(R.id.iv_play)
  private ImageView ivPlay;
  @FindViewById(R.id.iv_next)
  private ImageView ivNext;

  @FindViewById(R.id.tv_title)
  private TextView tvTitle;
  @FindViewById(R.id.tv_artist)
  private TextView tvArtist;

  @FindViewById(R.id.tv_current_time)
  private TextView tvCurrentTime;
  @FindViewById(R.id.tv_total_time)
  private TextView tvTotalTime;
  @FindViewById(R.id.sb_progress)
  private SeekBar sbProgress;


  private AudioManager mAudioManager;
  private AudioPlayer audioPlayer = Aop.get(AudioPlayer.class);

  private int mLastProgress = 0;
  private boolean isDraggingProgress = false;
  private Bitmap defaultCoverBitmap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //setContentView(R.layout.activity_playing);
    ViewInjectUtils.injectActivity(this, this);
    // 在onCreate或其他合适的初始化方法中
    mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    initPermisson();
    initTitle();
    initVolume();
    initCover();
    initLrc();
    switchCoverLrc(true);
    initPlayControl();
    updatePlayMode();
    initData();

  }

  private void initPermisson() {
    String[] permissions = {
      //写入外部设备权限
      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.READ_EXTERNAL_STORAGE
    };
    //创建acpListener
    AcpListener acpListener = new AcpListener() {
      @Override
      public void onGranted() {
        //ToastUtils.defaultToast(getApplicationContext(), "获取权限成功");
      }

      @Override
      public void onDenied(List<String> permissions) {
        ToastUtils.defaultToast(getApplicationContext(), permissions.toString() + "权限拒绝");
      }
    };
    AcpUtils.requestPermissions(this, permissions, acpListener);
  }

  private void initTitle() {
    ivBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
  }


  /**
   * 初始化音量控制滑块sbVolume的最大值和当前值。同时，我们为音量变化注册了一个广播接收器。
   * 这样，当系统音量发生变化时，应用程序会被通知，从而可以相应地更新UI或进行其他操作。
   */
  private void initVolume() {
    sbVolume.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
    sbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    IntentFilter filter = new IntentFilter(VOLUME_CHANGED_ACTION);
    // 假设你有一个registerReceiver方法或直接使用此方法
    registerReceiver(volumeReceiver, filter);

    sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // Implementation not provided in the original Kotlin code
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
        // Implementation not provided in the original Kotlin code
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == null) return;
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
      }
    });

  }

  /**
   * 我们初始化封面视图（似乎是一个音乐播放的封面或唱片旋转视图）并为其设置了一个点击监听器。
   * 点击封面时，会调用switchCoverLrc(false)来切换显示封面或歌词。
   */
  private void initCover() {
    albumCoverView.initNeedle(audioPlayer.getPlayState().getValue().isPlaying());
    albumCoverView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        switchCoverLrc(false);
      }
    });
  }

  private void switchCoverLrc(boolean showCover) {
    albumCoverView.setVisibility(showCover ? View.VISIBLE : View.GONE);
    lrcLayout.setVisibility(showCover ? View.GONE : View.VISIBLE);
  }


  /**
   * 始化了歌词视图lrcView。首先，我们设置歌词视图为可拖动，并为其指定了一个拖动监听器。
   * 当用户在歌词视图上拖动时，音乐会跳到指定的时间点。接下来，我们为歌词视图设置了一个点击监听器，当用户点击歌词时，将显示封面
   */
  private void initLrc() {
    lrcView.setDraggable(true, new LrcView.OnPlayClickListener() {
      @Override
      public boolean onPlayClick(LrcView view, long time) {
        if (audioPlayer.getPlayState().getValue().isPlaying() || audioPlayer.getPlayState().getValue().isPausing()) {
          audioPlayer.seekTo((int) time);
          if (audioPlayer.getPlayState().getValue().isPausing()) {
            audioPlayer.playPause();
          }
          return true;
        }
        return false;
      }
    });

    lrcView.setOnTapListener(new LrcView.OnTapListener() {
      @Override
      public void onTap(LrcView view, float x, float y) {
        switchCoverLrc(true);
      }
    });
  }

  /**
   * 此方法主要负责初始化播放控制按钮（如播放、暂停、上一曲、下一曲、切换播放模式）的点击监听器。同时，它还设置了进度条和音量控制滑块的监听器。
   */
  private void initPlayControl() {
    ivMode.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        switchPlayMode();
      }
    });

    ivPlay.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        audioPlayer.playPause();
      }
    });

    ivPrev.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        audioPlayer.prev();
      }
    });

    ivNext.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        audioPlayer.next();
      }
    });

    sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (Math.abs(progress - mLastProgress) >= DateUtils.SECOND_IN_MILLIS) {
          tvCurrentTime.setText(TimeUtils.formatTime("mm:ss", (long) progress));
          mLastProgress = progress;
        }
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
        isDraggingProgress = true;
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == null) return;
        isDraggingProgress = false;
        if (audioPlayer.getPlayState().getValue().isPlaying() || audioPlayer.getPlayState().getValue().isPausing()) {
          int progress = seekBar.getProgress();
          audioPlayer.seekTo(progress);
          if (lrcView.hasLrc()) {
            lrcView.updateTime((long) progress);
          }
        } else {
          seekBar.setProgress(0);
        }
      }

    });

  }

  private void switchPlayMode() {
    PlayMode mode = PlayMode.valueOf(ConfigPreferences.getPlayMode());
    if (Loop.equals(mode)) {
      mode = Shuffle;
      Toast.makeText(this, R.string.play_mode_shuffle, Toast.LENGTH_SHORT).show();
    } else if (Shuffle.equals(mode)) {
      mode = Single;
      Toast.makeText(this, R.string.play_mode_single, Toast.LENGTH_SHORT).show();
    } else if (Single.equals(mode)) {
      mode = Loop;
      Toast.makeText(this, R.string.play_mode_loop, Toast.LENGTH_SHORT).show();
    }
    ConfigPreferences.setPlayMode(mode.getValue());
    updatePlayMode();
  }

  private void updatePlayMode() {
    PlayMode mode = PlayMode.valueOf(ConfigPreferences.getPlayMode());
    if (Loop.equals(mode)) {
      mode = Shuffle;
      toast(R.string.play_mode_shuffle);
    } else if (Shuffle.equals(mode)) {
      mode = Single;
      toast(R.string.play_mode_single);
    } else if (Single.equals(mode)) {
      mode = Loop;
      toast(R.string.play_mode_loop);
    }
    ConfigPreferences.setPlayMode(mode.getValue());
    ivMode.setImageLevel(mode.getValue());
  }

  private void initData() {
    audioPlayer.getPlayState().observe(this, (playState) -> {
      if (playState.isPlaying() || playState.isPreparing()) {
        ivPlay.setSelected(true);
        albumCoverView.start();
      } else {
        ivPlay.setSelected(false);
        albumCoverView.pause();
      }
    });
    audioPlayer.getPlayProgress().observe(this,(progress)->{
      if (!isDraggingProgress) {
        sbProgress.setProgress(progress);
      }
      if (lrcView.hasLrc()) {
        lrcView.updateTime(progress);
      }
    });
    audioPlayer.getBufferingPercent().observe(this,(percent)->{
      sbProgress.setSecondaryProgress(sbProgress.getMax() * percent / 100);
    });

    audioPlayer.getCurrentSong().observe(this, new Observer<SongEntity>() {
      Logger log = LoggerFactory.getLogger(this.getClass());

      @Override
      public void onChanged(SongEntity song) {
        log.info("getCurrentSong changed:{}", song);
        if (song != null) {
          tvTitle.setText(song.getTitle());
          tvArtist.setText(song.getArtist());
          sbProgress.setProgress(audioPlayer.getPlayProgress().getValue());
          sbProgress.setSecondaryProgress(0);
          sbProgress.setMax(song.getDuration().intValue());
          mLastProgress = 0;
          tvCurrentTime.setText(R.string.play_time_start);
          tvTotalTime.setText(TimeUtils.formatTime("mm:ss", song.getDuration()));
          updateCover(song);
          updateLrc(song);
          if (audioPlayer.getPlayState().getValue().isPlaying() || audioPlayer.getPlayState().getValue().isPreparing()) {
            ivPlay.setSelected(true);
            albumCoverView.start();
          } else {
            ivPlay.setSelected(false);
            albumCoverView.pause();
          }
        } else {
          finish();
        }
      }
    });
  }

  private void updateCover(SongEntity song) {
    albumCoverView.setCoverBitmap(defaultCoverBitmap);
    ivPlayingBg.setImageResource(R.drawable.bg_playing_default);
    String albumCover = song.getAlbumCover();
    if (albumCover == null) {
      return;
    }
    ImageUtils.loadBitmap(albumCover, new ImageLoadCallback() {
      @Override
      public void onResult(CommonResult<Bitmap> result) {
        albumCoverView.setCoverBitmap(result.getData());
        Blurry.with(PlayingActivity.this).from(result.getData()).into(ivPlayingBg);
      }
    });
  }


  private void updateLrc(SongEntity song) {
//    if (loadLrcJob != null) {
//      loadLrcJob.cancel();
//      loadLrcJob = null;
//    }
    String lrcPath = LrcCache.getLrcFilePath(song);
    if (lrcPath != null && !lrcPath.isEmpty()) {
      loadLrc(lrcPath);
      return;
    }
    if (song.isLocal()) {
      setLrcLabel("暂无歌词");
    } else {
      setLrcLabel("歌词加载中…");
      //从网络加载歌词,咱不支持
//      loadLrcJob = executorService.submit(() -> {
//        try {
//          LrcWrap lrcWrap = DiscoverApi.get().getLrc(song.getSongId());
//          if (lrcWrap.getCode() == 200 && lrcWrap.getLrc().isValid()) {
//            File file = LrcCache.saveLrcFile(song, lrcWrap.getLrc().getLyric());
//            loadLrc(file.getPath());
//          } else {
//            throw new IllegalStateException("lrc is invalid");
//          }
//        } catch (Exception e) {
//          Log.e(TAG, "load lrc error", e);
//          setLrcLabel("歌词加载失败");
//        }
//      });

    }
  }

  private void loadLrc(String path) {
    File file = new File(path);
    lrcView.loadLrc(file);
  }

  private void setLrcLabel(String label) {
    lrcView.setLabel(label);
  }


  private Bitmap getDefaultCoverBitmap() {
    if (defaultCoverBitmap == null) {
      defaultCoverBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_playing_default_cover);
    }
    return defaultCoverBitmap;
  }

  private void toast(int messageId) {
    Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
  }


  private BroadcastReceiver volumeReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      sbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }
  };


  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(volumeReceiver);
  }
}
