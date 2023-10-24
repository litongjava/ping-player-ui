package com.litongjava.ping.player.player;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.litongjava.ping.player.common.Permissioner;
import com.litongjava.ping.player.revicer.StatusBarReceiver;
import com.litongjava.ping.player.storage.db.entity.SongEntity;
import com.litongjava.ping.player.ui.R;


public class PlayService extends Service {

  public static final String EXTRA_NOTIFICATION = "me.wcy.music.notification";
  private static final String TAG = "Service";
  private static final int NOTIFICATION_ID = 0x111;
  private static final String ACTION_SHOW_NOTIFICATION = "me.wcy.music.ACTION_SHOW_NOTIFICATION";
  private static final String ACTION_CANCEL_NOTIFICATION = "me.wcy.music.ACTION_CANCEL_NOTIFICATION";

  private StatusBarReceiver notificationReceiver;
  private boolean isChannelCreated = false;

  public class PlayBinder extends Binder {
    public PlayService getService() {
      return PlayService.this;
    }
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.i(TAG, "onCreate: " + getClass().getSimpleName());
    createNotificationChannel();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return new PlayBinder();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent == null) return START_NOT_STICKY;
    String action = intent.getAction();
    if (action != null) {
      switch (action) {
        case ACTION_SHOW_NOTIFICATION:
          boolean isPlaying = intent.getBooleanExtra("is_playing", false);
          SongEntity music = intent.getParcelableExtra("music");
          if (music != null) {
            showNotification(Utils.getApp(), isPlaying, music);
          }
          break;

        case ACTION_CANCEL_NOTIFICATION:
          cancelNotification();
          break;
      }
    }
    return START_NOT_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    cancelNotification();
    unregisterNotificationReceiver();
  }


  public static void showNotification(Context context, boolean isPlaying, SongEntity music) {
    Intent intent = new Intent(context, PlayService.class);
    intent.setAction(ACTION_SHOW_NOTIFICATION);
    intent.putExtra("is_playing", isPlaying);
    intent.putExtra("music", music);
    context.startService(intent);
  }

  public static void cancelNotification(Context context) {
    Intent intent = new Intent(context, PlayService.class);
    intent.setAction(ACTION_CANCEL_NOTIFICATION);
    context.startService(intent);
  }

  public void cancelNotification() {
    NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
  }

  public void unregisterNotificationReceiver() {
    unregisterReceiver(notificationReceiver);
  }

  private void createNotificationChannel() {
//    if (!isChannelCreated && Permissioner.hasNotificationPermission(this)) {
//      isChannelCreated = true;
//      String name = StringUtils.getString(R.string.app_name);
//      String descriptionText = "音乐通知栏";
//      int importance = NotificationManagerCompat.IMPORTANCE_LOW;
//      NotificationChannelCompat.Builder mChannelBuilder = new NotificationChannelCompat.Builder(String.valueOf(NOTIFICATION_ID), importance)
//        .setName(name)
//        .setDescription(descriptionText)
//        .setVibrationEnabled(false);
//      NotificationChannelCompat mChannel = mChannelBuilder.build();
//      NotificationManagerCompat.from(this).createNotificationChannel(mChannel);
//      registerNotificationReceiver();
//    }
  }

}
