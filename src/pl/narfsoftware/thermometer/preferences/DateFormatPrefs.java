package pl.narfsoftware.thermometer.preferences;

import pl.narfsoftware.thermometer.R;
import android.content.Context;
import android.content.SharedPreferences;

public class DateFormatPrefs extends BasePrefs {
	private String dateFormat;

	public DateFormatPrefs(Context context) {
		super(context);
		key = resources.getString(R.string.prefs_date_format_key);
		dateFormat = resources.getString(R.string.prefs_date_default_format);
		dateFormat = preferences.getString(key, dateFormat);
	}

	public String getDateFormat() {
		return dateFormat;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(this.key))
			dateFormat = preferences.getString(key, dateFormat);
	}
}
