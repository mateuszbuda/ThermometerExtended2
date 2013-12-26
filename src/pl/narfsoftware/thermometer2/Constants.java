package pl.narfsoftware.thermometer2;

public class Constants {
	static final double A = 6.112;
	static final double M = 17.62;
	static final double TN = 243.12;
	static final double ZERO_ABSOLUTE = 273.15;
	static final double HUNDRED_PERCENT = 100.0;
	static final double FAHRENHEIT_FACTOR = 5 / 9;
	static final double FAHRENHEIT_CONSTANT = 32;
	static final double ABSOLUTE_HUMIDITY_CONSTANT = 216.7;
	static final int ONE_SECOND = 1000;
	static final long DAY = 24 * 60 * 60 * ONE_SECOND;
	static final String DATE_FORMAT_TODAY = "HH:mm:ss";
	static final String DATE_FORMAT_OLDER = "d/M/yy";
}
