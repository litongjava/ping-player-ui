package com.litongjava.ping.player.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.litongjava.android.view.inject.annotation.FindViewById;
import com.litongjava.android.view.inject.annotation.FindViewByIdLayout;
import com.litongjava.android.view.inject.utils.ViewInjectUtils;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.ping.player.player.AudioPlayer;
import com.litongjava.ping.player.ui.R;
import com.litongjava.ping.player.ui.widget.AlbumCoverView;

import me.wcy.lrcview.LrcView;

@FindViewByIdLayout(R.layout.activity_playing)
public class PlayingActivity extends AppCompatActivity {
  private static final String TAG = "PlayingActivity";
  private static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";

  private AudioManager mAudioManager;

  AudioPlayer audioPlayer= Aop.get(AudioPlayer.class);

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

  @FindViewById(R.id.sb_progress)
  private SeekBar sbProgress;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //setContentView(R.layout.activity_playing);
    ViewInjectUtils.injectActivity(this, this);
    // 在onCreate或其他合适的初始化方法中
    mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

//    initTitle();
    initVolume();
    initCover();
    initLrc();
    switchCoverLrc(true);
    initPlayControl();
//    updatePlayMode();
//    initData();
    // 初始化组件和数据
    // 这里你需要将Kotlin代码中的初始化函数转换为Java，并添加到这里

    // 注册生命周期观察者，如果需要的话
    //getLifecycle().addObserver(this);
  }

  /**
   * 我们初始化封面视图（似乎是一个音乐播放的封面或唱片旋转视图）并为其设置了一个点击监听器。
   * 点击封面时，会调用switchCoverLrc(false)来切换显示封面或歌词。
   */
  private void initCover() {
    albumCoverView.initNeedle(audioPlayer.getPlayState().isPlaying());
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
      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }

    });
  }

  /**
   * 始化了歌词视图lrcView。首先，我们设置歌词视图为可拖动，并为其指定了一个拖动监听器。
   * 当用户在歌词视图上拖动时，音乐会跳到指定的时间点。接下来，我们为歌词视图设置了一个点击监听器，当用户点击歌词时，将显示封面
   */
  private void initLrc() {
    lrcView.setDraggable(true, new LrcView.OnPlayClickListener() {
      @Override
      public boolean onPlayClick(LrcView view, long time) {
        if (audioPlayer.getPlayState().isPlaying() || audioPlayer.getPlayState().isPausing()) {
          audioPlayer.seekTo((int) time);
          if (audioPlayer.getPlayState().isPausing()) {
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
//        switchPlayMode();
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

    //... 这里需要填写OnSeekBarChangeListener的三个方法
    sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }

    });

    //... 这里需要填写OnSeekBarChangeListener的三个方法

  }

//  private void switchPlayMode() {
//    PlayMode mode = PlayMode.valueOf(ConfigPreferences.getPlayMode());
//    switch (mode) {
//      case Loop:
//        mode = PlayMode.Shuffle;
//        Toast.makeText(this, R.string.play_mode_shuffle, Toast.LENGTH_SHORT).show();
//        break;
//      case Shuffle:
//        mode = PlayMode.Single;
//        Toast.makeText(this, R.string.play_mode_single, Toast.LENGTH_SHORT).show();
//        break;
//      case Single:
//        mode = PlayMode.Loop;
//        Toast.makeText(this, R.string.play_mode_loop, Toast.LENGTH_SHORT).show();
//        break;
//    }
//    ConfigPreferences.setPlayMode(mode.getValue());
//    updatePlayMode();
//  }



  private BroadcastReceiver volumeReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      //viewBinding.sbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }
  };


  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(volumeReceiver);
  }
}
