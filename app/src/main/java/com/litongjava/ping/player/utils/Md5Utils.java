package com.litongjava.ping.player.utils;

import com.blankj.utilcode.util.EncryptUtils;

public class Md5Utils {

  public static String md5(String input) {
    return md5(input,true);
  }

  public static String md5(String input, boolean lowercase) {
    String md5Result = EncryptUtils.encryptMD5ToString(input);
    if (lowercase) {
      return md5Result.toLowerCase();
    } else {
      return md5Result.toUpperCase();
    }
  }
}
