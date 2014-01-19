package pl.narfsoftware.thermometer.ui.listeners;

import pl.narfsoftware.thermometer.R;
import pl.narfsoftware.thermometer.SensorsListViewAdapter;
import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.utils.Converter;
import pl.narfsoftware.thermometer.utils.Preferences;
import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;

public class TemperatureListener extends BaseListener {
	static final String TAG = "TemperatureListener";

	public TemperatureListener(Context context, SensorsListViewAdapter adapter,
			int position) {
		super(context, adapter, position);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (preferences.showAmbientCondition[ThermometerApp.TEMPERATURE_INDEX]
				&& event.sensor.equals(app.getTemperatureSensor())) {
			value = event.values[0];

			// TODO i'm sure it can be done without those if's
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

			Log.d(TAG, "Got temperature sensor event with value: " + value);

			super.onSensorChanged(event);
		}
	}

}
