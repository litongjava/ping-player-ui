package com.litongjava.ping.player.test;

import android.media.MediaMetadataRetriever;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MetadataExtractorTest {
  Logger log = LoggerFactory.getLogger(this.getClass());

  public void getMp3Meta() {
    MediaMetadataRetriever retriever = new MediaMetadataRetriever();

    String mp3FilePath = "/storage/emulated/0/Music/开言英语/A1_APartyInvitation__lesson_1368784253.mp3";

    try {

      retriever.setDataSource(mp3FilePath);

      String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
      String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
      String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
      String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
      byte[] albumCoverBytes = retriever.getEmbeddedPicture();

      // Convert duration to long
      Long durationLong = Long.parseLong(duration);

      log.info("Title: " + title);
      log.info("Artist: " + artist);
      log.info("album:{}", album);
      log.info("durationLong:{}", durationLong);
      log.info("albumCoverBytes:{}", albumCoverBytes.length);

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      retriever.release(); // Don't forget to release the retriever
    }
  }
}
