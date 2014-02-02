package pl.narfsoftware.thermometer;

import java.util.HashMap;

import pl.narfsoftware.thermometer.db.SensorData;
import android.app.Application;
import android.util.Log;

public class ThermometerApp extends Application {
	static final String TAG = "ThermometerApp";

	private SensorData sensorData;
	public HashMap<Integer, Boolean> saveAmbientCondition;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreated");
	}

	public boolean saveAnyAmbientCondition() {
		for (int key : saveAmbientCondition.keySet()) {
			if (saveAmbientCondition.get(key))
				return true;
		}
		return false;
	}

	public SensorData getSensorData() {
		if (sensorData == null)
			sensorData = new SensorData(getApplicationContext());

		return sensorData;
	}
}
