package com.litongjava.ping.player.services;

import android.media.MediaMetadataRetriever;

import com.litongjava.ping.player.storage.db.entity.SongEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MetadataExtractorService {
  private Logger log = LoggerFactory.getLogger(this.getClass());

  public SongEntity getSong(File file) {


    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    String absolutePath = file.getAbsolutePath();
    log.info("absolutePath:{}", absolutePath);
    try {
      retriever.setDataSource(absolutePath);
      String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
      String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
      // String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
      String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
      // byte[] albumCoverBytes = retriever.getEmbeddedPicture();

      // Convert duration to long
      Long durationLong = Long.parseLong(duration);

      SongEntity songEntity = new SongEntity(SongEntity.LOCAL);
      songEntity.setTitle(title);
      songEntity.setArtist(artist);
      songEntity.setDuration(durationLong);
      songEntity.setFileName(file.getName());
      songEntity.setPath(absolutePath);
      songEntity.setFileSize(file.length());
      return songEntity;

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      retriever.release(); // Don't forget to release the retriever
    }

    return null;
  }
}
