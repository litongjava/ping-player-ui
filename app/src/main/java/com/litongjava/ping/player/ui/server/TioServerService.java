package com.litongjava.ping.player.ui.server;

import com.blankj.utilcode.util.ThreadUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * @author Ping E Lee
 * @email itonglinux@qq.com
 * @date 2022-05-03
 */
public class TioServerService {
  private Logger log = LoggerFactory.getLogger(this.getClass());
  ExecutorService cachedPool = ThreadUtils.getCachedPool();

  public void startTioServer(String serverIp, int serverPort) {

    cachedPool.submit(() -> {
      BytesServerStarter.run(serverIp, serverPort);
    });
  }
}