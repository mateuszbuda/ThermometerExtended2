package pl.narfsoftware.thermometer.ui.sensors;

import pl.narfsoftware.thermometer.SensorsListViewAdapter;
import pl.narfsoftware.thermometer.ThermometerApp;
import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;

public class RelativeHumiditySensor extends BaseSensor {
	static final String TAG = "RelativeHumiditySensor";

	public RelativeHumiditySensor(Context context,
			SensorsListViewAdapter adapter, int position) {
		super(context, adapter, position);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if ((preferences.showAmbientCondition[ThermometerApp.RELATIVE_HUMIDITY_INDEX]
				|| preferences.showAmbientCondition[ThermometerApp.DEW_POINT_INDEX] || preferences.showAmbientCondition[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX])
				&& event.sensor.equals(app.getRelativeHumiditySensor())) {
			value = event.values[0];

			stringValue = (String.format("%.0f", value) + " %");

			super.onSensorChanged(event);

			Log.d(TAG, "Got relative humidity sensor event: " + value);
		}
	}
}
