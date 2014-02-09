package pl.narfsoftware.thermometer.service.listeners;

import java.sql.Timestamp;
import java.util.Date;

import pl.narfsoftware.thermometer.db.DbHelper;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

public class PressureListener extends BaseServiceListener {
	static final String TAG = ".service.listeners.PressureListener";

	public PressureListener(Context context) {
		super(context);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		value = event.values[0];

		sensorData.insert(DbHelper.TABLE_NAMES.get(Sensor.TYPE_PRESSURE),
				(new Timestamp(new Date().getTime()).getTime()), value);

		Log.d(TAG, "Got pressure sensor event with value: " + value);
	}

	@Override
	public boolean register() {
		return sensors.sensorManager.registerListener(this,
				sensors.sensors.get(Sensor.TYPE_PRESSURE),
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	public void unregister() {
		sensors.sensorManager.unregisterListener(this,
				sensors.sensors.get(Sensor.TYPE_PRESSURE));
	}

}
