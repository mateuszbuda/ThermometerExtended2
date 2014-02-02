package pl.narfsoftware.thermometer.ui.listeners;

import pl.narfsoftware.thermometer.R;
import pl.narfsoftware.thermometer.ui.SensorsListViewAdapter;
import pl.narfsoftware.thermometer.utils.Converter;
import pl.narfsoftware.thermometer.utils.Preferences;
import pl.narfsoftware.thermometer.utils.SensorRow;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

public class DewPointListener extends BaseUIListener {
	static final String TAG = "DewPointListener";

	private float temperature = 0f;
	private float relativeHumidity = 0f;

	public DewPointListener(Context context, SensorsListViewAdapter adapter,
			SensorRow sensorRow) {
		super(context, adapter, sensorRow);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (preferences.showAmbientCondition.get(Sensors.TYPE_DEW_POINT)) {
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

			// TODO refactor
			if (preferences.temperatureUnit
					.equals(context.getResources().getStringArray(
							R.array.prefs_temp_unit_vals)[Preferences.CELSIUS]))
				stringValue = (String.format("%.0f", value) + " "
						+ (char) 0x00B0 + "C");
			else if (preferences.temperatureUnit
					.equals(context.getResources().getStringArray(
							R.array.prefs_temp_unit_vals)[Preferences.FAHRENHEIT]))
				stringValue = (String.format("%.0f", Converter
						.ConvertTemperature(value, Preferences.FAHRENHEIT))
						+ " " + (char) 0x00B0 + "F");
			else
				stringValue = (String
						.format("%.0f", Converter.ConvertTemperature(value,
								Preferences.KELVIN)) + " K");

			super.onSensorChanged(event);
			Log.d(TAG, "Dew point updated with value " + value);
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
