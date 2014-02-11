package pl.narfsoftware.thermometer.preferences;

import pl.narfsoftware.thermometer.R;
import android.content.Context;
import android.content.SharedPreferences;

public class TimeFormatPrefs extends BasePrefs {
	private String timeFormat;

	public TimeFormatPrefs(Context context) {
		super(context);
		key = resources.getString(R.string.prefs_time_format_key);
		timeFormat = resources.getString(R.string.prefs_time_default_format);
		timeFormat = preferences.getString(key, timeFormat);
	}

	public String getTimeFormat() {
		return timeFormat;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(this.key))
			timeFormat = preferences.getString(key, timeFormat);
	}
}
