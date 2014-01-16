package pl.narfsoftware.thermometer.service;

import java.util.HashMap;

import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.service.sensors.DataSavingSensor;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class SensorsDataSavingService extends Service {
	static final String TAG = "SensorsDataSavingService";

	ThermometerApp app;
	Sensors sensors;
	SensorManager sensorManager;
	HashMap<Integer, DataSavingSensor> sensorEventListeners;

	@Override
	public void onCreate() {
		app = (ThermometerApp) getApplication();
		sensors = new Sensors(getApplicationContext());
		sensorManager = (SensorManager) getApplicationContext()
				.getSystemService(Context.SENSOR_SERVICE);
		Log.d(TAG, "onCreated");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		for (int key : app.saveData.keySet()) {
			if (app.saveData.get(key))
				sensorManager
						.registerListener(sensorEventListeners.get(key),
								sensors.sensors.get(key),
								SensorManager.SENSOR_DELAY_UI);
			else
				sensorManager.unregisterListener(sensorEventListeners.get(key));
		}

		Log.d(TAG, "onStarted");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		for (Integer key : sensorEventListeners.keySet()) {
			sensorManager.unregisterListener(sensorEventListeners.get(key));
		}
		Log.d(TAG, "onDestroyed");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
