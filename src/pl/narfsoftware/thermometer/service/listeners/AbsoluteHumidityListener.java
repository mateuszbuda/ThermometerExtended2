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

/**
 * This listener should be registered on temperature sensor's events and
 * relative humidity ones.
 */
public class AbsoluteHumidityListener extends BaseServiceListener {
	static final String TAG = ".service.listeners.AbsoluteHumidityListener";

	private float temperature = 0f;
	private float relativeHumidity = 0f;

	public AbsoluteHumidityListener(Context context) {
		super(context);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
			temperature = event.values[0];
			Log.d(TAG, "Got temperature sensor event with value: "
					+ temperature);
		} else {
			relativeHumidity = event.values[0];
			Log.d(TAG, "Got relative humidity sensor event with value: "
					+ relativeHumidity);
		}

		value = Sensors.computeAbsoluteHumidity(temperature, relativeHumidity);

		sensorData.insert(
				DbHelper.TABLE_NAMES.get(Sensors.TYPE_ABSOLUTE_HUMIDITY),
				(new Timestamp(new Date().getTime()).getTime()), value);

		Log.d(TAG, "Absolute humidity updated with value " + value);
	}

	@Override
	public boolean register() {
		boolean temperature = sensors.sensorManager.registerListener(this,
				sensors.sensors.get(Sensor.TYPE_AMBIENT_TEMPERATURE),
				SensorManager.SENSOR_DELAY_UI);
		boolean relativeHumidity = sensors.sensorManager.registerListener(this,
				sensors.sensors.get(Sensor.TYPE_RELATIVE_HUMIDITY),
				SensorManager.SENSOR_DELAY_UI);

		if (!temperature || !relativeHumidity) {
			unregister();
			return false;
		}

		return true;
	}

	@Override
	public void unregister() {
		sensors.sensorManager.unregisterListener(this,
				sensors.sensors.get(Sensor.TYPE_AMBIENT_TEMPERATURE));
		sensors.sensorManager.unregisterListener(this,
				sensors.sensors.get(Sensor.TYPE_RELATIVE_HUMIDITY));
	}

}
