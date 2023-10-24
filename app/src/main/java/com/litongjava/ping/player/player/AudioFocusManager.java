package com.litongjava.ping.player.player;

import android.content.Context;
import android.media.AudioManager;

public class AudioFocusManager {
  private final Context context;
  private final AudioPlayer audioPlayer;
  private final AudioManager audioManager;
  private boolean isPausedByFocusLossTransient;

  public AudioFocusManager(Context context, AudioPlayer audioPlayer) {
    this.context = context;
    this.audioPlayer = audioPlayer;
    this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
  }

  public boolean requestAudioFocus() {
    return audioManager.requestAudioFocus(
      focusChangeListener,
      AudioManager.STREAM_MUSIC,
      AudioManager.AUDIOFOCUS_GAIN
    ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
  }

  public void abandonAudioFocus() {
    audioManager.abandonAudioFocus(focusChangeListener);
  }

  private final AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
    @Override
    public void onAudioFocusChange(int focusChange) {
      switch (focusChange) {
        case AudioManager.AUDIOFOCUS_GAIN:
          if (isPausedByFocusLossTransient) {
            // Resume playback after call
            audioPlayer.startPlayer();
          }
          // Restore volume
          audioPlayer.setVolume(1f, 1f);
          isPausedByFocusLossTransient = false;
          break;

        case AudioManager.AUDIOFOCUS_LOSS:
          audioPlayer.pausePlayer(true);
          break;

        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
          audioPlayer.pausePlayer(false);
          isPausedByFocusLossTransient = true;
          break;

        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
          // Reduce volume to half
          audioPlayer.setVolume(0.5f, 0.5f);
          break;
      }
    }
  };
}
