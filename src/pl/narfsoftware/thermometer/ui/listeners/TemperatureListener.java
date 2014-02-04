package pl.narfsoftware.thermometer.ui.listeners;

import pl.narfsoftware.thermometer.R;
import pl.narfsoftware.thermometer.ui.SensorsListViewAdapter;
import pl.narfsoftware.thermometer.utils.Converter;
import pl.narfsoftware.thermometer.utils.Preferences;
import pl.narfsoftware.thermometer.utils.SensorRow;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

public class TemperatureListener extends BaseUIListener {
	static final String TAG = "TemperatureListener";

	public TemperatureListener(Context context, SensorsListViewAdapter adapter,
			SensorRow sensorRow) {
		super(context, adapter, sensorRow);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		value = event.values[0];

		if (preferences.temperatureUnit
				.equals(context.getResources().getStringArray(
						R.array.prefs_temp_unit_vals)[Preferences.CELSIUS]))
			stringValue = (String.format("%.0f", value) + " " + (char) 0x00B0 + "C");

		else if (preferences.temperatureUnit
				.equals(context.getResources().getStringArray(
						R.array.prefs_temp_unit_vals)[Preferences.FAHRENHEIT]))
			stringValue = (String
					.format("%.0f", Converter.ConvertTemperature(value,
							Preferences.FAHRENHEIT))
					+ " " + (char) 0x00B0 + "F");

		else
			stringValue = (String.format("%.0f",
					Converter.ConvertTemperature(value, Preferences.KELVIN)) + " K");

		Log.d(TAG, "Got temperature sensor event with value: " + value);

		super.onSensorChanged(event);
	}

	@Override
	public boolean register() {
		return sensors.sensorManager.registerListener(this,
				sensors.sensors.get(Sensor.TYPE_AMBIENT_TEMPERATURE),
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	public void unregister() {
		sensors.sensorManager.unregisterListener(this,
				sensors.sensors.get(Sensor.TYPE_AMBIENT_TEMPERATURE));
	}

}
