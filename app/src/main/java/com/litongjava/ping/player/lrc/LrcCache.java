package com.litongjava.ping.player.lrc;

import com.litongjava.ping.player.common.FilePath;
import com.litongjava.ping.player.storage.db.entity.SongEntity;

import java.io.File;


/**
 * Created by wangchenyan.top on 2023/9/18.
 */
public class LrcCache {

  /**
   * Get lyrics path.
   */
  public static String getLrcFilePath(SongEntity music) {
    if (music.isLocal()) {
      File audioFile = new File(music.getPath());
      File lrcFile = new File(audioFile.getParent(), audioFile.getName().replaceFirst("[.][^.]+$", "") + ".lrc");
      if (lrcFile.exists()) {
        return lrcFile.getPath();
      }
    } else {
      File lrcFile = new File(FilePath.getLrcDir(), music.getSongId().toString());
      if (lrcFile.exists()) {
        return lrcFile.getPath();
      }
    }
    return null;
  }

  // Note: The following method uses Kotlin coroutines which doesn't have a direct Java equivalent.
  // You would need to implement asynchronous behavior differently in Java,
  // possibly using Executors, Futures, or a library like RxJava.
  public static File saveLrcFile(SongEntity music, String content) throws Exception {
    File file = new File(FilePath.getLrcDir(), music.getSongId().toString());
    return file;
  }

  private LrcCache() {
  }
}
