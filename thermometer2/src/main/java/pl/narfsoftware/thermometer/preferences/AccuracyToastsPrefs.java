package pl.narfsoftware.thermometer.preferences;

import pl.narfsoftware.thermometer.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;

public class AccuracyToastsPrefs extends BasePrefs {
	private boolean showAccuracyToasts;

	public AccuracyToastsPrefs(Context context) {
		super(context);
		key = resources.getString(R.string.prefs_accuracy_toast_key);
		showAccuracyToasts = preferences.getBoolean(key, true);
	}

	public boolean showAccuracyToasts() {
		return showAccuracyToasts;
	}
	
	public void setAccuracy(int sensorType, int accuracy) {
		preferences.edit().putInt(Integer.toString(sensorType), accuracy)
				.commit();
	}

	public int getAccuracy(int sensorType) {
		return preferences.getInt(Integer.toString(sensorType),
				SensorManager.SENSOR_STATUS_UNRELIABLE);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(this.key))
			showAccuracyToasts = preferences.getBoolean(key, true);
	}
}
