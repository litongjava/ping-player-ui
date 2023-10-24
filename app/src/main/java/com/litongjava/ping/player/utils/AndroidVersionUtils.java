package com.litongjava.ping.player.utils;

import android.os.Build;

import com.blankj.utilcode.util.AppUtils;

public class AndroidVersionUtils {

  public static boolean isAboveOrEqual(int version) {
    return Build.VERSION.SDK_INT >= version;
  }

  public static boolean isTargetAboveOrEqual(int version) {
    return AppUtils.getAppTargetSdkVersion() >= version;
  }

  public static boolean isAboveOrEqual6() {
    return isAboveOrEqual(Build.VERSION_CODES.M);
  }

  public static boolean isAboveOrEqual7() {
    return isAboveOrEqual(Build.VERSION_CODES.N);
  }

  public static boolean isAboveOrEqual8() {
    return isAboveOrEqual(Build.VERSION_CODES.O);
  }

  public static boolean isAboveOrEqual9() {
    return isAboveOrEqual(Build.VERSION_CODES.P);
  }

  public static boolean isAboveOrEqual10() {
    return isAboveOrEqual(Build.VERSION_CODES.Q);
  }

  public static boolean isAboveOrEqual11() {
    return isAboveOrEqual(Build.VERSION_CODES.R);
  }

  public static boolean isAboveOrEqual12() {
//    return isAboveOrEqual(Build.VERSION_CODES.S);
    return false;
  }

  // Note: Android 13 (TIRAMISU) is not officially released yet, so the following might throw errors
  public static boolean isAboveOrEqual13() {
//    return isAboveOrEqual(Build.VERSION_CODES.TIRAMISU);
    return false;
  }

  public static boolean isTargetAboveOrEqual13() {
//    return isTargetAboveOrEqual(Build.VERSION_CODES.TIRAMISU);
    return false;
  }
}
