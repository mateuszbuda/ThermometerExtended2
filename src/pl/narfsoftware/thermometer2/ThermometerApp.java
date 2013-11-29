package pl.narfsoftware.thermometer2;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

public class ThermometerApp extends Application implements
		OnSharedPreferenceChangeListener {
	static final String TAG = "ThermometerApp";

	SharedPreferences preferences;

	static final int temperatureIndex = 0;
	static final int relativeHumidityIndex = 1;
	static final int absoluteHumidityIndex = 2;
	static final int pressureIndex = 3;
	static final int dewPointIndex = 4;
	static final int lightIndex = 5;
	static final int magneticFieldIndex = 6;

	boolean saveTemperature = false;
	boolean saveRelativeHumidity = false;
	boolean saveAbsoluteHumidity = false;
	boolean savePressure = false;
	boolean saveDewPoint = false;
	boolean saveLight = false;
	boolean saveMagneticField = false;

	@Override
	public void onCreate() {
		super.onCreate();

		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		preferences.registerOnSharedPreferenceChangeListener(this);

		Log.d(TAG, "onCreated");
	}

	public boolean saveAnySensor() {
		return saveTemperature || saveRelativeHumidity || saveAbsoluteHumidity
				|| savePressure || saveDewPoint || saveLight
				|| saveMagneticField;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		preferences = sharedPreferences;
		Log.d(TAG, "onSharedPreferenceChanged for key: " + key);
	}

}
