package com.litongjava.ping.player.services;

import com.litongjava.android.utils.thread.ThreadIOUtils;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.ping.player.storage.db.MusicDatabase;
import com.litongjava.ping.player.storage.db.dao.PingPlayerCurrentPlayIndexDao;
import com.litongjava.ping.player.storage.db.entity.PingPlayerCurrentPlayIndexEntity;

public class PingPlayerCurrentPlayIndexManager {
  public void updateCurrentPlayIndexByIo(String collectionId, Integer index) {
    MusicDatabase db = Aop.get(MusicDatabase.class);
    PingPlayerCurrentPlayIndexDao dao = db.currentPlayIndexDao();
    ThreadIOUtils.executeByIo(() -> {
      PingPlayerCurrentPlayIndexEntity entity = dao.selectByCollectionId(collectionId);
      if (entity == null) {
        entity = new PingPlayerCurrentPlayIndexEntity(collectionId, index);
        dao.insert(entity);
      } else {
        entity.setIndex(index);
        dao.update(entity);
      }
      return null;
    });
  }
}


