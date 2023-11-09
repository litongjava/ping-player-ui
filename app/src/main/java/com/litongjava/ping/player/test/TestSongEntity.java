package com.litongjava.ping.player.test;

import android.os.Environment;

import com.litongjava.jfinal.aop.Aop;
import com.litongjava.ping.player.services.MetadataExtractorService;
import com.litongjava.ping.player.storage.db.entity.SongEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestSongEntity {
  public static SongEntity getSong(Long songId) {
    int type = 0;
    String title = "Title";
    String artist = "artist";
    Long artistId = 0L;
    String album = null;
    Long albumId = 0L;
    String albumCover = null;
    Long duration = 494000L;
    String path = "/storage/emulated/0/Music/开言英语/A1_APartyInvitation__lesson_1368784253.mp3";
    String name = "name";
    Long fileSize = 0L;
    SongEntity songEntity = new SongEntity(type, songId, title, artist, artistId, album, albumId, albumCover, duration, path, name, fileSize);
    return songEntity;
  }

  public static List<SongEntity> getPlayList() {
    ArrayList<SongEntity> list = new ArrayList<>();
    File externalStorageDirectory = Environment.getExternalStorageDirectory();

//    String path ="/storage/self/primary/Music/开言英语";
    File mp3Folder = new File(externalStorageDirectory, "/Music/3D脑暴");
    File[] files = mp3Folder.listFiles();
    for (int i = 0; i < files.length; i++) {
      File file = files[i];
      if (file.getName().endsWith("mp3")) {
        SongEntity song = Aop.get(MetadataExtractorService.class).getSong(file);
        Long id = Long.valueOf(i);
        song.setSongId(id);
        song.setUniqueId(id + "");
        song.setAlbumId(id);
        song.setArtistId(id);
        song.setCollectionId("OpenLanguage");
        list.add(song);
      }

    }

    //upateList.add(getSong(0L));
    return list;
  }
}
