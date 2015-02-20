package com.loqwai.magic_headband;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    private static String TAG = "MagicHeadbandMain";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void setupAudio(View view) {
        setStatus("Starting Audio Service");
        Intent intent = new Intent(AudioPitchService.PITCH_INTENT);
        intent.setClass(this, AudioPitchService.class);
        startService(intent);
    }
}
