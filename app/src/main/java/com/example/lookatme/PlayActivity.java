package com.example.lookatme;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.MediaController;

public class PlayActivity extends AppCompatActivity implements GestureDetection.SimpleGestureListener {

    private LookAtMe play_video;
    private AudioManager audioManager;
    private GestureDetection detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Context context = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00212121")));
        getSupportActionBar().setElevation(0);
        play_video = findViewById(R.id.play_video);

        play_video.init(this);
        play_video.requestFocus();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        detector = new GestureDetection(this, this);

        String video_data = getIntent().getStringExtra("video");
        getSupportActionBar().setTitle(video_data.split(",>")[0]);

        Uri video = Uri.parse(video_data.split(",>")[1]);
        play_video.setVideoURI(video);
        MediaController mediaController = new MediaController(context);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource("" + video_data.split(",>")[1]);
        int ori = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ori = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
        }
        retriever.release();

        if (ori == 90) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mediaController.setAnchorView(play_video);
        mediaController.setMediaPlayer(play_video);
        play_video.setMediaController(mediaController);

        play_video.requestFocus();
        play_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                play_video.start();

                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                    }
                });

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.reset();
                        mp.release();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        play_video.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        play_video.paused();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        play_video.destroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        // TODO Auto-generated method stub

        switch (direction) {

            case GestureDetection.SWIPE_LEFT:

                int currentPosition = play_video.getCurrentPosition();
                currentPosition = play_video.getCurrentPosition() + 5000;
                play_video.seekTo(currentPosition);
                break;

            case GestureDetection.SWIPE_RIGHT:

                currentPosition = play_video.getCurrentPosition();
                currentPosition = play_video.getCurrentPosition() - 5000;
                play_video.seekTo(currentPosition);
                break;

            case GestureDetection.SWIPE_DOWN:

                int currentVolume = audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        currentVolume - 1, 0);
                break;
            case GestureDetection.SWIPE_UP:

                currentVolume = audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        currentVolume + 1, 0);
                break;
        }
    }


}