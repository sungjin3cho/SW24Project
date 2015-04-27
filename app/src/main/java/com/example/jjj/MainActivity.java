package com.example.jjj;

import android.app.Activity;
import android.hardware.camera2.*;
import android.hardware.camera2.params.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity implements SensorEventListener
{
	private static final String TAG = MainActivity.class.getSimpleName();
	
	private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;
    private static final int SHAKE_THRESHOLD = 800;
    private static final int DATA_X = 0;
    private static final int DATA_Y = 1;
    private static final int DATA_Z = 2;
    
    private float mPrevRotationVectorValue = 0.0f;
    
    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;
    private Sensor lightSensor;
    private boolean isFlashOn = false;
    private boolean isLightReady = false;
    private boolean isGravityReady = false;
    private FlashlightController fc;
    private Sensor gravitySensor;
    private Sensor rotationVectorSensor;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        
        getCamera();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor,
                    SensorManager.SENSOR_DELAY_GAME);
        if (gravitySensor != null)
            sensorManager.registerListener(this, gravitySensor,
                    SensorManager.SENSOR_DELAY_GAME);
        if(lightSensor != null)
        	sensorManager.registerListener(this, lightSensor,SensorManager.SENSOR_DELAY_GAME);
        if(rotationVectorSensor != null)
        	sensorManager.registerListener(this, rotationVectorSensor,SensorManager.SENSOR_DELAY_NORMAL); 
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
        turnOffFlash();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY)
        {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100)
            {

                lastTime = currentTime;
                x = event.values[DATA_X];
                y = event.values[DATA_Y];
                z = event.values[DATA_Z];
                //Log.d(TAG, "" + x + "," + y + "," + z);
                // speed = Math.abs(x + y + z - lastX - lastY - lastZ) /
                // gabOfTime * 10000;
                if (y > 8)
                {
                    isGravityReady = true;
                } else
                {
                    isGravityReady = false;
                }
                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }
        }
        else if(event.sensor.getType() == Sensor.TYPE_LIGHT)
        {
        	if(event.values[0]  <= SensorManager.LIGHT_FULLMOON){
        		isLightReady = true;
        	} else {
        		isLightReady = false;
        		turnOffFlash();
        	}
        }
        else if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
        {
        	Log.d(TAG, "[RotationVector.vlaues[0]] = "+event.values[0]);
        	if(event.values[0] - mPrevRotationVectorValue > 0.15){
        		if(isLightReady && isGravityReady)
        			turnOnFlash();
        	} else if(event.values[0] - mPrevRotationVectorValue < -0.15)
        		turnOffFlash();
        	mPrevRotationVectorValue = event.values[0];
        }
    }

    // getting camera parameters
    private void getCamera()
    {
        if (fc == null)
        {
            fc = new FlashlightController(this);
        }
    }

    /*
     * Turning On flash
     */
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
}
