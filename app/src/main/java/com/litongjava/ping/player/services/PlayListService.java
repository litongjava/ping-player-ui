package com.litongjava.ping.player.services;

import com.blankj.utilcode.util.ThreadUtils;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.ping.player.storage.db.MusicDatabase;
import com.litongjava.ping.player.storage.db.entity.SongEntity;
import com.litongjava.ping.player.task.BackgroundTask;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class PlayListService {

  public void updateListByIo(List<SongEntity> entities) {
    ThreadUtils.executeByIo(new BackgroundTask<Object>() {
      @Override
      public Void doInBackground() {
        updateList(entities);
        return null;
      }
    });
  }


  public void deleteByIo(SongEntity song) {
    ThreadUtils.executeByIo(new BackgroundTask<Object>() {
      @Override
      public Void doInBackground() {
        delete(song);
        return null;
      }
    });
  }

  public void delete(SongEntity song) {
    MusicDatabase db = Aop.get(MusicDatabase.class);
    db.playlistDao().delete(song);
  }

  private void updateList(List<SongEntity> entities) {
    MusicDatabase db = Aop.get(MusicDatabase.class);
    db.playlistDao().clear();
    db.playlistDao().insertAll(entities);
  }
}
