package com.example.tiltspotbaseproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // System sensor manager instance.
    private SensorManager mSensorManager;

    // Accelerometer and magnetometer sensors, as retrieved from the
    // sensor manager.
    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnetometer;

    // Variabel untuk menerima perubahan data pada sensor
    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];

    private ImageView spotTop;
    private ImageView spotBottom;
    private ImageView spotLeft;
    private ImageView spotRight;

    // TextViews to display current sensor values.
    private TextView mTextSensorAzimuth;
    private TextView mTextSensorPitch;
    private TextView mTextSensorRoll;


    // Very small values for the accelerometer (on all three axes) should
    // be interpreted as 0. This value is the amount of acceptable
    // non-zero drift.
    private static final float VALUE_DRIFT = 0.05f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mTextSensorAzimuth = (TextView) findViewById(R.id.value_azimuth);
        mTextSensorPitch = (TextView) findViewById(R.id.value_pitch);
        mTextSensorRoll = (TextView) findViewById(R.id.value_roll);


        spotTop = findViewById(R.id.spot_top);
        spotBottom = findViewById(R.id.spot_bottom);
        spotLeft = findViewById(R.id.spot_left);
        spotRight = findViewById(R.id.spot_right);

        // Get accelerometer and magnetometer sensors from the sensor manager.
        // The getDefaultSensor() method returns null if the sensor
        // is not available on the device.
        mSensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_MAGNETIC_FIELD);

    }

    /**
     * Listeners for the sensors are registered in this callback so that
     * they can be unregistered in onStop().
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Listeners for the sensors are registered in this callback and
        // can be unregistered in onStop().
        //
        // Check to ensure sensors are available before registering listeners.
        // Both listeners are registered with a "normal" amount of delay
        // (SENSOR_DELAY_NORMAL).

        // Register semua sensor yang dipakai
        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(this, mSensorMagnetometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is stopped.
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Mengetahui tipe sensor mana yg berubah
        int sensorType = sensorEvent.sensor.getType();

        // Ambil data pada sensor
        switch( sensorType ) {
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = sensorEvent.values.clone();
                break;
            default:
                break;
        }

        // Ubah koordinat menjadi rotation matrix
        float[] rotationMatrix = new float[9];
        boolean rotation0k = SensorManager.getRotationMatrix(rotationMatrix, null, mAccelerometerData, mMagnetometerData);

        // Ubah rotation matrix menjadi data
        float[] orientationValues = new float[3];
        if ( rotation0k ) {
            SensorManager.getOrientation(rotationMatrix, orientationValues);
        }

        float mAzimuth = orientationValues[0];
        float mPitch = orientationValues[1];
        float mRoll = orientationValues[2];


        if (Math.abs(mPitch) < VALUE_DRIFT) {
            mPitch = 0;
        }
        if (Math.abs(mRoll) < VALUE_DRIFT) {
            mRoll = 0;
        }

        spotRight.setAlpha(0f);
        spotTop.setAlpha(0f);
        spotBottom.setAlpha(0f);
        spotLeft.setAlpha(0f);

        if (mPitch > 0) {
            spotBottom.setAlpha(mPitch);
        } else {
            spotTop.setAlpha(Math.abs(mPitch));
        }

        if (mRoll > 0) {
            spotLeft.setAlpha(mRoll);
        } else {
            spotRight.setAlpha(Math.abs(mRoll));
        }

        mTextSensorAzimuth.setText(getResources().getString(
                R.string.value_format, mAzimuth
        ));
        mTextSensorPitch.setText(getResources().getString(
                R.string.value_format, mPitch
        ));
        mTextSensorRoll.setText(getResources().getString(
                R.string.value_format, mRoll
        ));


    }

    /**
     * Must be implemented to satisfy the SensorEventListener interface;
     * unused in this app.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}