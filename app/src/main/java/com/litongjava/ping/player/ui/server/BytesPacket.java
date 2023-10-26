package com.litongjava.ping.player.ui.server;

import org.tio.core.intf.Packet;

public class BytesPacket extends Packet {

  private byte[] body;
  
  public byte[] getBody() {
    return body;
  }
  
  public void setBody(byte[] body) {
    this.body=body;
  }
}