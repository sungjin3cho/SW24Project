package com.example.jjj;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Created by project on 2015-04-27.
 */
public class SensorService extends Service implements SensorEventListener {

    static {
//        System.loadLibrary("ifttt_client");
    }

    private static final String TAG = MainActivity.class.getSimpleName();
    private static SensorService mInstance = null;

    private long lastTime;

    private float mPrevRotationVectorValue = 0.0f;
    private float mRotationVectorZ = 0.0f;
    private SensorManager mSensorManager;

    private boolean isFlashOn = false;
    private boolean isLightReady = false;
    private boolean isGravityReady = false;

    private FlashlightController fc;

    public static SensorService getInstance(){
        return mInstance;
    }

    private native void startSensor();
    private native void stopSensor();

    private BroadcastReceiver mBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive (Context context, Intent intent){
            if (intent.getAction() == Intent.ACTION_HEADSET_PLUG) {
                if (intent.hasExtra("state")) {
                    Log.e(TAG, "state = " + intent.getIntExtra("state", 0));
                    if (1 == intent.getIntExtra("state", 0)) {
                        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);

                        long eventtime = SystemClock.uptimeMillis() - 1;
                        KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY, 0);
                        am.dispatchMediaKeyEvent(downEvent);
                        eventtime++;
                        KeyEvent upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY, 0);
                        am.dispatchMediaKeyEvent(upEvent);
                    }
                }
            }
            if(intent.getAction() == Intent.ACTION_SCREEN_ON){
                if(mRotationVectorZ > 0.38f) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.gameloft.android.ANMP.GloftA8HM");
                    Log.e(TAG, "screen on");
                    startActivity(launchIntent);
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
 //               startSensor();
            }
        });
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor mAccelrometerSesnor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Sensor mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        Sensor mRotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        getCamera();
        if (mAccelrometerSesnor != null)
            mSensorManager.registerListener(this, mAccelrometerSesnor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        if (mGravitySensor != null)
            mSensorManager.registerListener(this, mGravitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        if(mLightSensor != null)
            mSensorManager.registerListener(this, mLightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        if(mRotationVectorSensor != null)
            mSensorManager.registerListener(this, mRotationVectorSensor,SensorManager.SENSOR_DELAY_NORMAL);
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mBroadcast,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcast);
        mSensorManager.unregisterListener(this);
     //   stopSensor();
        mInstance = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,
                notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Sensor G")
                .setTicker("Ticker G")
                .setContentText("Content G")
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setPriority(Notification.PRIORITY_HIGH)
                .build();
        startForeground(1,notification);
        return START_STICKY;
    }

    private void getCamera()
    {
        if (fc == null)
        {
            fc = new FlashlightController(this);
        }
    }

    private void turnOnFlash()
    {
        if (fc == null)
            getCamera();
        if (!isFlashOn)
        {
            fc.setFlashlight(true);
            isFlashOn = true;
        }
    }

    private void turnOffFlash()
    {
        if (fc == null)
            getCamera();
        if (isFlashOn)
        {
            fc.setFlashlight(false);
            isFlashOn = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY)
        {
            Log.d(TAG,"GravitySensor.values[1] = "+event.values[1]);
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100)
            {

                lastTime = currentTime;

                if (event.values[1] > 8)
                {
                    isGravityReady = true;
                } else
                {
                    isGravityReady = false;
                }
            }
        }
        else if(event.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            Log.d(TAG,"[LightSensor.values[0] = "+ event.values[0]);
            if(event.values[0]  <= SensorManager.LIGHT_CLOUDY){
                isLightReady = true;
            } else {
                isLightReady = false;
                turnOffFlash();
            }
        }
        else if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
        {
            Log.d(TAG, "[RotationVector.vlaues[0]] = " + event.values[0]);
            if(event.values[0] - mPrevRotationVectorValue > 0.15){
                if(isLightReady && isGravityReady)
                    turnOnFlash();
            } else if(event.values[0] - mPrevRotationVectorValue < -0.15)
                turnOffFlash();
            mPrevRotationVectorValue = event.values[0];
            mRotationVectorZ = event.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void callbackfromAccel(float x, float y, float z){

    }

    public void callbackfromGyro(float x,float y, float z){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"get gyro",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void callbackfromLight(float data){
        if(data  <= SensorManager.LIGHT_CLOUDY){
            isLightReady = true;
        } else {
            isLightReady = false;
            turnOffFlash();
        }
    }

    public void callbackfromHead(float data) {

        if (1 == data) {
            AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
            long eventtime = SystemClock.uptimeMillis() - 1;
            KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY, 0);
            am.dispatchMediaKeyEvent(downEvent);
            eventtime++;
            KeyEvent upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY, 0);
            am.dispatchMediaKeyEvent(upEvent);
        }
    }

}
