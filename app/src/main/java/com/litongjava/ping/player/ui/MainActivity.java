package com.litongjava.ping.player.ui;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.litongjava.android.utils.acp.AcpUtils;
import com.litongjava.android.utils.toast.ToastUtils;
import com.litongjava.android.view.inject.annotation.FindViewByIdLayout;
import com.litongjava.android.view.inject.annotation.OnClick;
import com.litongjava.android.view.inject.utils.ViewInjectUtils;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.ping.player.player.AudioPlayer;
import com.litongjava.ping.player.storage.db.entity.SongEntity;
import com.litongjava.ping.player.test.TestSongEntity;
import com.litongjava.ping.player.tio.hello.TioServerService;
import com.litongjava.ping.player.ui.activity.PlayingActivity;
import com.mylhyl.acp.AcpListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@FindViewByIdLayout(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
  private Logger log = LoggerFactory.getLogger(this.getClass());

  private MediaPlayer mediaPlayer;
  private Button playButton, stopButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //setContentView(R.layout.activity_media_player);
    ViewInjectUtils.injectActivity(this, this);
    playButton = findViewById(R.id.playButton);
    stopButton = findViewById(R.id.stopButton);

    // 初始化MediaPlayer对象
    mediaPlayer = new MediaPlayer();

    playButton.setOnClickListener(v -> {
      try {
        // 设置数据源为你的mp3文件的路径
        mediaPlayer.setDataSource("/storage/emulated/0/Music/开言英语/A1_APartyInvitation__lesson_1368784253.mp3");
        mediaPlayer.prepare(); // 准备播放
        mediaPlayer.start();   // 开始播放
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    stopButton.setOnClickListener(v -> {
      if (mediaPlayer.isPlaying()) {
        mediaPlayer.stop();
        mediaPlayer.reset();  // 重置MediaPlayer以便再次使用
      }
    });
  }

  @OnClick(R.id.startPlayerBtn)
  public void startPlayerBtn_OnClick(View v) {
    Intent intent = new Intent(this, PlayingActivity.class);
    startActivity(intent);
  }

  @OnClick(R.id.addAndPlayBtn)
  public void addAndPlayBtn_OnClick(View v) {
    SongEntity song = TestSongEntity.getSong(0L);
    Aop.get(AudioPlayer.class).addAndPlay(song);
  }

  @OnClick(R.id.btnSendBroadcastToStatic)
  public void btnSendBroadcast_OnClick(View view) {

    log.info("发送广播");
    Intent intent = new Intent();
    intent.setAction("com.litongjava.ping.player.revicer.MyFirstCustomRecevier");
    super.sendBroadcast(intent);
  }

  @OnClick(R.id.btnStartServer)
  public void btnStartServer_OnClick(View v) {
    String[] permissions = {
      //写入外部设备权限
      Manifest.permission.ACCESS_NETWORK_STATE,
      Manifest.permission.INTERNET,
    };
    //创建acpListener
    AcpListener acpListener = new AcpListener() {
      @Override
      public void onGranted() {
        log.info("开始启动服务器:");
        Aop.get(TioServerService.class).startTioServer();
      }

      @Override
      public void onDenied(List<String> permissions) {
        ToastUtils.defaultToast(getApplicationContext(), permissions.toString() + "权限拒绝,无法写入日志");
      }
    };

    AcpUtils.requestPermissions(this, permissions, acpListener);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mediaPlayer != null) {
      mediaPlayer.release(); // 释放资源
      mediaPlayer = null;
    }
  }
}
