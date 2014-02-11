package pl.narfsoftware.thermometer.preferences;

import java.util.HashMap;
import java.util.HashSet;

import pl.narfsoftware.thermometer.R;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;

public class SensorsPrefs extends BasePrefs {
	private HashMap<Integer, Boolean> showData = new HashMap<Integer, Boolean>();
	private HashSet<String> keySet = new HashSet<String>();

	public SensorsPrefs(Context context) {
		super(context);

		initKeys();
		setShowedData();
	}

	private void initKeys() {
		keySet.add(resources.getString(R.string.ambient_temp_key));
		keySet.add(resources.getString(R.string.relative_humidity_key));
		keySet.add(resources.getString(R.string.absolute_humidity_key));
		keySet.add(resources.getString(R.string.pressure_key));
		keySet.add(resources.getString(R.string.dew_point_key));
		keySet.add(resources.getString(R.string.light_key));
		keySet.add(resources.getString(R.string.magnetic_field_key));
	}

	private void setShowedData() {
		showData.put(
				Sensor.TYPE_AMBIENT_TEMPERATURE,
				preferences.getBoolean(
						resources.getString(R.string.ambient_temp_key), true));

		showData.put(Sensor.TYPE_RELATIVE_HUMIDITY, preferences.getBoolean(
				resources.getString(R.string.relative_humidity_key), true));
		showData.put(Sensors.TYPE_ABSOLUTE_HUMIDITY, preferences.getBoolean(
				resources.getString(R.string.absolute_humidity_key), true));
		showData.put(
				Sensor.TYPE_PRESSURE,
				preferences.getBoolean(
						resources.getString(R.string.pressure_key), true));
		showData.put(
				Sensors.TYPE_DEW_POINT,
				preferences.getBoolean(
						resources.getString(R.string.dew_point_key), true));
		showData.put(Sensor.TYPE_LIGHT, preferences.getBoolean(
				resources.getString(R.string.light_key), true));
		showData.put(
				Sensor.TYPE_MAGNETIC_FIELD,
				preferences.getBoolean(
						resources.getString(R.string.magnetic_field_key), true));
	}

	public HashMap<Integer, Boolean> getShowDataDict() {
		return showData;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (keySet.contains(key)) {
			setShowedData();
		}
	}
}
