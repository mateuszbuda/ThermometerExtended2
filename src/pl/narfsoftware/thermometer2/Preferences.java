package pl.narfsoftware.thermometer2;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.preference.PreferenceManager;

public class Preferences implements OnSharedPreferenceChangeListener {

	Context context;
	SharedPreferences preferences;

	String timeFormat = DEFAULT_TIME_FORMAT;
	String dateFormat = DEFAULT_DATE_FORMAT;
	static final String DEFAULT_TIME_FORMAT = "kk:mm a";
	static final String DEFAULT_DATE_FORMAT = "EEEE, dd MMMM";

	String temperatureUnit;
	static final int CELSIUS = 0;
	static final int FAHRENHEIT = 1;
	static final int KELVIN = 2;

	int backgroundColor;
	static final int BACKGROUND_DEFAULT_COLOR = Color.parseColor("#FFF0F8FF");

	boolean[] showAmbientCondition = new boolean[ThermometerApp.AMBIENT_CONDITIONS_COUNT];

	Preferences(Context context) {
		this.context = context;
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		preferences.registerOnSharedPreferenceChangeListener(this);
		setAmbientConditionsToShow();
	}

	private void setAmbientConditionsToShow() {
		showAmbientCondition[ThermometerApp.TEMPERATURE_INDEX] = preferences
				.getBoolean(
						context.getResources().getString(
								R.string.ambient_temp_key), true);

		showAmbientCondition[ThermometerApp.RELATIVE_HUMIDITY_INDEX] = preferences
				.getBoolean(
						context.getResources().getString(
								R.string.relative_humidity_key), true);
		showAmbientCondition[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX] = preferences
				.getBoolean(
						context.getResources().getString(
								R.string.absolute_humidity_key), false);
		showAmbientCondition[ThermometerApp.PRESSURE_INDEX] = preferences
				.getBoolean(
						context.getResources().getString(R.string.pressure_key),
						true);
		showAmbientCondition[ThermometerApp.DEW_POINT_INDEX] = preferences
				.getBoolean(
						context.getResources()
								.getString(R.string.dew_point_key), false);
		showAmbientCondition[ThermometerApp.LIGHT_INDEX] = preferences
				.getBoolean(context.getResources()
						.getString(R.string.light_key), false);
		showAmbientCondition[ThermometerApp.MAGNETIC_FIELD_INDEX] = preferences
				.getBoolean(
						context.getResources().getString(
								R.string.magnetic_field_key), false);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		preferences = sharedPreferences;

		setAmbientConditionsToShow();

		// set background color
		backgroundColor = (Color.parseColor(preferences.getString(context
				.getResources().getString(R.string.prefs_background_color_key),
				String.valueOf(BACKGROUND_DEFAULT_COLOR))));

		// set temperature unit
		temperatureUnit = preferences
				.getString(
						context.getResources().getString(
								R.string.prefs_temp_unit_key),
						context.getResources().getStringArray(
								R.array.prefs_temp_unit_vals)[0]);

		// set date and time format
		dateFormat = preferences.getString(
				context.getResources()
						.getString(R.string.prefs_date_format_key),
				DEFAULT_DATE_FORMAT);
		timeFormat = preferences.getString(
				context.getResources()
						.getString(R.string.prefs_time_format_key),
				DEFAULT_TIME_FORMAT);
	}

}
