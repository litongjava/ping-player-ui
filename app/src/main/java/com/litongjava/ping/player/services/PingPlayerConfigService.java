package com.litongjava.ping.player.services;

import com.blankj.utilcode.util.ThreadUtils;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.ping.player.storage.db.MusicDatabase;
import com.litongjava.ping.player.storage.db.entity.PingPlayerConfigEntity;
import com.litongjava.ping.player.storage.db.entity.PingPlayerConfigKey;

public class PingPlayerConfigService {

  public void updateCurrentPlayIndexInDb(int index) {
    ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Object>() {
      @Override
      public Void doInBackground() {
        MusicDatabase db = Aop.get(MusicDatabase.class);
        PingPlayerConfigEntity entity = db.configDao().selectByKey(PingPlayerConfigKey.playIndex);
        if (entity == null) {
          entity = new PingPlayerConfigEntity(PingPlayerConfigKey.playIndex, index + "", "play index");
          db.configDao().insert(entity);
        } else {
          entity.setValue(index + "");
          db.configDao().update(entity);
        }
        return null;
      }

      @Override
      public void onSuccess(Object result) {

      }
    });
  }
}
