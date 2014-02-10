package pl.narfsoftware.thermometer.utils;

import java.util.HashMap;

import pl.narfsoftware.thermometer.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

// TODO needs refactoring
// create subclasses, each one responding to particular key:
// DateAndTimePReferences, TemperatureUnitPreferences, BackgroundColorPreferences
// TypefacePreferences, ThemePreferences, AccuracyToastsPreferences, 
public class Preferences implements OnSharedPreferenceChangeListener {

	Context context;
	SharedPreferences preferences;
	Resources res;

	// TODO getters and setters... (?)
	public String timeFormat = DEFAULT_TIME_FORMAT;
	public String dateFormat = DEFAULT_DATE_FORMAT;
	// ?
	static final String DEFAULT_TIME_FORMAT = "KK:mm a";
	static final String DEFAULT_DATE_FORMAT = "EEEE, dd MMMM";

	// ?
	public String temperatureUnit;
	public int temperatureUnitCode;
	public static final int CELSIUS = 0;
	public static final int FAHRENHEIT = 1;
	public static final int KELVIN = 2;

	// ?
	public int backgroundColor;
	static final int BACKGROUND_DEFAULT_COLOR = Color.parseColor("#FFF0F8FF");
	private final String BACKGROUND_DEFAULT_COLOR_STRING = "#FFF0F8FF";

	// ?
	public String fontTypeface;
	static final String DEFAULT_FONT_TYPEFACE = "Roboto.ttf";
	public Typeface typeface;

	// ?
	public String theme;
	static final String DEFAULT_THEME = "";
	public Drawable themeDrawable;

	public HashMap<Integer, Boolean> showData = new HashMap<Integer, Boolean>();

	private static String DATA_HINT_TOAST_SHOWED_KEY = "data_hint_toast_showed";
	public boolean dataHintToastShowed;

	public boolean showAccuracyToasts = true;

	public Preferences(Context context) {
		this.context = context;
		res = context.getResources();
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		preferences.registerOnSharedPreferenceChangeListener(this);
		setAmbientConditionsToShow();
		setCustomizationPreferences();
		initDataHintToastShowed();
	}

	public void setAccuracy(int sensorType, int accuracy) {
		preferences.edit().putInt(Integer.toString(sensorType), accuracy)
				.commit();
	}

	public int getAccuracy(int sensorType) {
		return preferences.getInt(Integer.toString(sensorType),
				SensorManager.SENSOR_STATUS_UNRELIABLE);
	}

	private void setAmbientConditionsToShow() {
		showData.put(Sensor.TYPE_AMBIENT_TEMPERATURE, preferences
				.getBoolean(res.getString(R.string.ambient_temp_key), true));

		showData.put(
				Sensor.TYPE_RELATIVE_HUMIDITY,
				preferences.getBoolean(
						res.getString(R.string.relative_humidity_key), true));
		showData.put(
				Sensors.TYPE_ABSOLUTE_HUMIDITY,
				preferences.getBoolean(
						res.getString(R.string.absolute_humidity_key), false));
		showData.put(Sensor.TYPE_PRESSURE, preferences.getBoolean(
				res.getString(R.string.pressure_key), true));
		showData.put(Sensors.TYPE_DEW_POINT, preferences
				.getBoolean(res.getString(R.string.dew_point_key), false));
		showData.put(Sensor.TYPE_LIGHT, preferences.getBoolean(
				res.getString(R.string.light_key), false));
		showData.put(
				Sensor.TYPE_MAGNETIC_FIELD,
				preferences.getBoolean(
						res.getString(R.string.magnetic_field_key), false));
	}

	private void setCustomizationPreferences() {
		// set background color
		backgroundColor = (Color.parseColor(preferences.getString(context
				.getResources().getString(R.string.prefs_background_color_key),
				BACKGROUND_DEFAULT_COLOR_STRING)));

		// set temperature unit
		temperatureUnit = preferences
				.getString(
						context.getResources().getString(
								R.string.prefs_temp_unit_key),
						context.getResources().getStringArray(
								R.array.prefs_temp_unit_vals)[0]);
		if (temperatureUnit.equals(context.getResources().getStringArray(
				R.array.prefs_temp_unit_vals)[CELSIUS]))
			temperatureUnitCode = CELSIUS;

		else if (temperatureUnit.equals(context.getResources().getStringArray(
				R.array.prefs_temp_unit_vals)[FAHRENHEIT]))
			temperatureUnitCode = FAHRENHEIT;

		else
			temperatureUnitCode = KELVIN;

		// set date and time format
		dateFormat = preferences.getString(
				context.getResources()
						.getString(R.string.prefs_date_format_key),
				DEFAULT_DATE_FORMAT);
		timeFormat = preferences.getString(
				context.getResources()
						.getString(R.string.prefs_time_format_key),
				DEFAULT_TIME_FORMAT);

		// set font typeface
		fontTypeface = preferences.getString(
				context.getResources().getString(R.string.prefs_font_key),
				DEFAULT_FONT_TYPEFACE);
		typeface = Typeface.createFromAsset(context.getAssets(), fontTypeface);

		// set theme
		theme = preferences.getString(
				context.getResources().getString(R.string.prefs_theme_key),
				DEFAULT_THEME);
		if (!theme.equals("")) {
			try {
				themeDrawable = context.getResources().getDrawable(
						context.getResources().getIdentifier(
								theme,
								"drawable",
								context.getApplicationContext()
										.getPackageName()));
			} catch (OutOfMemoryError e) {
				Toast.makeText(
						context,
						context.getResources().getString(
								R.string.out_of_memory_error_toast),
						Toast.LENGTH_LONG).show();
			}
		}

		// set showing accuracy toast
		showAccuracyToasts = preferences.getBoolean(context.getResources()
				.getString(R.string.prefs_accuracy_toast_key), true);
	}

	private void initDataHintToastShowed() {
		dataHintToastShowed = preferences.getBoolean(
				DATA_HINT_TOAST_SHOWED_KEY, false);
	}

	public void dataHintToastShowed() {
		dataHintToastShowed = true;
		preferences.edit()
				.putBoolean(DATA_HINT_TOAST_SHOWED_KEY, dataHintToastShowed)
				.commit();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		preferences = sharedPreferences;
		// TODO Set only setting with this particular key (?)
		setAmbientConditionsToShow();
		setCustomizationPreferences();
	}

}
