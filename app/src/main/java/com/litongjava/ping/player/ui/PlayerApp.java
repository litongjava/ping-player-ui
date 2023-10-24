package com.litongjava.ping.player.ui;

import android.app.Application;
import android.content.Context;

import com.litongjava.jfinal.aop.Aop;
import com.litongjava.jfinal.aop.AopManager;
import com.litongjava.ping.player.player.AudioPlayer;
import com.litongjava.ping.player.player.AudioPlayerImpl;
import com.litongjava.ping.player.storage.db.MusicDatabase;
import com.litongjava.ping.player.storage.db.entity.DatabaseModule;

public class PlayerApp extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    initAopBean();
  }

  private void initAopBean() {
    MusicDatabase musicDatabase = new DatabaseModule().provideAppDatabase();
    AudioPlayer audioPlayer = new AudioPlayerImpl(musicDatabase);
    AopManager.me().addMapping(AudioPlayer.class, audioPlayer.getClass());
    AopManager.me().addSingletonObject(audioPlayer);
  }
}
