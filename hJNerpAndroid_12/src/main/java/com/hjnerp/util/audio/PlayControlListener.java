package com.hjnerp.util.audio;

import android.media.MediaPlayer;


public interface PlayControlListener
{
  void playRecording(String paramString, boolean paramBoolean);

  void stopPlayback();

  boolean isPlaying();

  int getPlaybackDuration();

  MediaPlayer getMediaPlayer();
}