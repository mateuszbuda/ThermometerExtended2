package pl.narfsoftware.thermometer.service;

import java.util.HashMap;

import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.service.listeners.AbsoluteHumidityListener;
import pl.narfsoftware.thermometer.service.listeners.BaseServiceListener;
import pl.narfsoftware.thermometer.service.listeners.DewPointListener;
import pl.narfsoftware.thermometer.service.listeners.LightListener;
import pl.narfsoftware.thermometer.service.listeners.MagneticFieldListener;
import pl.narfsoftware.thermometer.service.listeners.PressureListener;
import pl.narfsoftware.thermometer.service.listeners.RelativeHumidityListener;
import pl.narfsoftware.thermometer.service.listeners.TemperatureListener;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.IBinder;
import android.util.Log;

@SuppressLint("UseSparseArrays")
public class SensorsDataSavingService extends Service {
	static final String TAG = "SensorsDataSavingService";

	ThermometerApp app;
	Sensors sensors;
	HashMap<Integer, BaseServiceListener> listeners = new HashMap<Integer, BaseServiceListener>();

	@Override
	public void onCreate() {
		app = (ThermometerApp) getApplication();
		sensors = app.getSensors();
		initListeners();
		Log.d(TAG, "onCreated");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		for (int key : app.getSaveDataKeySet()) {
			if (app.saveData(key) && app.getPrefs().showData.get(key))
				listeners.get(key).register();
			else
				listeners.get(key).unregister();
		}

		Log.d(TAG, "onStarted");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		for (int key : listeners.keySet()) {
			listeners.get(key).unregister();
		}
		Log.d(TAG, "onDestroyed");
		super.onDestroy();
	}

	private void initListeners() {
		listeners.put(Sensor.TYPE_AMBIENT_TEMPERATURE, new TemperatureListener(
				app));
		listeners.put(Sensor.TYPE_RELATIVE_HUMIDITY,
				new RelativeHumidityListener(app));
		listeners.put(Sensor.TYPE_PRESSURE, new PressureListener(app));
		listeners.put(Sensor.TYPE_LIGHT, new LightListener(app));
		listeners.put(Sensor.TYPE_MAGNETIC_FIELD,
				new MagneticFieldListener(app));
		listeners.put(Sensors.TYPE_ABSOLUTE_HUMIDITY,
				new AbsoluteHumidityListener(app));
		listeners.put(Sensors.TYPE_DEW_POINT, new DewPointListener(app));
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
