package pl.narfsoftware.thermometer2;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

public class ThermometerApp extends Application implements
		OnSharedPreferenceChangeListener, SensorEventListener {
	static final String TAG = "ThermometerApp";

	SharedPreferences preferences;
	SensorManager sensorManager;

	static final int TEMPERATURE_INDEX = 0;
	static final int RELATIVE_HUMIDITY_INDEX = 1;
	static final int ABSOLUTE_HUMIDITY_INDEX = 2;
	static final int PRESSURE_INDEX = 3;
	static final int DEW_POINT_INDEX = 4;
	static final int LIGHT_INDEX = 5;
	static final int MAGNETIC_FIELD_INDEX = 6;
	static final int AMBIENT_CONDITIONS_COUNT = 7;

	boolean[] saveAmbientConditionData = new boolean[AMBIENT_CONDITIONS_COUNT];

	boolean[] hasSensor = new boolean[SENSORS_COUNT];

	boolean[] showAmbientCondition = new boolean[AMBIENT_CONDITIONS_COUNT];

	private Sensor[] sensors = new Sensor[SENSORS_COUNT];

	static final int S_TEMPRATURE = 0;
	static final int S_RELATIVE_HUMIDITY = 1;
	static final int S_PRESSURE = 2;
	static final int S_LIGHT = 3;
	static final int S_MAGNETIC_FIELD = 4;
	static final int SENSORS_COUNT = 5;

	@Override
	public void onCreate() {
		super.onCreate();

		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		preferences.registerOnSharedPreferenceChangeListener(this);

		showAmbientCondition[TEMPERATURE_INDEX] = preferences.getBoolean(
				getResources().getString(R.string.ambient_temp_key), true);

		showAmbientCondition[RELATIVE_HUMIDITY_INDEX] = preferences.getBoolean(
				getResources().getString(R.string.relative_humidity_key), true);
		showAmbientCondition[ABSOLUTE_HUMIDITY_INDEX] = preferences
				.getBoolean(
						getResources()
								.getString(R.string.absolute_humidity_key),
						false);
		showAmbientCondition[PRESSURE_INDEX] = preferences.getBoolean(
				getResources().getString(R.string.pressure_key), true);
		showAmbientCondition[DEW_POINT_INDEX] = preferences.getBoolean(
				getResources().getString(R.string.dew_point_key), false);
		showAmbientCondition[LIGHT_INDEX] = preferences.getBoolean(
				getResources().getString(R.string.light_key), false);
		showAmbientCondition[MAGNETIC_FIELD_INDEX] = preferences.getBoolean(
				getResources().getString(R.string.magnetic_field_key), false);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		getSensors();
		checkSensorsAvailability();

		Log.d(TAG, "onCreated");
	}

	public boolean saveAnyAmbientCondition() {
		return saveAmbientConditionData[TEMPERATURE_INDEX]
				|| saveAmbientConditionData[RELATIVE_HUMIDITY_INDEX]
				|| saveAmbientConditionData[ABSOLUTE_HUMIDITY_INDEX]
				|| saveAmbientConditionData[PRESSURE_INDEX]
				|| saveAmbientConditionData[DEW_POINT_INDEX]
				|| saveAmbientConditionData[LIGHT_INDEX]
				|| saveAmbientConditionData[MAGNETIC_FIELD_INDEX];
	}

	private void getSensors() {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			sensors[S_TEMPRATURE] = sensorManager
					.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
		else
			sensors[S_TEMPRATURE] = sensorManager
					.getDefaultSensor(Sensor.TYPE_TEMPERATURE);

		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			sensors[S_RELATIVE_HUMIDITY] = sensorManager
					.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

		sensors[S_PRESSURE] = sensorManager
				.getDefaultSensor(Sensor.TYPE_PRESSURE);

		sensors[S_LIGHT] = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

		sensors[S_MAGNETIC_FIELD] = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	}

	private void checkSensorsAvailability() {
		try {
			hasSensor[S_TEMPRATURE] = sensorManager.registerListener(this,
					sensors[S_TEMPRATURE], SensorManager.SENSOR_DELAY_UI);
		} catch (Exception e) {
			hasSensor[S_TEMPRATURE] = false;
		}
		try {
			hasSensor[S_RELATIVE_HUMIDITY] = sensorManager.registerListener(
					this, sensors[S_RELATIVE_HUMIDITY],
					SensorManager.SENSOR_DELAY_UI);
		} catch (Exception e) {
			hasSensor[S_RELATIVE_HUMIDITY] = false;
		}
		try {
			hasSensor[S_PRESSURE] = sensorManager.registerListener(this,
					sensors[S_PRESSURE], SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "Pressure sensor registered");
		} catch (Exception e) {
			hasSensor[S_PRESSURE] = false;
		}
		try {
			hasSensor[S_LIGHT] = sensorManager.registerListener(this,
					sensors[S_LIGHT], SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "Light sensor registered");
		} catch (Exception e) {
			hasSensor[S_LIGHT] = false;
		}
		try {
			hasSensor[S_MAGNETIC_FIELD] = sensorManager.registerListener(this,
					sensors[S_MAGNETIC_FIELD], SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "Magnetic field sensor registered");
		} catch (Exception e) {
			hasSensor[S_MAGNETIC_FIELD] = false;
		}

		sensorManager.unregisterListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		preferences = sharedPreferences;
		Log.d(TAG, "onSharedPreferenceChanged for key: " + key);

		// get from preferences which sensors to show
		showAmbientCondition[TEMPERATURE_INDEX] = preferences.getBoolean(
				getResources().getString(R.string.ambient_temp_key), true);

		showAmbientCondition[RELATIVE_HUMIDITY_INDEX] = preferences.getBoolean(
				getResources().getString(R.string.relative_humidity_key), true);
		showAmbientCondition[ABSOLUTE_HUMIDITY_INDEX] = preferences
				.getBoolean(
						getResources()
								.getString(R.string.absolute_humidity_key),
						false);
		showAmbientCondition[PRESSURE_INDEX] = preferences.getBoolean(
				getResources().getString(R.string.pressure_key), true);
		showAmbientCondition[DEW_POINT_INDEX] = preferences.getBoolean(
				getResources().getString(R.string.dew_point_key), false);
		showAmbientCondition[LIGHT_INDEX] = preferences.getBoolean(
				getResources().getString(R.string.light_key), false);
		showAmbientCondition[MAGNETIC_FIELD_INDEX] = preferences.getBoolean(
				getResources().getString(R.string.magnetic_field_key), false);
	}

	Sensor getTemperatureSensor() {
		return sensors[S_TEMPRATURE];
	}

	Sensor getRelativeHumiditySensor() {
		return sensors[S_RELATIVE_HUMIDITY];
	}

	Sensor getPressureSensor() {
		return sensors[S_PRESSURE];
	}

	Sensor getLightSensor() {
		return sensors[S_LIGHT];
	}

	Sensor getMagneticFieldSensor() {
		return sensors[S_MAGNETIC_FIELD];
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Implements SensorEventListener just to check sensors availability

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// Implements SensorEventListener just to check sensors availability
	}

}
