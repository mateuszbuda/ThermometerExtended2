package pl.narfsoftware.thermometer.ui.listeners;

import pl.narfsoftware.thermometer.R;
import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.ui.SensorsListViewAdapter;
import pl.narfsoftware.thermometer.utils.Listener;
import pl.narfsoftware.thermometer.utils.Preferences;
import pl.narfsoftware.thermometer.utils.SensorRow;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

public abstract class BaseUIListener implements Listener, SensorEventListener {

	private SensorsListViewAdapter adapter;
	/**
	 * SenorRow associated with particular ambient condition indicator on
	 * adapter's list
	 */
	private SensorRow sensorRow;
	private ThermometerApp app;
	protected Context context;
	protected Preferences preferences;
	protected Sensors sensors;
	protected float value;
	protected String stringValue;

	public BaseUIListener(Context context, SensorsListViewAdapter adapter,
			SensorRow sensorRow) {
		this.app = (ThermometerApp) context.getApplicationContext();
		this.context = context;
		this.adapter = adapter;
		this.sensorRow = sensorRow;
		preferences = app.getPrefs();
		sensors = app.getSensors();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		if (!preferences.showAccuracyToasts)
			return;

		if (preferences.getAccuracy(sensor.getType()) == accuracy)
			return;

		preferences.setAccuracy(sensor.getType(), accuracy);
		String text = sensor.getName();

		// TODO switch ?
		if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_HIGH)
			text += " "
					+ context.getResources().getString(
							R.string.accuracy_high_toast_text);
		else if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM)
			text += " "
					+ context.getResources().getString(
							R.string.accuracy_medium_toast_text);
		else if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW)
			text += " "
					+ context.getResources().getString(
							R.string.accuracy_low_toast_text);
		else if (accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
			text = context.getResources().getString(
					R.string.accuracy_unreliable_toast_text_part_1)
					+ " "
					+ sensor.getName()
					+ " "
					+ context.getResources().getString(
							R.string.accuracy_unreliable_toast_text_part_2);

		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	/**
	 * After computing value and setting it as a string call super to update
	 * adapter.
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		sensorRow.setFloatValue(value);
		sensorRow.setStringValue(stringValue);
		adapter.notifyDataSetChanged();
	}

	@Override
	public abstract boolean register();

	@Override
	public abstract void unregister();

}
