package com.litongjava.ping.player.ui.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.litongjava.ping.player.ui.R;

import java.io.IOException;

public class MediaPlayerActivity extends AppCompatActivity {

  private MediaPlayer mediaPlayer;
  private Button playButton, stopButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_media_player);

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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mediaPlayer != null) {
      mediaPlayer.release(); // 释放资源
      mediaPlayer = null;
    }
  }
}
