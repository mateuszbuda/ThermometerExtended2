package pl.narfsoftware.thermometer2;

import android.util.Log;

public class SensorRow {
	static final String TAG = "SensorRow";

	private int icon;
	private String sensor;
	private String value;

	public SensorRow(int icon, String sensor, String value) {
		this.icon = icon;
		this.sensor = sensor;
		this.value = value;
	}

	public SensorRow(int icon, String sensor, Float value) {
		this.icon = icon;
		this.sensor = sensor;
		this.value = value.toString();
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int iconResId) {
		this.icon = iconResId;
	}

	public String getSensorName() {
		return sensor;
	}

	public void setSensorName(String sensorName) {
		this.sensor = sensorName;
	}

	public String getStringValue() {
		return value;
	}

	public void setStringValue(String value) {
		this.value = value;
	}

	public Float getFloatValue() {
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException e) {
			Log.e(TAG, "Error: " + e.getCause());
		}
		return 0f;
	}

	public void setFloatValue(Float value) {
		this.value = value.toString();
	}

	@Override
	public String toString() {
		return sensor + "\n" + value;
	}
}
