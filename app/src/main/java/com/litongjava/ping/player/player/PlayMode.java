package com.litongjava.ping.player.player;

public abstract class PlayMode {
  private int value;

  private PlayMode(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static final PlayMode Loop = new PlayMode(0) {
  };
  public static final PlayMode Shuffle = new PlayMode(1) {
  };
  public static final PlayMode Single = new PlayMode(2) {
  };

  public static PlayMode valueOf(int value) {
    switch (value) {
      case 0:
        return Loop;
      case 1:
        return Shuffle;
      case 2:
        return Single;
      default:
        return Loop;
    }
  }
}
