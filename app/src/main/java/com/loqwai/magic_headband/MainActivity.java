package com.loqwai.magic_headband;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;


public class MainActivity extends ActionBarActivity {
    private static String TAG = "MagicHeadbandMain";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupAudio();
    }

    private void setupAudio() {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);


        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, new PitchDetectionHandler() {

            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult,
                                    AudioEvent audioEvent) {
                final float pitchInHz = pitchDetectionResult.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setStatus("pitch: " + pitchInHz);
                    }
                });

            }
        }));

        new Thread(dispatcher,"Audio Dispatcher").start();
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
