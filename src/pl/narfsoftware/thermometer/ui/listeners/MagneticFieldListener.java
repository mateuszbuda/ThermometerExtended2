package pl.narfsoftware.thermometer.ui.listeners;

import pl.narfsoftware.thermometer.SensorsListViewAdapter;
import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;

public class MagneticFieldListener extends BaseListener {
	static final String TAG = "MagneticFieldListener";
	float magneticFieldX = 0f;
	float magneticFieldY = 0f;
	float magneticFieldZ = 0f;

	public MagneticFieldListener(Context context,
			SensorsListViewAdapter adapter, int position) {
		super(context, adapter, position);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (preferences.showAmbientCondition[ThermometerApp.MAGNETIC_FIELD_INDEX]
				&& event.sensor.equals(app.getMagneticFieldSensor())) {
			magneticFieldX = event.values[0];
			magneticFieldY = event.values[1];
			magneticFieldZ = event.values[2];
			value = Sensors.computeMagneticField(magneticFieldX,
					magneticFieldY, magneticFieldZ);

			stringValue = (String.format("%.0f", value) + " " + (char) 0x03BC + "T");

			Log.d(TAG, "Got magnetic field sensor event with value: " + value);

			super.onSensorChanged(event);
		}
	}
}
