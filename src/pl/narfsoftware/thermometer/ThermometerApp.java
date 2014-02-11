package pl.narfsoftware.thermometer;

import java.util.HashMap;
import java.util.Set;

import pl.narfsoftware.thermometer.db.SensorData;
import pl.narfsoftware.thermometer.preferences.Preferences;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.app.Application;
import android.hardware.Sensor;
import android.util.Log;

public class ThermometerApp extends Application {
	static final String TAG = "ThermometerApp";

	private SensorData sensorData;
	private Sensors sensors;
	private Preferences preferences;
	private HashMap<Integer, Boolean> saveData;

	@Override
	public void onCreate() {
		super.onCreate();
		sensorData = new SensorData(this);
		sensors = new Sensors(this);
		preferences = new Preferences(this);

		initSaveData();

		Log.d(TAG, "onCreated");
	}

	private void initSaveData() {
		saveData = new HashMap<Integer, Boolean>();
		saveData.put(Sensor.TYPE_AMBIENT_TEMPERATURE, false);
		saveData.put(Sensor.TYPE_RELATIVE_HUMIDITY, false);
		saveData.put(Sensor.TYPE_PRESSURE, false);
		saveData.put(Sensor.TYPE_LIGHT, false);
		saveData.put(Sensor.TYPE_MAGNETIC_FIELD, false);
		saveData.put(Sensors.TYPE_ABSOLUTE_HUMIDITY, false);
		saveData.put(Sensors.TYPE_DEW_POINT, false);
	}

	/**
	 * Checks if any available sensor is running in data saving mode.
	 * 
	 * @return true if there is at least one such a sensor and false otherwise
	 */
	public boolean saveAnyData() {
		for (int key : saveData.keySet()) {
			if (saveData.get(key) && sensors.hasSensor.get(key))
				return true;
		}

		if (sensors.hasSensor.get(Sensor.TYPE_AMBIENT_TEMPERATURE)
				&& sensors.hasSensor.get(Sensor.TYPE_RELATIVE_HUMIDITY)
				&& (saveData.get(Sensors.TYPE_ABSOLUTE_HUMIDITY) || saveData
						.get(Sensors.TYPE_DEW_POINT)))
			return true;

		return false;
	}

	public SensorData getSensorData() {
		if (sensorData == null)
			sensorData = new SensorData(getApplicationContext());

		return sensorData;
	}

	public Sensors getSensors() {
		if (sensors == null)
			sensors = new Sensors(getApplicationContext());

		return sensors;
	}

	public Preferences getPrefs() {
		if (preferences == null)
			preferences = new Preferences(getApplicationContext());

		return preferences;
	}

	public boolean saveData(int key) {
		return saveData.get(key) != null ? saveData.get(key) : false;
	}

	public boolean setSaveData(int key, boolean save) {
		return saveData.put(key, save);
	}

	public Set<Integer> getSaveDataKeySet() {
		if (saveData == null)
			initSaveData();

		return saveData.keySet();
	}
}
