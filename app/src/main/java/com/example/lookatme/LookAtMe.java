package com.example.lookatme;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import java.io.IOException;


public class LookAtMe extends VideoView {


    private CameraSource cameraSource;
    private Context activityContext;

    public void init(Context activityContext) {
        this.activityContext = activityContext;
        createCameraSource();
    }


    public void resume() {
        if (cameraSource != null) {
            try {
                if (ActivityCompat.checkSelfPermission(activityContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) activityContext, new String[]{Manifest.permission.CAMERA}, 1);
                    Toast.makeText(activityContext, "Grant Permission and restart app", Toast.LENGTH_SHORT).show();
                }
                cameraSource.start();
                //Log.d("some8","Starting first");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void paused() {
        if (cameraSource != null) {
            cameraSource.stop();
            //Log.d("some6","stopped here");
        }
        if (this.isPlaying()) {
            this.pause();
            //Log.d("some7","Paused() here");
        }
    }

    public void destroy() {
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    public LookAtMe(Context context) {
        super(context);
    }

    public LookAtMe(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LookAtMe(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LookAtMe(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void pause() {
        super.pause();

        //Log.d("some1","Paused due to pause()");
    }

    @Override
    public boolean isPlaying() {
        return super.isPlaying();
    }

    @Override
    public void start() {
        super.start();
    }


    private class EyesTracker extends Tracker<Face> {

        private final float THRESHOLD = 0.75f;

        private EyesTracker() {

        }

        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {
            if (face.getIsLeftEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) {
                if (!isPlaying())
                    start();

              } else {
                if (isPlaying()) {
                     pause();
                }

            }
        }

        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);
            pause();
         }

        @Override
        public void onDone() {
            super.onDone();
        }
    }

    private void createCameraSource() {
        FaceDetector detector = new FaceDetector.Builder(activityContext).setTrackingEnabled(true).setClassificationType(FaceDetector.ALL_CLASSIFICATIONS).setMode(FaceDetector.FAST_MODE).build();

        detector.setProcessor(new LargestFaceFocusingProcessor(detector, new EyesTracker()));

        cameraSource = new CameraSource.Builder(activityContext, detector).setRequestedPreviewSize(1024, 768).setFacing(CameraSource.CAMERA_FACING_FRONT).setRequestedFps(30.0f).build();

        try {
            if (ActivityCompat.checkSelfPermission(activityContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) activityContext, new String[]{Manifest.permission.CAMERA}, 1);
                Toast.makeText(activityContext, "Grant Permission and restart app", Toast.LENGTH_SHORT).show();
            } else
                cameraSource.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}