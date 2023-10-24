package com.litongjava.ping.player.revicer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.litongjava.jfinal.aop.Aop;
import com.litongjava.ping.player.player.AudioPlayer;


/**
 * Created by wcy on 2017/4/18.
 */
public class StatusBarReceiver extends BroadcastReceiver {

  AudioPlayer audioPlayer = Aop.get(AudioPlayer.class);

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent != null) {
      String extra = intent.getStringExtra(EXTRA);
      if (EXTRA_NEXT.equals(extra)) {
        audioPlayer.next();
      } else if (EXTRA_PLAY_PAUSE.equals(extra)) {
        audioPlayer.playPause();
      }
    }
  }

  public static final String ACTION_STATUS_BAR = "me.wcy.music.STATUS_BAR_ACTIONS";
  public static final String EXTRA = "extra";
  public static final String EXTRA_NEXT = "next";
  public static final String EXTRA_PLAY_PAUSE = "play_pause";
}
