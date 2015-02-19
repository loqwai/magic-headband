package com.loqwai.magic_headband;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MagicHeadbandMain";
    private static final String ACTION_USB_PERMISSION = "USB_PERMISSION_GRANTED";

    private BroadcastReceiver receiver;
    private BlinkyTape blinkyTape;
    private UsbManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                    Log.d(TAG, "got permission to use the blinky!");
                    if(blinkyTape == null) {
                        blinkyTape = BlinkyTape.findBlinkyTape(manager);
                    }

                    if(blinkyTape == null) {
                        Log.d(TAG, "got permission to use the blinky, but can't find it.");
                    }

                    try {
                        blinkyTape.connect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                    Log.d(TAG, "I saw a USB device attach.");
                    blinkyTape = BlinkyTape.findBlinkyTape(manager);
                    if(blinkyTape == null) {
                        return;
                    }

                    try {
                        if (!blinkyTape.connect()) {
                            askForPermission();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        registerReceiver(receiver, filter);
        setContentView(R.layout.activity_main);
    }

    private void askForPermission() {
        Log.d(TAG, "asking for permission");
        manager.requestPermission(blinkyTape.getDevice(),
                PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
