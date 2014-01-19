package pl.narfsoftware.thermometer.service.listeners;

import java.sql.Timestamp;
import java.util.Date;

import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.db.DbHelper;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

/**
 * This listener should be registered on temperature sensor's events and
 * relative humidity ones.
 */
public class DewPointListener extends DataSavingListener {
	static final String TAG = ".service.listeners.DewPointListener";

	private float temperature = 0f;
	private float relativeHumidity = 0f;

	public DewPointListener(Context context) {
		super(context);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (app.saveAmbientConditionData[ThermometerApp.DEW_POINT_INDEX]
				&& (event.sensor.equals(app.getTemperatureSensor()) || event.sensor
						.equals(app.getRelativeHumiditySensor()))) {
			if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
				temperature = event.values[0];
				Log.d(TAG, "Got temperature sensor event with value: "
						+ temperature);
			} else if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
				relativeHumidity = event.values[0];
				Log.d(TAG, "Got relative humidity sensor event with value: "
						+ relativeHumidity);
			}

			value = Sensors.computeDewPoint(temperature, relativeHumidity);

			sensorData.insert(DbHelper.TABLE_DEW_POINT, (new Timestamp(
					new Date().getTime()).getTime()), value);

			Log.d(TAG, "Dew point updated: " + value);
		}
	}

}
