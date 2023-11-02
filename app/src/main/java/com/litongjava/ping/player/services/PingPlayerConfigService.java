package com.litongjava.ping.player.services;

import com.litongjava.android.utils.thread.ThreadIOUtils;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.ping.player.storage.db.MusicDatabase;
import com.litongjava.ping.player.storage.db.entity.PingPlayerConfigEntity;
import com.litongjava.ping.player.storage.db.entity.PingPlayerConfigKey;

public class PingPlayerConfigService {

  private MusicDatabase db = Aop.get(MusicDatabase.class);

  public PingPlayerConfigEntity getCurrentPlayMode() {
    return db.configDao().selectByKey(PingPlayerConfigKey.playMode);
  }

  public void setPlayMode(int value) {
    ThreadIOUtils.executeByIo(() -> {
      PingPlayerConfigEntity entity = db.configDao().selectByKey(PingPlayerConfigKey.playMode);
      if (entity == null) {
        entity = new PingPlayerConfigEntity(PingPlayerConfigKey.playMode, value + "", "play mode");
        db.configDao().insert(entity);
      } else {
        entity.setValue(value + "");
        db.configDao().update(entity);
      }

      return null;
    });
  }
}
