package pl.narfsoftware.thermometer;

import java.util.HashMap;

import pl.narfsoftware.thermometer.db.SensorData;
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
	private SensorData sensorData;

	public static final int TEMPERATURE_INDEX = 0;
	public static final int RELATIVE_HUMIDITY_INDEX = 1;
	public static final int ABSOLUTE_HUMIDITY_INDEX = 2;
	public static final int PRESSURE_INDEX = 3;
	public static final int DEW_POINT_INDEX = 4;
	public static final int LIGHT_INDEX = 5;
	public static final int MAGNETIC_FIELD_INDEX = 6;
	public static final int AMBIENT_CONDITIONS_COUNT = 7;

	public boolean[] saveAmbientConditionData = new boolean[AMBIENT_CONDITIONS_COUNT];
	public HashMap<Integer, Boolean> saveData;

	public boolean[] hasSensor = new boolean[SENSORS_COUNT];

	private Sensor[] sensors = new Sensor[SENSORS_COUNT];

	public static final int S_TEMPRATURE = 0;
	public static final int S_RELATIVE_HUMIDITY = 1;
	public static final int S_PRESSURE = 2;
	public static final int S_LIGHT = 3;
	public static final int S_MAGNETIC_FIELD = 4;
	public static final int SENSORS_COUNT = 5;

	@Override
	public void onCreate() {
		super.onCreate();

		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		preferences.registerOnSharedPreferenceChangeListener(this);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		setSensors();
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
		// ///////// REFACTORED ////////// //
		// for (int key : saveData.keySet()) {
		// if (saveData.get(key))
		// return true;
		// }
		// return false;
		// ///////// REFACTORED ////////// //
	}

	private void setSensors() {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			sensors[S_TEMPRATURE] = sensorManager
					.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
		} else {
			sensors[S_TEMPRATURE] = sensorManager
					.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
		}

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
		// setAmbientConditionsToShow();
	}

	public Sensor getTemperatureSensor() {
		return sensors[S_TEMPRATURE];
	}

	public Sensor getRelativeHumiditySensor() {
		return sensors[S_RELATIVE_HUMIDITY];
	}

	public Sensor getPressureSensor() {
		return sensors[S_PRESSURE];
	}

	public Sensor getLightSensor() {
		return sensors[S_LIGHT];
	}

	public Sensor getMagneticFieldSensor() {
		return sensors[S_MAGNETIC_FIELD];
	}

	public SensorData getSensorData() {
		if (sensorData == null)
			sensorData = new SensorData(getApplicationContext());

		return sensorData;
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
