package pl.narfsoftware.thermometer2.utils;

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

	static final String UNIT_TEMPERATURE = "[" + (char) 0x00B0 + "C]";
	static final String UNIT_RELATIVE_HUMIDITY = "[%]";
	static final String UNIT_ABSOLUTE_HUMIDITY = Html.fromHtml(
			"[g/m<sup><small>3</small></sup>]").toString();
	static final String UNIT_PRESSURE = "[hPa]";
	static final String UNIT_DEW_POINT = "[" + (char) 0x00B0 + "C]";
	static final String UNIT_LIGHT = "[lx]";
	static final String UNIT_MAGNETIC_FIELD = "[" + (char) 0x03BC + "T]";

	public static final String[] UNITS = { UNIT_TEMPERATURE,
			UNIT_RELATIVE_HUMIDITY, UNIT_ABSOLUTE_HUMIDITY, UNIT_PRESSURE,
			UNIT_DEW_POINT, UNIT_LIGHT, UNIT_MAGNETIC_FIELD };

	public static final int HORIZONTAL_LABELS_COUNT = 4;
	public static final int VERTICAL_LABELS_COUNT = 6;
}
