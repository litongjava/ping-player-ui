package com.litongjava.ping.player.test;

import com.litongjava.ping.player.storage.db.entity.SongEntity;

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
    list.add(getSong(0L));
    return list;
  }
}
