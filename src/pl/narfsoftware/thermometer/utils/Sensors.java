package pl.narfsoftware.thermometer.utils;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

@SuppressLint("UseSparseArrays")
public class Sensors {
	public SensorManager sensorManager;
	public HashMap<Integer, Boolean> sensorsAvailability = new HashMap<Integer, Boolean>();
	public HashMap<Integer, Sensor> sensors = new HashMap<Integer, Sensor>();

	public Sensors(Context context) {
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		checkSensorsAvailability();
		setAvailableSensors();
	}

	public SensorManager getManager() {
		return sensorManager;
	}

	private void checkSensorsAvailability() {
		if (sensorManager.getSensorList(Sensor.TYPE_AMBIENT_TEMPERATURE).size() > 0)
			sensorsAvailability.put(Sensor.TYPE_AMBIENT_TEMPERATURE, true);
		else
			sensorsAvailability.put(Sensor.TYPE_AMBIENT_TEMPERATURE, false);

		if (sensorManager.getSensorList(Sensor.TYPE_RELATIVE_HUMIDITY).size() > 0)
			sensorsAvailability.put(Sensor.TYPE_RELATIVE_HUMIDITY, true);
		else
			sensorsAvailability.put(Sensor.TYPE_RELATIVE_HUMIDITY, false);

		if (sensorManager.getSensorList(Sensor.TYPE_PRESSURE).size() > 0)
			sensorsAvailability.put(Sensor.TYPE_PRESSURE, true);
		else
			sensorsAvailability.put(Sensor.TYPE_PRESSURE, false);

		if (sensorManager.getSensorList(Sensor.TYPE_LIGHT).size() > 0)
			sensorsAvailability.put(Sensor.TYPE_LIGHT, true);
		else
			sensorsAvailability.put(Sensor.TYPE_LIGHT, false);

		if (sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).size() > 0)
			sensorsAvailability.put(Sensor.TYPE_MAGNETIC_FIELD, false);
		else
			sensorsAvailability.put(Sensor.TYPE_MAGNETIC_FIELD, false);
	}

	private void setAvailableSensors() {
		for (Integer key : sensorsAvailability.keySet()) {
			if (sensorsAvailability.get(key))
				sensors.put(key, sensorManager.getDefaultSensor(key));
		}
	}

	/**
	 * Computes absolute humidity
	 * 
	 * @param temperature
	 *            in Celsius degrees
	 * @param relativeHumidity
	 *            in %
	 * @return absolute humidity in g/m^3
	 */
	static public float computeAbsoluteHumidity(float temperature,
			float relativeHumidity) {
		return (float) (Constants.ABSOLUTE_HUMIDITY_CONSTANT * (relativeHumidity
				/ Constants.HUNDRED_PERCENT
				* Constants.A
				* Math.exp(Constants.M * temperature
						/ (Constants.TN + temperature)) / (Constants.ZERO_ABSOLUTE + temperature)));
	}

	/**
	 * Computes dew point
	 * 
	 * @param temperature
	 *            in Celsius degrees
	 * @param relativeHumidity
	 *            in %
	 * @return dew point in Celsius degrees
	 */
	static public float computeDewPoint(float temperature,
			float relativeHumidity) {
		double h = Math.log(relativeHumidity / Constants.HUNDRED_PERCENT)
				+ (Constants.M * temperature) / (Constants.TN + temperature);
		return (float) (Constants.TN * h / (Constants.M - h));
	}

	static public float computeMagneticField(float magneticFieldX,
			float magneticFieldY, float magneticFieldZ) {
		return (float) Math.sqrt(magneticFieldX * magneticFieldX
				+ magneticFieldY * magneticFieldY + magneticFieldZ
				* magneticFieldZ);
	}
}
