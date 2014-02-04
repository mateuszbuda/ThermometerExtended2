package pl.narfsoftware.thermometer;

import java.util.HashMap;

import pl.narfsoftware.thermometer.db.SensorData;
import pl.narfsoftware.thermometer.utils.Preferences;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.app.Application;
import android.util.Log;

public class ThermometerApp extends Application {
	static final String TAG = "ThermometerApp";

	private SensorData sensorData;
	private Sensors sensors;
	private Preferences preferences;
	public HashMap<Integer, Boolean> saveAmbientCondition;

	@Override
	public void onCreate() {
		super.onCreate();
		sensorData = new SensorData(this);
		sensors = new Sensors(this);
		preferences = new Preferences(this);
		Log.d(TAG, "onCreated");
	}

	/**
	 * Checks if any available sensor is running in data saving mode.
	 * 
	 * @return true if there is at least one such a sensor and false otherwise
	 */
	public boolean saveAnyAmbientCondition() {
		for (int key : saveAmbientCondition.keySet()) {
			if (saveAmbientCondition.get(key)
					&& sensors.sensorsAvailability.get(key))
				return true;
		}
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
}
