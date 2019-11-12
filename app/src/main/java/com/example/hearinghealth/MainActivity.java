package com.example.hearinghealth;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    /* constants */
    private static final int POLL_INTERVAL = 300;

    /** running state **/
    private boolean mRunning = false;

    /** config state **/
    private int mThreshold;

    int RECORD_AUDIO = 0;
    private PowerManager.WakeLock mWakeLock;

    private Handler mHandler = new Handler();

    /* References to view elements */
    private TextView mStatusView,tv_noice;

    /* sound data source */
    private DetectNoise mSensor;
    ProgressBar bar;
    /****************** Define runnable thread again and again detect noise *********/

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            start();
        }
    };
    // Create runnable thread to Monitor Voice
    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
            //Log.i("Noise", "runnable mPollTask");
            updateDisplay("Monitoring Voice...", amp);

            if ((amp > mThreshold)) {
                callForHelp(amp);
            }
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }
    };
    /** Called when the activity is first created. */
    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Defined SoundLevelView in main.xml file
        setContentView(R.layout.activity_main);
        mStatusView = findViewById(R.id.status);
        tv_noice= findViewById(R.id.tv_noice);
        bar= findViewById(R.id.progressBar1);
        // Used to record voice
        mSensor = new DetectNoise();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, "NoiseAlert");
    }
    @Override
    public void onResume() {
        super.onResume();

        initializeApplicationConstants();
        if (!mRunning) {
            mRunning = true;
            start();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        stop();
    }
    private void start() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO);
        }

        mSensor.start();
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
        //Noise monitoring start
        // Runnable(mPollTask) will execute after POLL_INTERVAL
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }
    private void stop() {
        Log.d("Noise", "==== Stop Noise Monitoring===");
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        bar.setProgress(0);
        updateDisplay("stopped...", 0.0);
        mRunning = false;
    }

    private void initializeApplicationConstants() {
        // Set Noise Threshold
        mThreshold = 8;

    }

    private void updateDisplay(String status, double signalEMA) {
        mStatusView.setText(status);
        bar.setProgress((int)signalEMA);
        Log.d("SONUND", String.valueOf(signalEMA));
        tv_noice.setText(signalEMA+"dB");
    }

    private void callForHelp(double signalEMA) {
        // Show alert when noise thersold crossed
        Toast.makeText(getApplicationContext(), "Noise Thersold Crossed,Check the noise level around you",
                Toast.LENGTH_LONG).show();
        Log.d("SONUND", String.valueOf(signalEMA));
        tv_noice.setText(signalEMA+"dB");
    }

};
