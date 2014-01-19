package pl.narfsoftware.thermometer.service.listeners;

import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.db.SensorData;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public abstract class DataSavingListener implements SensorEventListener {
	float value;
	SensorData sensorData;
	ThermometerApp app;

	public DataSavingListener(Context context) {
		value = 0f;
		app = ((ThermometerApp) context.getApplicationContext());
		sensorData = app.getSensorData();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public abstract void onSensorChanged(SensorEvent event);

}
