package com.loqwai.magic_headband;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.io.IOException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

/**
 * Created by redaphid on 2/19/15.
 */
public class AudioPitchService extends IntentService {
    private static final String TAG = "AudioPitchService";
    public static final String PITCH_INTENT = "PITCH";

    public AudioPitchService() {
        super("AudioPitchService");
    }

    public AudioPitchService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, new PitchDetectionHandler() {

            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult,
                                    AudioEvent audioEvent) {
                Log.d(TAG, "Pitch: " + pitchDetectionResult.getPitch() );
            }
        }));
        
        new Thread(dispatcher, "Audio Dispatcher").start();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch (intent.getAction()) {

            default:
                Log.d(TAG, "Got a weird message: " + intent.getAction());
        }
    }
}
