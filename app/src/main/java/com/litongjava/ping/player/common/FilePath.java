package com.litongjava.ping.player.common;

import com.blankj.utilcode.util.Utils;

import java.io.File;

/**
 * Created by wangchenyan.top on 2022/9/24.
 */
public class FilePath {

  public static String getHttpCache() {
    return assembleExternalCachePath("http");
  }

  public static String getLogRootDir() {
    return assembleExternalFilePath("log");
  }

  public static String getLrcDir() {
    String path = assembleExternalFilePath("lrc");
    mkdirs(path);
    return path;
  }

  public static String getLogPath(String type) {
    return getLogRootDir() + File.separator + type;
  }

  private static String assembleExternalCachePath(String name) {
    return Utils.getApp().getExternalCacheDir() + File.separator + "music_" + name;
  }

  private static String assembleExternalFilePath(String name) {
    File externalFilesDir = Utils.getApp().getExternalFilesDir("music_" + name);
    return externalFilesDir != null ? externalFilesDir.getPath() : "";
  }

  private static void mkdirs(String path) {
    File file = new File(path);
    if (!file.exists()) {
      file.mkdirs();
    }
  }

  // Private constructor to prevent instantiation
  private FilePath() {
  }
}
