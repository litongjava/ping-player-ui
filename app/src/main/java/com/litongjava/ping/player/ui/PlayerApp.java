package com.litongjava.ping.player.ui;

import android.app.Application;
import android.content.Context;

import com.litongjava.jfinal.aop.Aop;
import com.litongjava.jfinal.aop.AopManager;
import com.litongjava.ping.player.player.AudioFocusManager;
import com.litongjava.ping.player.player.AudioPlayer;
import com.litongjava.ping.player.player.AudioPlayerImpl;
import com.litongjava.ping.player.player.MediaSessionManager;
import com.litongjava.ping.player.storage.db.MusicDatabase;
import com.litongjava.ping.player.storage.db.entity.DatabaseModule;
import com.litongjava.ping.player.storage.db.entity.SongEntity;
import com.litongjava.ping.player.test.TestSongEntity;

import java.util.List;

public class PlayerApp extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    initAopBean();
  }

  private void initAopBean() {
    Context context = getBaseContext();
    MusicDatabase musicDatabase = new DatabaseModule().provideAppDatabase();
    AudioPlayer audioPlayer = new AudioPlayerImpl(musicDatabase);
    AopManager.me().addMapping(AudioPlayer.class, audioPlayer.getClass());
    AopManager.me().addSingletonObject(audioPlayer);

    //模拟测试
//    audioPlayer.startPlayer();
//    List<SongEntity> playList = TestSongEntity.getPlayList();
//    audioPlayer.replaceAll(playList, playList.get(0));
//    audioPlayer.addAndPlay(playList.get(0));
  }
}
