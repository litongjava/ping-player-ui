package com.litongjava.ping.player.revicer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.litongjava.jfinal.aop.Aop;
import com.litongjava.ping.player.player.AudioPlayer;

public class NoisyAudioStreamReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    Aop.get(AudioPlayer.class).playPause();
  }
}


