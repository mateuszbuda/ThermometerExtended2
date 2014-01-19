package pl.narfsoftware.thermometer.ui.listeners;

import pl.narfsoftware.thermometer.SensorsListViewAdapter;
import pl.narfsoftware.thermometer.ThermometerApp;
import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;

public class PressureListener extends BaseListener {
	static final String TAG = "PressureListener";

	public PressureListener(Context context, SensorsListViewAdapter adapter,
			int position) {
		super(context, adapter, position);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (preferences.showAmbientCondition[ThermometerApp.PRESSURE_INDEX]
				&& event.sensor.equals(app.getPressureSensor())) {
			value = event.values[0];

			stringValue = (String.format("%.0f", value) + " hPa");

			super.onSensorChanged(event);

			Log.d(TAG, "Got pressure sensor event with value: " + value);
		}
	}
}
