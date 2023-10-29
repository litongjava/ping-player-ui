package com.litongjava.ping.player.player;

import androidx.annotation.MainThread;
import androidx.lifecycle.LiveData;


import com.litongjava.ping.player.storage.db.entity.SongEntity;

import java.util.List;
public interface AudioPlayer {

  LiveData<List<SongEntity>> getPlaylist();

  LiveData<SongEntity> getCurrentSong();

  LiveData<PlayState> getPlayState();

  LiveData<Integer> getPlayProgress();

  LiveData<Integer> getBufferingPercent();

  @MainThread
  void addAndPlay(SongEntity... songs);

  @MainThread
  void replaceAll(List<SongEntity> songList, SongEntity song);

  @MainThread
  void play(SongEntity song);

  @MainThread
  void delete(SongEntity song);

  @MainThread
  void playPause();

  @MainThread
  void startPlayer();

  @MainThread
  void pausePlayer(boolean abandonAudioFocus);

  @MainThread
  void stopPlayer();

  @MainThread
  void next();

  @MainThread
  void prev();

  @MainThread
  void seekTo(int msec);

  @MainThread
  void setVolume(float leftVolume, float rightVolume);

  @MainThread
  long getAudioPosition();

  @MainThread
  int getAudioSessionId();

  void clearPlayList();

  void delete(Long songId);
}
