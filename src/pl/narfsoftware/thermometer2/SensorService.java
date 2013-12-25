package pl.narfsoftware.thermometer2;

import java.sql.Timestamp;
import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

@SuppressWarnings("deprecation")
public class SensorService extends Service implements SensorEventListener {
	static final String TAG = "SensorService";

	ThermometerApp app;
	Preferences preferences;

	SensorData sensorData;

	SensorManager sensorManager;
	Sensor[] sensors = new Sensor[SENSORS_COUNT];

	static final String INTENT_EXTRA_SENSOR = "sensor_to_unregister";

	static final int S_TEMPERATURE = 0;
	static final int S_RELATIVE_HUMIDITY = 1;
	static final int S_PRESSURE = 2;
	static final int S_LIGHT = 3;
	static final int S_MAGNETIC_FIELD = 4;
	static final int SENSORS_COUNT = 5;

	float temperature;
	float relativeHumidity;
	float absoluteHumidity;
	float pressure;
	float dewPoint;
	float light;
	float magneticField;

	@Override
	public void onCreate() {
		super.onCreate();

		app = (ThermometerApp) getApplication();

		sensorData = app.getSensorData();

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			sensors[S_TEMPERATURE] = sensorManager
					.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
			sensors[S_RELATIVE_HUMIDITY] = sensorManager
					.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
		} else
			sensors[S_TEMPERATURE] = sensorManager
					.getDefaultSensor(Sensor.TYPE_TEMPERATURE);

		sensors[S_PRESSURE] = sensorManager
				.getDefaultSensor(Sensor.TYPE_PRESSURE);

		sensors[S_LIGHT] = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

		sensors[S_MAGNETIC_FIELD] = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		Log.d(TAG, "onCreated");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// register chosen sensors
		if (app.hasSensor[ThermometerApp.S_TEMPRATURE]
				&& (preferences.showAmbientCondition[ThermometerApp.TEMPERATURE_INDEX]
						|| preferences.showAmbientCondition[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX] || preferences.showAmbientCondition[ThermometerApp.DEW_POINT_INDEX])) {
			sensorManager.registerListener(this, app.getTemperatureSensor(),
					SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "Temperature sensor registered");
		}
		if (app.hasSensor[ThermometerApp.S_RELATIVE_HUMIDITY]
				&& (preferences.showAmbientCondition[ThermometerApp.RELATIVE_HUMIDITY_INDEX]
						|| preferences.showAmbientCondition[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX] || preferences.showAmbientCondition[ThermometerApp.DEW_POINT_INDEX])) {
			sensorManager.registerListener(this,
					app.getRelativeHumiditySensor(),
					SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "Relative humidity sensor registered");
		}
		if (app.hasSensor[ThermometerApp.S_PRESSURE]
				&& preferences.showAmbientCondition[ThermometerApp.PRESSURE_INDEX]) {
			sensorManager.registerListener(this, app.getPressureSensor(),
					SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "Pressure sensor registered");
		}
		if (app.hasSensor[ThermometerApp.S_LIGHT]
				&& preferences.showAmbientCondition[ThermometerApp.LIGHT_INDEX]) {
			sensorManager.registerListener(this, app.getLightSensor(),
					SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "Light sensor registered");
		}
		if (app.hasSensor[ThermometerApp.S_MAGNETIC_FIELD]
				&& preferences.showAmbientCondition[ThermometerApp.MAGNETIC_FIELD_INDEX]) {
			sensorManager.registerListener(this, app.getMagneticFieldSensor(),
					SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "Magnetic field sensor registered");
		}

		Log.d(TAG, "onStarted");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// unregister sensors, yet no longer need
		sensorManager.unregisterListener(this);
		Log.d(TAG, "Sensors unregistered");

		Log.d(TAG, "onDestroyed");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Date date = new Date();

		if ((preferences.showAmbientCondition[ThermometerApp.TEMPERATURE_INDEX]
				|| preferences.showAmbientCondition[ThermometerApp.DEW_POINT_INDEX] || preferences.showAmbientCondition[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX])
				&& event.sensor.equals(app.getTemperatureSensor())) {
			temperature = event.values[0];

			sensorData.insert(DbHelper.TABLE_TEMPERATUE,
					(new Timestamp(date.getTime()).getTime()), temperature);

			Log.d(TAG, "Got temperature sensor event: " + temperature);
		}

		if ((preferences.showAmbientCondition[ThermometerApp.RELATIVE_HUMIDITY_INDEX]
				|| preferences.showAmbientCondition[ThermometerApp.DEW_POINT_INDEX] || preferences.showAmbientCondition[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX])
				&& event.sensor.equals(app.getRelativeHumiditySensor())) {
			relativeHumidity = event.values[0];

			sensorData.insert(DbHelper.TABLE_RELATIVE_HUMIDITY, (new Timestamp(
					date.getTime()).getTime()), relativeHumidity);

			Log.d(TAG, "Got relative humidity sensor event: "
					+ relativeHumidity);
		}

		if (preferences.showAmbientCondition[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX]
				&& (event.sensor.equals(app.getTemperatureSensor()) || event.sensor
						.equals(app.getRelativeHumiditySensor()))) {
			updateAbsoluteHumidity();
		}

		if (preferences.showAmbientCondition[ThermometerApp.PRESSURE_INDEX]
				&& event.sensor.equals(app.getPressureSensor())) {
			pressure = event.values[0];

			sensorData.insert(DbHelper.TABLE_PRESSURE,
					(new Timestamp(date.getTime()).getTime()), pressure);

			Log.d(TAG, "Got pressure sensor event: " + pressure);
		}

		if (preferences.showAmbientCondition[ThermometerApp.DEW_POINT_INDEX]
				&& (event.sensor.equals(app.getTemperatureSensor()) || event.sensor
						.equals(app.getRelativeHumiditySensor()))) {
			updateDewPoint();
		}

		if (preferences.showAmbientCondition[ThermometerApp.LIGHT_INDEX]
				&& event.sensor.equals(app.getLightSensor())) {
			light = event.values[0];

			sensorData.insert(DbHelper.TABLE_LIGHT,
					(new Timestamp(date.getTime()).getTime()), light);

			Log.d(TAG, "Got light sensor event: " + light);
		}

		if (preferences.showAmbientCondition[ThermometerApp.MAGNETIC_FIELD_INDEX]
				&& event.sensor.equals(app.getMagneticFieldSensor())) {
			float magneticFieldX = event.values[0];
			float magneticFieldY = event.values[1];
			float magneticFieldZ = event.values[2];
			magneticField = (float) Math.sqrt(magneticFieldX * magneticFieldX
					+ magneticFieldY * magneticFieldY + magneticFieldZ
					* magneticFieldZ);

			sensorData.insert(DbHelper.TABLE_MAGNETIC_FIELD, (new Timestamp(
					date.getTime()).getTime()), magneticField);

			Log.d(TAG, "Got magnetic field sensor event: " + magneticField);
		}
	}

	private void updateAbsoluteHumidity() {
		Date date = new Date();

		absoluteHumidity = (float) (Constants.ABSOLUTE_HUMIDITY_CONSTANT * (relativeHumidity
				/ Constants.HUNDRED_PERCENT
				* Constants.A
				* Math.exp(Constants.M * temperature
						/ (Constants.TN + temperature)) / (Constants.ZERO_ABSOLUTE + temperature)));

		sensorData.insert(DbHelper.TABLE_ABSOLUTE_HUMIDITY,
				(new Timestamp(date.getTime()).getTime()), absoluteHumidity);

		Log.d(TAG, "Absolute humidity updated: " + absoluteHumidity);
	}

	private void updateDewPoint() {
		Date date = new Date();

		double h = Math.log(relativeHumidity / Constants.HUNDRED_PERCENT)
				+ (Constants.M * temperature) / (Constants.TN + temperature);
		dewPoint = (float) (Constants.TN * h / (Constants.M - h));

		sensorData.insert(DbHelper.TABLE_DEW_POINT,
				(new Timestamp(date.getTime()).getTime()), dewPoint);

		Log.d(TAG, "Dew point updated: " + dewPoint);
	}
}
