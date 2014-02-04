package pl.narfsoftware.thermometer.ui.listeners;

import pl.narfsoftware.thermometer.ui.SensorsListViewAdapter;
import pl.narfsoftware.thermometer.utils.SensorRow;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

public class MagneticFieldListener extends BaseUIListener {
	static final String TAG = "MagneticFieldListener";
	float magneticFieldX = 0f;
	float magneticFieldY = 0f;
	float magneticFieldZ = 0f;

	public MagneticFieldListener(Context context,
			SensorsListViewAdapter adapter, SensorRow sensorRow) {
		super(context, adapter, sensorRow);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		magneticFieldX = event.values[0];
		magneticFieldY = event.values[1];
		magneticFieldZ = event.values[2];
		value = Sensors.computeMagneticField(magneticFieldX, magneticFieldY,
				magneticFieldZ);

		stringValue = (String.format("%.0f", value) + " " + (char) 0x03BC + "T");

		Log.d(TAG, "Got magnetic field sensor event with value: " + value);

		super.onSensorChanged(event);
	}

	@Override
	public boolean register() {
		return sensors.sensorManager.registerListener(this,
				sensors.sensors.get(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	public void unregister() {
		sensors.sensorManager.unregisterListener(this,
				sensors.sensors.get(Sensor.TYPE_MAGNETIC_FIELD));
	}
}
