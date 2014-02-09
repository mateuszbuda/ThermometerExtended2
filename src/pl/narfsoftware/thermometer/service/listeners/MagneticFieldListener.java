package pl.narfsoftware.thermometer.service.listeners;

import java.sql.Timestamp;
import java.util.Date;

import pl.narfsoftware.thermometer.db.DbHelper;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

public class MagneticFieldListener extends BaseServiceListener {
	static final String TAG = ".service.listeners.MagneticFieldListener";
	float magneticFieldX = 0f;
	float magneticFieldY = 0f;
	float magneticFieldZ = 0f;

	public MagneticFieldListener(Context context) {
		super(context);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		magneticFieldX = event.values[0];
		magneticFieldY = event.values[1];
		magneticFieldZ = event.values[2];
		value = Sensors.computeMagneticField(magneticFieldX, magneticFieldY,
				magneticFieldZ);

		sensorData.insert(DbHelper.TABLE_NAMES.get(Sensor.TYPE_MAGNETIC_FIELD),
				(new Timestamp(new Date().getTime()).getTime()), value);

		Log.d(TAG, "Got magnetic field sensor event with value: " + value);
	}

	@Override
	public boolean register() {
		return sensors.sensorManager.registerListener(this,
				sensors.sensors.get(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	public void unregister() {
		sensors.sensorManager.unregisterListener(this,
				sensors.sensors.get(Sensor.TYPE_MAGNETIC_FIELD));
	}

}
