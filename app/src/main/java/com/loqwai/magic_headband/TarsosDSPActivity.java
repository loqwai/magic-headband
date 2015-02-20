package com.loqwai.magic_headband;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;

public class TarsosDSPActivity extends ActionBarActivity {
    private static String TAG = "TarsosMagicHeadband";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        setupAudio();
		
	}

    private void setupAudio() {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);


        dispatcher.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, new PitchDetectionHandler() {

            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult,
                    AudioEvent audioEvent) {
                final float pitchInHz = pitchDetectionResult.getPitch();
                if (pitchInHz != -1)
                    emitPitch(pitchInHz);
                runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         TextView view =(TextView)findViewById(R.id.statusView);
                         view.setText("Pitch: " + pitchInHz);
                    }
                });

            }
        }));
        new Thread(dispatcher,"Audio Dispatcher").start();
    }

    private void emitPitch(float pitchInHz) {
        float[] hsv = new float[3];
        hsv[0] = (float)(pitchInHz / 2000.0) * 360;
        hsv[1] = 1.0f;
        hsv[2] = 1.0f;
        messageBlinky(Color.HSVToColor(hsv));
    }

    /**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.activity_main,
					container, false);
			return rootView;
		}
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
