package com.loqwai.magic_headband;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    private static String TAG = "MagicHeadbandMain";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        messageBlinky(Color.RED);
    }

    public void renderYellow(View view) {
        setStatus("Rendering yellow.");
        messageBlinky(Color.YELLOW);
    }

    public void renderBlue(View view) {
        setStatus("Rendering blue.");
        messageBlinky(Color.BLUE);
    }

    private void messageBlinky(int color) {
        Intent intent = new Intent(BlinkyTapeService.ANIMATE_INTENT);
        intent.putExtra("color", color);
        intent.setClass(this, BlinkyTapeService.class);

        startService(intent);
    }
}
