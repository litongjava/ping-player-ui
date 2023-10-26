package com.litongjava.ping.player.ui.server;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import androidx.lifecycle.LiveData;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.ping.player.player.AudioPlayer;
import com.litongjava.ping.player.player.PlayState;
import com.litongjava.ping.player.storage.db.entity.SongEntity;
import com.litongjava.ping.player.test.TestSongEntity;
import com.litongjava.ping.player.ui.activity.PlayingActivity;

import org.tio.core.ChannelContext;

public class MessageDispatcher {
  public String processMessage(String msg, ChannelContext channelContext) {
    String[] msgArray = msg.split(" ");

    if ("version".equals(msgArray[0])) {
      return version();
    } else if ("toast".equals(msgArray[0])) {
      return toast(msgArray);
    } else if ("env".equals(msgArray[0])) {
      return env(msgArray);
    } else if ("play-music".equals(msgArray[0])) {
      return playMusic(msgArray);
    } else if ("start-player".equals(msgArray[0])) {
      return startPlayer(msgArray);
    } else if ("play-info".equals(msgArray[0])) {
      return palyInfo(msgArray);
    } else if ("play-pause".equals(msgArray[0])) {
      return playPause(msgArray);
    }else if("update-ui".equals(msgArray[0])){
      return updateUI(msgArray);
    }
    return "index";
  }

  private String updateUI(String[] msgArray) {
    return null;
  }

  private String playPause(String[] msgArray) {
    Aop.get(AudioPlayer.class).playPause();
    return "0";
  }

  private String palyInfo(String[] msgArray) {
    AudioPlayer audioPlayer = Aop.get(AudioPlayer.class);
    SongEntity song = audioPlayer.getCurrentSong().getValue();
    PlayState playState = audioPlayer.getPlayState().getValue();
    Integer playProgress = audioPlayer.getPlayProgress().getValue();
    int audioSessionId = audioPlayer.getAudioSessionId();
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("song", song);
    jsonObject.put("playState",playState);
    jsonObject.put("playProgress",playProgress);
    jsonObject.put("audioSessionId",audioSessionId);
    return JSON.toJSONString(jsonObject);
  }

  private String startPlayer(String[] msgArray) {
    Application app = Utils.getApp();
    Intent intent = new Intent(app, PlayingActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    try {
      app.startActivity(intent);
    } catch (Exception e) {
      return e.getLocalizedMessage();
    }
    return "success";
  }

  private String playMusic(String[] msgArray) {
    SongEntity song = TestSongEntity.getSong(0L);
    Aop.get(AudioPlayer.class).addAndPlay(song);
    return "success";
  }

  private String env(String[] msgArray) {
    Application app = Utils.getApp();
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("app", app.toString());
    return JSON.toJSONString(jsonObject);
  }

  private String toast(String[] msgArray) {
    ToastUtils.showLong(msgArray[1]);
    return "success";
  }

  public String version() {
    String release = Build.VERSION.RELEASE; // 获取Android版本名称
    int sdkVersion = Build.VERSION.SDK_INT; // 获取Android版本代码
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("version", "1.0.0");
    jsonObject.put("Android version ", release);
    jsonObject.put("SDK version ", sdkVersion);
    return JSON.toJSONString(jsonObject);
  }
}
