package com.loqwai.magic_headband;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by redaphid on 2/19/15.
 */
public class BlinkyTapeService extends IntentService {
    private static final String TAG = "BlinkyTapeService";
    private static final String ACTION_USB_PERMISSION = "USB_PERMISSION_GRANTED";
    private static final String USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String ANIMATE_INTENT = "BLINKY_TAPE_ANIMATE";

    private UsbManager manager;
    private BlinkyTape blinkyTape;

    public BlinkyTapeService() {
        super("BlinkyTapeService");
    }

    public BlinkyTapeService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        findBlinkyAndConnect();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch (intent.getAction()) {
            case ACTION_USB_PERMISSION:
                Log.d(TAG, "Permission Granted");
                findBlinkyAndConnect();
                return;

            case USB_DEVICE_ATTACHED:
                Log.d(TAG, "USB device attached");
                blinkyTape = BlinkyTape.findBlinkyTape(manager);
                if (blinkyTape == null) {
                    Log.d(TAG, "Can't find Magic Headband");
                    return;
                }
                try {
                    if (!blinkyTape.connect()) {
                        Log.d(TAG, "Asking for permission to use Magic Headband.");
                        askForPermission();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;

            case ANIMATE_INTENT:
                if(intent.hasExtra("color")){
                    Log.d(TAG, "writing to the 'tape");
                    blinkyTape.render(intent.getIntExtra("color", Color.GREEN));
                }
            return;

            default:
                Log.d(TAG, "Got a weird message: " + intent.getAction());
        }
    }

    private boolean findBlinkyAndConnect() {
        blinkyTape = BlinkyTape.findBlinkyTape(manager);
        if (blinkyTape == null) {
            Log.d(TAG, "Can't find Magic Headband");
            return true;
        }
        try {
            Log.d(TAG, "Connecting to Magic Headband");
            if (blinkyTape.connect()) {
                Log.d(TAG, "Connected!");
                return true;
            }
            Log.d(TAG, "Problem connecting. Asking for permission.");
            askForPermission();
        } catch (IOException e) {
            Log.d(TAG, "Error: " + e.getMessage());
        }
        return false;
    }

    private void askForPermission() {
        Log.d(TAG, "asking for permission");
        
        manager.requestPermission(blinkyTape.getDevice(),
                PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0));
    }
}
