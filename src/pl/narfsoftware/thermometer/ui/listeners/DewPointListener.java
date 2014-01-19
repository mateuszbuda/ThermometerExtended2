package pl.narfsoftware.thermometer.ui.listeners;

import pl.narfsoftware.thermometer.R;
import pl.narfsoftware.thermometer.SensorsListViewAdapter;
import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.utils.Converter;
import pl.narfsoftware.thermometer.utils.Preferences;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

/**
 * This listener should be registered on temperature sensor's events and
 * relative humidity ones.
 */
public class DewPointListener extends BaseListener {
	static final String TAG = "DewPointListener";

	private float temperature = 0f;
	private float relativeHumidity = 0f;

	public DewPointListener(Context context, SensorsListViewAdapter adapter,
			int position) {
		super(context, adapter, position);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (preferences.showAmbientCondition[ThermometerApp.DEW_POINT_INDEX]
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

			Log.d(TAG, "Dew point updated with value " + value);

			super.onSensorChanged(event);
		}
	}
}
