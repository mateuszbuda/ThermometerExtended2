package pl.narfsoftware.thermometer.ui.listeners;

import pl.narfsoftware.thermometer.SensorsListViewAdapter;
import pl.narfsoftware.thermometer.ThermometerApp;
import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;

public class RelativeHumidityListener extends BaseListener {
	static final String TAG = "RelativeHumidityListener";

	public RelativeHumidityListener(Context context,
			SensorsListViewAdapter adapter, int position) {
		super(context, adapter, position);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (preferences.showAmbientCondition[ThermometerApp.RELATIVE_HUMIDITY_INDEX] && event.sensor
				.equals(app.getRelativeHumiditySensor())) {
			value = event.values[0];

			stringValue = (String.format("%.0f", value) + " %");

			super.onSensorChanged(event);

			Log.d(TAG, "Got relative humidity sensor event with value: "
					+ value);
		}
	}
}
