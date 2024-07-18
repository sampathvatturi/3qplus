package com.spgon.a3flowers.util;

import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;

import com.spgon.a3flowers.activity.MainActivity;

import java.io.File;
import java.io.IOException;

public class DecibelMeter {
    private MediaRecorder mediaRecorder;
    private Handler handler;
    private boolean isRecording = false;
    private static final int REFRESH_INTERVAL = 250; // Interval in milliseconds to update decibel readings

    private OnDecibelListener decibelListener;




    public interface OnDecibelListener {
        void onRefresh();

        void onDecibelChanged(double decibel);
    }

    public void setDecibelListener(OnDecibelListener listener) {
        decibelListener = listener;
    }

    public void start() {
        if (isRecording) return;

        File myCaptureFile = new File( MainActivity.mcontext.getFilesDir().getPath() + File.separator + "test.3gp");
        if (myCaptureFile.exists()) {
            myCaptureFile.delete();
        }
        try {
            myCaptureFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler = new Handler(Looper.getMainLooper());
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(myCaptureFile);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            handler.postDelayed(updateDecibelRunnable, REFRESH_INTERVAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (!isRecording) return;
        try {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            handler.removeCallbacks(updateDecibelRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Runnable updateDecibelRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaRecorder != null) {
                double amplitude = mediaRecorder.getMaxAmplitude();
                double decibel = 20 * Math.log10(amplitude / 2700.0); // Adjust the reference amplitude accordingly
                if (decibelListener != null) {
                    decibelListener.onDecibelChanged(decibel);
                }
            }
            handler.postDelayed(this, REFRESH_INTERVAL);
        }
    };
}

