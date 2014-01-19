package pl.narfsoftware.thermometer.ui.listeners;

import pl.narfsoftware.thermometer.R;
import pl.narfsoftware.thermometer.SensorsListViewAdapter;
import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.utils.Preferences;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

public abstract class BaseListener implements SensorEventListener {

	public static final int TYPE_ABSOLUTE_HUMIDITY = Sensor.TYPE_AMBIENT_TEMPERATURE
			* 9 + Sensor.TYPE_RELATIVE_HUMIDITY * 9;
	public static final int TYPE_DEW_POINT = Sensor.TYPE_AMBIENT_TEMPERATURE
			* 10 + Sensor.TYPE_RELATIVE_HUMIDITY * 10;

	/**
	 * Value get from sensor event
	 */
	protected float value;
	/**
	 * Value converted to string with unit of measure attached
	 */
	protected String stringValue;
	protected SensorsListViewAdapter adapter;
	/**
	 * Position of item (SenorRow) associated with particular ambient condition
	 * indicator on adapter's list
	 */
	protected int position = -1;
	protected ThermometerApp app;
	protected Context context;
	protected Preferences preferences;

	public BaseListener(Context context, SensorsListViewAdapter adapter,
			int position) {
		this.context = context;
		this.app = (ThermometerApp) context.getApplicationContext();
		this.adapter = adapter;
		this.position = position;
		preferences = new Preferences(context);
		value = 0f;
		stringValue = context.getResources().getString(R.string.sensor_no_data);
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
	 * In subclasses after counting values and setting stringValue call super to
	 * update list view adapter. {@inheritDoc}
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (position < 0 || position >= adapter.getCount())
			return;

		adapter.getItem(position).setStringValue(stringValue);

		adapter.notifyDataSetChanged();
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
