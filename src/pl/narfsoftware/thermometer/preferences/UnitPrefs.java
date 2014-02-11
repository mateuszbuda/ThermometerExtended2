package pl.narfsoftware.thermometer.preferences;

import pl.narfsoftware.thermometer.R;
import android.content.Context;
import android.content.SharedPreferences;

public class UnitPrefs extends BasePrefs {
	private String temperatureUnit;
	private int temperatureUnitCode;
	private String[] tempUnitOptions;

	public UnitPrefs(Context context) {
		super(context);
		key = resources.getString(R.string.prefs_temp_unit_key);
		tempUnitOptions = resources
				.getStringArray(R.array.prefs_temp_unit_vals);

		setTempUnit();
	}

	public int getTempUnitCode() {
		return temperatureUnitCode;
	}

	private void setTempUnit() {
		temperatureUnit = preferences.getString(key,
				tempUnitOptions[Preferences.CELSIUS]);

		if (temperatureUnit.equals(tempUnitOptions[Preferences.CELSIUS]))
			temperatureUnitCode = Preferences.CELSIUS;

		else if (temperatureUnit
				.equals(tempUnitOptions[Preferences.FAHRENHEIT]))
			temperatureUnitCode = Preferences.FAHRENHEIT;

		else
			temperatureUnitCode = Preferences.KELVIN;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(this.key))
			setTempUnit();
	}
}
