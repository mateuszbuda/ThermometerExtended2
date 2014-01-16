package pl.narfsoftware.thermometer.ui.sensors;

import pl.narfsoftware.thermometer.R;
import pl.narfsoftware.thermometer.SensorsListViewAdapter;
import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.utils.Constants;
import pl.narfsoftware.thermometer.utils.Preferences;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

/**
 * This listener should be registered on temperature sensor's events and
 * relative humidity ones.
 */
public class DewPointSensor extends BaseSensor {
	static final String TAG = "DewPointSensor";

	private float temperature = 0f;
	private float relativeHumidity = 0f;

	public DewPointSensor(Context context, SensorsListViewAdapter adapter,
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

			double h = Math.log(relativeHumidity / Constants.HUNDRED_PERCENT)
					+ (Constants.M * temperature)
					/ (Constants.TN + temperature);
			value = (float) (Constants.TN * h / (Constants.M - h));

			if (preferences.temperatureUnit
					.equals(context.getResources().getStringArray(
							R.array.prefs_temp_unit_vals)[Preferences.CELSIUS]))
				stringValue = (String.format("%.0f", value) + " "
						+ (char) 0x00B0 + "C");
			else if (preferences.temperatureUnit
					.equals(context.getResources().getStringArray(
							R.array.prefs_temp_unit_vals)[Preferences.FAHRENHEIT]))
				stringValue = (String.format("%.0f", value
						* Constants.FAHRENHEIT_FACTOR
						+ Constants.FAHRENHEIT_CONSTANT)
						+ " " + (char) 0x00B0 + "F");
			else
				stringValue = (String.format("%.0f", value
						+ Constants.ZERO_ABSOLUTE) + " K");

			Log.d(TAG, "Dew point updated with value " + value);

			super.onSensorChanged(event);
		}
	}
}
