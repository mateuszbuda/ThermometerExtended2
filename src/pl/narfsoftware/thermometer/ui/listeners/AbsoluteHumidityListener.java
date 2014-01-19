package pl.narfsoftware.thermometer.ui.listeners;

import pl.narfsoftware.thermometer.SensorsListViewAdapter;
import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.text.Html;
import android.util.Log;

/**
 * This listener should be registered on temperature sensor's events and
 * relative humidity ones.
 */
public class AbsoluteHumidityListener extends BaseListener {
	static final String TAG = "AbsoluteHumidityListener";

	private float temperature = 0f;
	private float relativeHumidity = 0f;

	public AbsoluteHumidityListener(Context context,
			SensorsListViewAdapter adapter, int position) {
		super(context, adapter, position);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (preferences.showAmbientCondition[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX]
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

			value = Sensors.computeAbsoluteHumidity(temperature,
					relativeHumidity);

			stringValue = (Html.fromHtml(String.format("%.0f", value)
					+ " g/m<sup><small>3</small></sup>")).toString();

			Log.d(TAG, "Absolute humidity updated with value " + value);

			super.onSensorChanged(event);
		}
	}
}
