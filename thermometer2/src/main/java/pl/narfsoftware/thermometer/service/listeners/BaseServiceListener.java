package pl.narfsoftware.thermometer.service.listeners;

import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.db.SensorData;
import pl.narfsoftware.thermometer.utils.Listener;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public abstract class BaseServiceListener implements Listener,
		SensorEventListener {
	protected float value;
	protected SensorData sensorData;
	protected ThermometerApp app;
	protected Sensors sensors;

	public BaseServiceListener(Context context) {
		value = 0f;
		app = ((ThermometerApp) context.getApplicationContext());
		sensorData = app.getSensorData();
		sensors = app.getSensors();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public abstract void onSensorChanged(SensorEvent event);

	@Override
	public abstract boolean register();

	@Override
	public abstract void unregister();

}
