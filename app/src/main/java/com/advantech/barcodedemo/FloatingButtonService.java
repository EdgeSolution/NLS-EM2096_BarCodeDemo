package com.advantech.barcodedemo;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class FloatingButtonService extends Service {
    private static final String ACTION_TRIGGER_SCAN = "com.adv.em2096barcode.TRIGGER_SCAN";
    private LinearLayout mFloatLayout;
    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;
    private ImageButton mFloatView;
    private long startTime = 0;
    private long endTime = 0;
    private boolean noTriggerClick;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wmParams = new WindowManager.LayoutParams();
        mWindowManager  = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);

        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }

        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        mWindowManager.addView(mFloatLayout, wmParams);
        mFloatView = (ImageButton) mFloatLayout.findViewById(R.id.float_id);
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));


        mFloatView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        startTime = System.currentTimeMillis();
                        noTriggerClick = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        noTriggerClick = true;
                        wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth() / 2;
                        wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight() / 2 - 25;
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        endTime = System.currentTimeMillis();
                        if((endTime - startTime) > 0.5*1000L){
                            noTriggerClick = true;
                        }else{
                            noTriggerClick = false;
                        }
                        break;
                }
                return noTriggerClick;
            }

        });

        mFloatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mFloatView.setEnabled(false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        mFloatView.setEnabled(true);
                    }
                }, 1000);
                Intent intent = new Intent(ACTION_TRIGGER_SCAN);
                intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                sendBroadcast(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
    }
}
