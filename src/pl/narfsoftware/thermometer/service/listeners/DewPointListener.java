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
public class DewPointListener extends BaseServiceListener {
	static final String TAG = ".service.listeners.DewPointListener";

	private float temperature = 0f;
	private float relativeHumidity = 0f;

	public DewPointListener(Context context) {
		super(context);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (app.saveAmbientCondition.get(Sensors.TYPE_DEW_POINT)) {
			if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
				temperature = event.values[0];
				Log.d(TAG, "Got temperature sensor event with value: "
						+ temperature);
			} else if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
				relativeHumidity = event.values[0];
				Log.d(TAG, "Got relative humidity sensor event with value: "
						+ relativeHumidity);
			} else
				return;

			value = Sensors.computeDewPoint(temperature, relativeHumidity);

			sensorData.insert(DbHelper.TABLE_DEW_POINT, (new Timestamp(
					new Date().getTime()).getTime()), value);

			Log.d(TAG, "Dew point updated: " + value);
		}
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
