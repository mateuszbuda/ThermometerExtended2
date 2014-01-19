package pl.narfsoftware.thermometer.ui.listeners;

import pl.narfsoftware.thermometer.SensorsListViewAdapter;
import pl.narfsoftware.thermometer.ThermometerApp;
import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;

public class LightListener extends BaseListener {
	static final String TAG = "LightListener";

	public LightListener(Context context, SensorsListViewAdapter adapter,
			int position) {
		super(context, adapter, position);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (preferences.showAmbientCondition[ThermometerApp.LIGHT_INDEX]
				&& event.sensor.equals(app.getLightSensor())) {
			value = event.values[0];

			stringValue = (String.format("%.0f", value) + " lx");

			super.onSensorChanged(event);

			Log.d(TAG, "Got light sensor event with value " + value);
		}
	}
}
