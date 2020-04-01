package com.advantech.em2096barcodedemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TAG = "BarCodeTest";
    private static final String SERVICE_PACKAGE_NAME = "com.advantech.em2096barcode";
    private static final String SERVICE_CLASS_NAME = "com.advantech.em2096barcode.RunEm2096BarcodeService";
    private static final String ACTION_START_SERVICE = "com.adv.em2096barcode.START_SERVICE";
    private static final String ACTION_TRANSFER_DATA = "com.adv.em2096barcode.TRANSFER_DATA";

    private TextView textView;
    BarCodeDataBroadcastReceiver barCodeDataBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 100);
            }
        }


        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_TRANSFER_DATA);
        barCodeDataBroadcastReceiver = new BarCodeDataBroadcastReceiver();
        registerReceiver(barCodeDataBroadcastReceiver,filter);


        Intent intent = new Intent(ACTION_START_SERVICE);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setComponent(new ComponentName(SERVICE_PACKAGE_NAME,SERVICE_CLASS_NAME));
        }
        sendBroadcast(intent);

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                textView.setText("");
                return true;
            }
        });

        Button showFloatButton = findViewById(R.id.showfltbtn);
        Button hideFloatButton = findViewById(R.id.hidefltbtn);
        showFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Settings.canDrawOverlays(MainActivity.this)) {
                    Intent intent = new Intent(MainActivity.this, FloatingButtonService.class);
                    startService(intent);
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 100);
                }
            }
        });

        hideFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FloatingButtonService.class);
                stopService(intent);
            }
        });

        Log.d(TAG,"onCreate");
    }

    private  class BarCodeDataBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String barcodeData = intent.getStringExtra("barcodeData");
            if(barcodeData != null){
                textView.append(barcodeData + "\n");
            }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(barCodeDataBroadcastReceiver);
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "Permission denied by user, please check in Settings", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission allowed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
