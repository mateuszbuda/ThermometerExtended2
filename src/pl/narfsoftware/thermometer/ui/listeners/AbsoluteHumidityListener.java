package pl.narfsoftware.thermometer.ui.listeners;

import pl.narfsoftware.thermometer.ui.SensorsListViewAdapter;
import pl.narfsoftware.thermometer.utils.SensorRow;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.text.Html;
import android.util.Log;

public class AbsoluteHumidityListener extends BaseUIListener {
	static final String TAG = "AbsoluteHumidityListener";

	private float temperature = 0f;
	private float relativeHumidity = 0f;

	public AbsoluteHumidityListener(Context context,
			SensorsListViewAdapter adapter, SensorRow sensorRow) {
		super(context, adapter, sensorRow);
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

		stringValue = (Html.fromHtml(String.format("%.0f", value)
				+ " g/m<sup><small>3</small></sup>")).toString();

		Log.d(TAG, "Absolute humidity updated with value " + value);

		super.onSensorChanged(event);
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
