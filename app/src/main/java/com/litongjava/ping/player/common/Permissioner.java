package com.litongjava.ping.player.common;

import android.Manifest;
import android.content.Context;

import androidx.annotation.MainThread;

import com.litongjava.ping.player.utils.AndroidVersionUtils;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Special;
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener;

public class Permissioner {

  public static boolean hasNotificationPermission(Context context) {
    if (AndroidVersionUtils.isAboveOrEqual13() && AndroidVersionUtils.isTargetAboveOrEqual13()) {
      //return SoulPermission.getInstance().checkPermissions(Manifest.permission.POST_NOTIFICATIONS);
      return false;
    } else {
      return SoulPermission.getInstance().checkSpecialPermission(Special.NOTIFICATION);
    }
  }


  public static void goApplicationSettings(Context context, Runnable onBack) {
    try {
      SoulPermission.getInstance().goApplicationSettings(data -> onBack.run());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @MainThread
  public static void requestPermission(Context context, String permission, PermissionCallback callback) {
    SoulPermission.getInstance().checkAndRequestPermission(permission, new CheckRequestPermissionListener() {
      @Override
      public void onPermissionOk(Permission permission) {
        callback.invoke(true, permission.shouldRationale());
      }

      @Override
      public void onPermissionDenied(Permission permission) {
        callback.invoke(false, permission.shouldRationale());
      }
    });
  }

  // ... (Other methods remain the same with similar pattern)

  public interface PermissionCallback {
    void invoke(boolean isGranted, boolean shouldRationale);
  }
}
