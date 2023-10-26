package com.litongjava.ping.player.ui.server;


import org.tio.server.AioServer;
import org.tio.server.ServerGroupContext;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;

import java.io.IOException;

public class BytesServerStarter {
  // handler, 包括编码、解码、消息处理
  public static ServerAioHandler aioHandler = new BytesHandler();
  // 事件监听器，可以为null，但建议自己实现该接口，可以参考showcase了解些接口
  public static ServerAioListener aioListener = null;

  // 一组连接共用的上下文对象
  public static ServerGroupContext serverGroupContext = new ServerGroupContext(aioHandler, aioListener);

  public static AioServer aioServer;

  /**
   * 启动程序入口
   */
  public static void main(String[] args) {
    String serverIp = null;
    int serverPort = 5678;
    run(serverIp, serverPort);
  }

  public static void run(String serverIp, int serverPort) {
    if (aioServer != null) {
      return;
    }
    // 设置心跳
    serverGroupContext.setHeartbeatTimeout(0);
    // aioServer对象
    aioServer = new AioServer(serverGroupContext);
    // 启动服务
    try {
      long start = System.currentTimeMillis();
      aioServer.start(serverIp, serverPort);
      long end = System.currentTimeMillis();
      System.out.println("started:" + (end - start) + "ms");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}