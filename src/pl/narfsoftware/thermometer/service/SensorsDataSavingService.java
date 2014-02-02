package pl.narfsoftware.thermometer.service;

import java.util.HashMap;

import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.service.listeners.BaseServiceListener;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class SensorsDataSavingService extends Service {
	static final String TAG = "SensorsDataSavingService";

	ThermometerApp app;
	Sensors sensors;
	HashMap<Integer, BaseServiceListener> sensorEventListeners;

	@Override
	public void onCreate() {
		app = (ThermometerApp) getApplication();
		sensors = new Sensors(getApplicationContext());
		Log.d(TAG, "onCreated");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		for (int key : app.saveAmbientCondition.keySet()) {
			if (app.saveAmbientCondition.get(key))
				sensors.getManager()
						.registerListener(sensorEventListeners.get(key),
								sensors.sensors.get(key),
								SensorManager.SENSOR_DELAY_UI);
			else
				sensors.getManager().unregisterListener(
						sensorEventListeners.get(key));
		}

		Log.d(TAG, "onStarted");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		for (Integer key : sensorEventListeners.keySet()) {
			sensors.getManager().unregisterListener(
					sensorEventListeners.get(key));
		}
		Log.d(TAG, "onDestroyed");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
