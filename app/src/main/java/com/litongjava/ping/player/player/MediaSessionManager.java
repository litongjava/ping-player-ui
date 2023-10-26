package com.litongjava.ping.player.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.litongjava.ping.player.model.CommonResult;
import com.litongjava.ping.player.storage.db.entity.SongEntity;
import com.litongjava.ping.player.utils.ImageUtils;

public class MediaSessionManager {
  private static final String TAG = "MediaSessionManager";
  private static final long MEDIA_SESSION_ACTIONS = PlaybackStateCompat.ACTION_PLAY |
    PlaybackStateCompat.ACTION_PAUSE |
    PlaybackStateCompat.ACTION_PLAY_PAUSE |
    PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
    PlaybackStateCompat.ACTION_STOP |
    PlaybackStateCompat.ACTION_SEEK_TO;

  private final Context context;
  private final AudioPlayer audioPlayer;
  private final MediaSessionCompat mediaSession;
  //  private Job loadCoverJob;
  private AsyncTask<SongEntity, Void, Bitmap> loadCoverTask;

  public MediaSessionManager(Context context, AudioPlayer audioPlayer) {
    this.context = context;
    this.audioPlayer = audioPlayer;
    this.mediaSession = new MediaSessionCompat(context, TAG);
    mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
    mediaSession.setCallback(callback);
    mediaSession.setActive(true);
  }

  public void updatePlaybackState() {
    int state;
    if (audioPlayer.getPlayState().getValue().isPlaying() || audioPlayer.getPlayState().getValue().isPreparing()) {
      state = PlaybackStateCompat.STATE_PLAYING;
    } else {
      state = PlaybackStateCompat.STATE_PAUSED;
    }
    PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder()
      .setActions(MEDIA_SESSION_ACTIONS)
      .setState(state, audioPlayer.getAudioPosition(), 1f);
    mediaSession.setPlaybackState(builder.build());
  }

  public void updateMetaData(SongEntity song) {
    if (loadCoverTask != null) {
      loadCoverTask.cancel(true);
      loadCoverTask = null;
    }
    if (song == null) {
      mediaSession.setMetadata(null);
    } else {
      int numTracks = audioPlayer.getPlaylist().getValue() != null ? audioPlayer.getPlaylist().getValue().size() : 0;
      MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getTitle())
        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getArtist())
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbum())
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, song.getArtist())
        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.getDuration())
        .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, numTracks);
      mediaSession.setMetadata(builder.build());
      loadCoverTask = new AsyncTask<SongEntity, Void, Bitmap>() {
        @Override
        protected Bitmap doInBackground(SongEntity... songs) {
          // Assuming ImageUtils.loadBitmap() returns a Bitmap
          CommonResult<Bitmap> bitmapCommonResult = ImageUtils.loadBitmap(song.getAlbumCover());
          return bitmapCommonResult.getData();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
          if (bitmap != null) {
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
            mediaSession.setMetadata(builder.build());
          }
        }
      }.execute(song);

    }
  }

  private final MediaSessionCompat.Callback callback = new MediaSessionCompat.Callback() {
    @Override
    public void onPlay() {
      audioPlayer.playPause();
    }

    @Override
    public void onPause() {
      audioPlayer.playPause();
    }

    @Override
    public void onSkipToNext() {
      audioPlayer.next();
    }

    @Override
    public void onSkipToPrevious() {
      audioPlayer.prev();
    }

    @Override
    public void onStop() {
      audioPlayer.stopPlayer();
    }

    @Override
    public void onSeekTo(long pos) {
      audioPlayer.seekTo((int) pos);
    }
  };
}
