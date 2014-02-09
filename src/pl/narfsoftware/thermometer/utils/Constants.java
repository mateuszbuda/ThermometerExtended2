package pl.narfsoftware.thermometer.utils;

import java.util.HashMap;

import android.hardware.Sensor;
import android.text.Html;

public class Constants {
	public static final double A = 6.112;
	public static final double M = 17.62;
	public static final double TN = 243.12;
	public static final double ZERO_ABSOLUTE = 273.15;
	public static final double HUNDRED_PERCENT = 100.0;
	public static final double FAHRENHEIT_FACTOR = 5 / 9;
	public static final double FAHRENHEIT_CONSTANT = 32;
	public static final double ABSOLUTE_HUMIDITY_CONSTANT = 216.7;

	public static final int ONE_SECOND = 1000;
	public static final long DAY = 24 * 60 * 60 * ONE_SECOND;
	public static final String DATE_FORMAT_TODAY = "HH:mm:ss";
	public static final String DATE_FORMAT_OLDER = "d/M/yy";

	static final String UNIT_TEMPERATURE_CELSIUS = "[" + (char) 0x00B0 + "C]";
	static final String UNIT_TEMPERATURE_FAHRENHEIT = "[" + (char) 0x00B0
			+ "F]";
	static final String UNIT_TEMPERATURE_KELVIN = "[K]";
	static final String UNIT_RELATIVE_HUMIDITY = "[%]";
	static final String UNIT_ABSOLUTE_HUMIDITY = Html.fromHtml(
			"[g/m<sup><small>3</small></sup>]").toString();
	static final String UNIT_PRESSURE = "[hPa]";
	static final String UNIT_DEW_POINT_CELSIUS = UNIT_TEMPERATURE_CELSIUS;
	static final String UNIT_DEW_POINT_KELVIN = UNIT_TEMPERATURE_KELVIN;
	static final String UNIT_DEW_POINT_FAHRENHEIT = UNIT_TEMPERATURE_FAHRENHEIT;
	static final String UNIT_LIGHT = "[lx]";
	static final String UNIT_MAGNETIC_FIELD = "[" + (char) 0x03BC + "T]";

	static final HashMap<Integer, String> CELSIUS_UNITS = new HashMap<Integer, String>() {
		{
			put(Sensor.TYPE_AMBIENT_TEMPERATURE, UNIT_TEMPERATURE_CELSIUS);
			put(Sensor.TYPE_RELATIVE_HUMIDITY, UNIT_RELATIVE_HUMIDITY);
			put(Sensors.TYPE_ABSOLUTE_HUMIDITY, UNIT_ABSOLUTE_HUMIDITY);
			put(Sensor.TYPE_PRESSURE, UNIT_PRESSURE);
			put(Sensors.TYPE_DEW_POINT, UNIT_DEW_POINT_CELSIUS);
			put(Sensor.TYPE_LIGHT, UNIT_LIGHT);
			put(Sensor.TYPE_MAGNETIC_FIELD, UNIT_MAGNETIC_FIELD);
		};
	};
	static final HashMap<Integer, String> FAHRENHEIT_UNITS = new HashMap<Integer, String>() {
		{
			put(Sensor.TYPE_AMBIENT_TEMPERATURE, UNIT_TEMPERATURE_FAHRENHEIT);
			put(Sensor.TYPE_RELATIVE_HUMIDITY, UNIT_RELATIVE_HUMIDITY);
			put(Sensors.TYPE_ABSOLUTE_HUMIDITY, UNIT_ABSOLUTE_HUMIDITY);
			put(Sensor.TYPE_PRESSURE, UNIT_PRESSURE);
			put(Sensors.TYPE_DEW_POINT, UNIT_DEW_POINT_FAHRENHEIT);
			put(Sensor.TYPE_LIGHT, UNIT_LIGHT);
			put(Sensor.TYPE_MAGNETIC_FIELD, UNIT_MAGNETIC_FIELD);
		};
	};
	static final HashMap<Integer, String> KELVIN_UNITS = new HashMap<Integer, String>() {
		{
			put(Sensor.TYPE_AMBIENT_TEMPERATURE, UNIT_TEMPERATURE_KELVIN);
			put(Sensor.TYPE_RELATIVE_HUMIDITY, UNIT_RELATIVE_HUMIDITY);
			put(Sensors.TYPE_ABSOLUTE_HUMIDITY, UNIT_ABSOLUTE_HUMIDITY);
			put(Sensor.TYPE_PRESSURE, UNIT_PRESSURE);
			put(Sensors.TYPE_DEW_POINT, UNIT_DEW_POINT_KELVIN);
			put(Sensor.TYPE_LIGHT, UNIT_LIGHT);
			put(Sensor.TYPE_MAGNETIC_FIELD, UNIT_MAGNETIC_FIELD);
		};
	};

	public static final HashMap<Integer, String>[] UNITS = (HashMap<Integer, String>[]) new HashMap[] {
			CELSIUS_UNITS, FAHRENHEIT_UNITS, KELVIN_UNITS };

	public static final int HORIZONTAL_LABELS_COUNT = 4;
	public static final int VERTICAL_LABELS_COUNT = 6;
}
