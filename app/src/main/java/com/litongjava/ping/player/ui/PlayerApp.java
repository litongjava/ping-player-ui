package com.litongjava.ping.player.ui;

import android.app.Application;
import android.content.Context;

import com.litongjava.jfinal.aop.AopManager;
import com.litongjava.ping.player.player.AudioPlayer;
import com.litongjava.ping.player.player.AudioPlayerImpl;
import com.litongjava.ping.player.storage.db.MusicDatabase;
import com.litongjava.ping.player.storage.db.DatabaseModule;

public class PlayerApp extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    initAopBean();
  }

  private void initAopBean() {
    //init database
    Context context = getBaseContext();
    MusicDatabase musicDatabase = new DatabaseModule().provideAppDatabase(context);
    try {
      AopManager.me().addMapping(MusicDatabase.class, musicDatabase.getClass());
      AopManager.me().addSingletonObject(musicDatabase);
    } catch (Exception e) {
      e.printStackTrace();
    }


    //init audio player
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
