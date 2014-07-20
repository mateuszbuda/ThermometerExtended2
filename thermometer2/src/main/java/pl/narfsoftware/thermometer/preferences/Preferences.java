package pl.narfsoftware.thermometer.preferences;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

public class Preferences {
	public static final int CELSIUS = 0;
	public static final int FAHRENHEIT = 1;
	public static final int KELVIN = 2;

	Context context;

	AccuracyToastsPrefs accuracyToastsPrefs;
	BackgroundColorPrefs backgroundColorPrefs;
	DataHintToastPrefs dataHintToastPrefs;
	DateFormatPrefs dateFormatPrefs;
	SensorsPrefs sensorsPrefs;
	ThemePrefs themePrefs;
	TimeFormatPrefs timeFormatPrefs;
	TypefacePrefs typefacePrefs;
	UnitPrefs unitPrefs;

	public Preferences(Context context) {
		this.context = context;

		createPrefs();
		registerPrefs();
	}

	private void createPrefs() {
		accuracyToastsPrefs = new AccuracyToastsPrefs(context);
		backgroundColorPrefs = new BackgroundColorPrefs(context);
		dataHintToastPrefs = new DataHintToastPrefs(context);
		dateFormatPrefs = new DateFormatPrefs(context);
		sensorsPrefs = new SensorsPrefs(context);
		themePrefs = new ThemePrefs(context);
		timeFormatPrefs = new TimeFormatPrefs(context);
		typefacePrefs = new TypefacePrefs(context);
		unitPrefs = new UnitPrefs(context);
	}

	private void registerPrefs() {
		accuracyToastsPrefs.register();
		backgroundColorPrefs.register();
		dateFormatPrefs.register();
		sensorsPrefs.register();
		themePrefs.register();
		timeFormatPrefs.register();
		typefacePrefs.register();
		unitPrefs.register();
	}

	public boolean showAccuracyToast() {
		return accuracyToastsPrefs.showAccuracyToasts();
	}

	public int getBackgroundColor() {
		return backgroundColorPrefs.getBackgroundColor();
	}

	public String getDateFormat() {
		return dateFormatPrefs.getDateFormat();
	}

	public HashMap<Integer, Boolean> getShowDataDict() {
		return sensorsPrefs.getShowDataDict();
	}

	public Drawable getTheme() {
		if (themePrefs.isThemeSet())
			return themePrefs.getThemeDrawable();

		return null;
	}

	public String getTimeFormat() {
		return timeFormatPrefs.getTimeFormat();
	}

	public Typeface getTypeface() {
		return typefacePrefs.getTypeface();
	}

	public int getTempUnitCode() {
		return unitPrefs.getTempUnitCode();
	}

	public void setAccuracy(int sensorType, int accuracy) {
		accuracyToastsPrefs.setAccuracy(sensorType, accuracy);
	}

	public int getAccuracy(int sensorType) {
		return accuracyToastsPrefs.getAccuracy(sensorType);
	}

	public boolean dataHintToastShowed() {
		return dataHintToastPrefs.dataHintToastShowed();
	}

	public void setDataHintToastShowed() {
		dataHintToastPrefs.setDataHintToastShowed();
	}
}
