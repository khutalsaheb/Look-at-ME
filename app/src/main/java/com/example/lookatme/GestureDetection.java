package com.example.lookatme;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;



class GestureDetection extends GestureDetector.SimpleOnGestureListener {

    final static int SWIPE_UP = 1;
    final static int SWIPE_DOWN = 2;
    final static int SWIPE_LEFT = 3;
    final static int SWIPE_RIGHT = 4;

    private final static int MODE_SOLID = 1;
    private final static int MODE_DYNAMIC = 2;

    private final static int ACTION_FAKE = -13; // just an unlikely number

    private final int mode = MODE_DYNAMIC;
    private boolean tapIndicator = false;

    private final Activity context;

    private final GestureDetector detector;
    private final SimpleGestureListener listener;

    GestureDetection(Activity context, SimpleGestureListener sgl) {

        this.context = context;
        this.detector = new GestureDetector(context, this);
        this.listener = sgl;
    }

    void onTouchEvent(MotionEvent event) {


        boolean result = this.detector.onTouchEvent(event);
        if (this.mode == MODE_SOLID)
            event.setAction(MotionEvent.ACTION_CANCEL);
        else if (this.mode == MODE_DYNAMIC) {

            if (event.getAction() == ACTION_FAKE)
                event.setAction(MotionEvent.ACTION_UP);
            else if (result)
                event.setAction(MotionEvent.ACTION_CANCEL);
            else if (this.tapIndicator) {
                event.setAction(MotionEvent.ACTION_DOWN);
                this.tapIndicator = false;
            }

        }
        // else just do nothing, it's Transparent
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {

        final float xDistance = Math.abs(e1.getX() - e2.getX());
        final float yDistance = Math.abs(e1.getY() - e2.getY());

        int swipe_Max_Distance = 350;
        if (xDistance > swipe_Max_Distance
                || yDistance > swipe_Max_Distance)
            return false;

        velocityX = Math.abs(velocityX);
        velocityY = Math.abs(velocityY);
        boolean result = false;

        int swipe_Min_Distance = 50;
        int swipe_Min_Velocity = 100;
        if (velocityX > swipe_Min_Velocity
                && xDistance > swipe_Min_Distance) {
            if (e1.getX() > e2.getX()) // right to left
                this.listener.onSwipe(SWIPE_RIGHT);
            else
                this.listener.onSwipe(SWIPE_LEFT);

            result = true;
        } else if (velocityY > swipe_Min_Velocity
                && yDistance > swipe_Min_Distance) {
            if (e1.getY() > e2.getY()) // bottom to up
                this.listener.onSwipe(SWIPE_UP);
            else
                this.listener.onSwipe(SWIPE_DOWN);

            result = true;
        }

        return result;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent arg) {

        if (this.mode == MODE_DYNAMIC) {
            arg.setAction(ACTION_FAKE);

            this.context.dispatchTouchEvent(arg);
        }

        return false;
    }

     interface SimpleGestureListener {
        void onSwipe(int direction);

    }

}