package com.litongjava.ping.player.utils;

import android.util.Log;

import com.blankj.utilcode.util.EncryptUtils;

public final class DES {
  private static final String TAG = "DES";
  private static final byte[] IV_PARAMETER = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
  private static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";

  private DES() {
    // private constructor to prevent instantiation
  }

  public static String encrypt(String key, String data) {
    try {
      byte[] bytes = EncryptUtils.encryptDES2Base64(
        data.getBytes(),
        key.getBytes(),
        CIPHER_ALGORITHM,
        IV_PARAMETER
      );
      return new String(bytes);
    } catch (Throwable t) {
      Log.e(TAG, "encrypt error, key: " + key + ", data: " + data, t);
      return "";
    }
  }

  public static String decrypt(String key, String data) {
    try {
      byte[] bytes = EncryptUtils.decryptBase64DES(
        data.getBytes(),
        key.getBytes(),
        CIPHER_ALGORITHM,
        IV_PARAMETER
      );
      return new String(bytes);
    } catch (Throwable t) {
      Log.e(TAG, "decrypt error, key: " + key + ", data: " + data, t);
      return "";
    }
  }
}
