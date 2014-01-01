package pl.narfsoftware.thermometer.utils;

public class Converter {
	/**
	 * Converts valueFrom from Celsius degrees and returns value converted to
	 * unit with code toUnitCode as in Preferences. If toUnitCode is
	 * Preferences.CELSIUS, unchanged value is returned. In case you pass
	 * unspecified toUnitCode, IllegalArgumentException is thrown.
	 * 
	 * @param valueFrom
	 *            value in Celsius degrees to convert
	 * @param toUnitCode
	 *            unit code defined in Preferences class as static constant
	 * @return converted to value
	 */
	public static float ConvertTemperature(float valueFrom, int toUnitCode) {
		if (Preferences.CELSIUS == toUnitCode)
			return valueFrom;

		else if (toUnitCode == Preferences.FAHRENHEIT)
			return valueFrom * 9 / 5 + 32;

		else if (toUnitCode == Preferences.KELVIN)
			return valueFrom + 273;

		throw new IllegalArgumentException();
	}
}
