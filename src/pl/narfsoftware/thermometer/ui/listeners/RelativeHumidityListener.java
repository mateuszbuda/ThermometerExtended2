package pl.narfsoftware.thermometer.ui.listeners;

import pl.narfsoftware.thermometer.ui.SensorsListViewAdapter;
import pl.narfsoftware.thermometer.utils.SensorRow;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

public class RelativeHumidityListener extends BaseUIListener {
	static final String TAG = "RelativeHumidityListener";

	public RelativeHumidityListener(Context context,
			SensorsListViewAdapter adapter, SensorRow sensorRow) {
		super(context, adapter, sensorRow);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (preferences.showAmbientCondition.get(Sensor.TYPE_RELATIVE_HUMIDITY)) {
			if (event.sensor.getType() != Sensor.TYPE_RELATIVE_HUMIDITY)
				return;

			value = event.values[0];
			stringValue = (String.format("%.0f", value) + " %");

			super.onSensorChanged(event);

			Log.d(TAG, "Got relative humidity sensor event with value: "
					+ value);
		}
	}

	@Override
	public boolean register() {
		return sensors.sensorManager.registerListener(this,
				sensors.sensors.get(Sensor.TYPE_RELATIVE_HUMIDITY),
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	public void unregister() {
		sensors.sensorManager.unregisterListener(this,
				sensors.sensors.get(Sensor.TYPE_RELATIVE_HUMIDITY));
	}
}
