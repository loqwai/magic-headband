package com.loqwai.magic_headband;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MagicHeadbandMain";
    private static final String ACTION_USB_PERMISSION = "USB_PERMISSION_GRANTED";
    private static final String USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";

    private BlinkyTape blinkyTape;
    private UsbManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case ACTION_USB_PERMISSION:
                        setStatus("Permission Granted");
                        blinkyTape = BlinkyTape.findBlinkyTape(manager);
                        if (blinkyTape == null) {
                            setStatus("Can't find Magic Headband");
                            return;
                        }
                        try {
                            setStatus("Connecting to Magic Headband");
                            if (blinkyTape.connect()) {
                                setStatus("Connected!");
                                return;
                            }
                            setStatus("Problem connecting. Asking for permission again.");
                            askForPermission();
                        } catch (IOException e) {
                            setStatus("Error: " + e.getMessage());
                        }
                        return;

                    case USB_DEVICE_ATTACHED:
                        setStatus("USB device attached");
                        blinkyTape = BlinkyTape.findBlinkyTape(manager);
                        if (blinkyTape == null) {
                            setStatus("Can't find Magic Headband");
                            return;
                        }
                        try {
                            if (!blinkyTape.connect()) {
                                setStatus("Asking for permission to use Magic Headband.");
                                askForPermission();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(USB_DEVICE_ATTACHED);
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

    public void setStatus(String status) {
        Log.d(TAG, status);
        TextView view =(TextView)findViewById(R.id.statusView);
        view.setText(status);
    }

    public void renderRed(View view) {
        setStatus("Rendering red.");
        if(blinkyTape != null) {
            blinkyTape.render(Color.RED);
        }
    }

    public void renderYellow(View view) {
        setStatus("Rendering red.");
        if(blinkyTape != null) {
            blinkyTape.render(Color.YELLOW);
        }
    }

    public void renderBlue(View view) {
        setStatus("Rendering red.");
        if(blinkyTape != null) {
            blinkyTape.render(Color.BLUE);
        }
    }
}
