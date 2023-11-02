package com.litongjava.ping.player.player;

public enum PlayMode {
  LOOP(0, "Loop Mode"),
  SHUFFLE(1, "Shuffle Mode"),
  SINGLE(2, "Single Mode");

  private final int value;
  private final String description;

  PlayMode(int value, String description) {
    this.value = value;
    this.description = description;
  }

  public int getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }

  public static PlayMode valueOf(int value) {
    switch (value) {
      case 1:
        return SHUFFLE;
      case 2:
        return SINGLE;
      default:
        return LOOP;
    }
  }
}