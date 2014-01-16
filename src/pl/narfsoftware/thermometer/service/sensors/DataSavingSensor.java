package pl.narfsoftware.thermometer.service.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public abstract class DataSavingSensor implements SensorEventListener {

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public abstract void onSensorChanged(SensorEvent event);

}
