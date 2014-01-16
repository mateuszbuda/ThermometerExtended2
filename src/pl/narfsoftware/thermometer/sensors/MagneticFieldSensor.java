package pl.narfsoftware.thermometer.sensors;

import pl.narfsoftware.thermometer.SensorsListViewAdapter;
import pl.narfsoftware.thermometer.ThermometerApp;
import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;

public class MagneticFieldSensor extends BaseSensor {
	static final String TAG = "MagneticFieldSensor";
	float magneticFieldX = 0f;
	float magneticFieldY = 0f;
	float magneticFieldZ = 0f;

	public MagneticFieldSensor(Context context, SensorsListViewAdapter adapter,
			int position) {
		super(context, adapter, position);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (preferences.showAmbientCondition[ThermometerApp.MAGNETIC_FIELD_INDEX]
				&& event.sensor.equals(app.getMagneticFieldSensor())) {
			float magneticFieldX = event.values[0];
			float magneticFieldY = event.values[1];
			float magneticFieldZ = event.values[2];
			value = (float) Math.sqrt(magneticFieldX * magneticFieldX
					+ magneticFieldY * magneticFieldY + magneticFieldZ
					* magneticFieldZ);

			stringValue = (String.format("%.0f", value) + " " + (char) 0x03BC + "T");

			Log.d(TAG, "Got magnetic field sensor event: " + value);

			super.onSensorChanged(event);
		}
	}
}
