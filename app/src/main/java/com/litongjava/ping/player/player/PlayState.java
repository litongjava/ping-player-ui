package com.litongjava.ping.player.player;

public abstract class PlayState {

  public static final PlayState IDLE = new Idle();
  public static final PlayState PREPARING = new Preparing();
  public static final PlayState PLAYING = new Playing();
  public static final PlayState PAUSE = new Pause();

  private PlayState() {
  }

  public boolean isIdle() {
    return this instanceof Idle;
  }

  public boolean isPreparing() {
    return this instanceof Preparing;
  }

  public boolean isPlaying() {
    return this instanceof Playing;
  }

  public boolean isPausing() {
    return this instanceof Pause;
  }

  public static final class Idle extends PlayState {
  }

  public static final class Preparing extends PlayState {
  }

  public static final class Playing extends PlayState {
  }

  public static final class Pause extends PlayState {
  }
}
