package pl.narfsoftware.thermometer.preferences;

import pl.narfsoftware.thermometer.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

public class BackgroundColorPrefs extends BasePrefs {
	private int backgroundColor;
	private String colorString;

	public BackgroundColorPrefs(Context context) {
		super(context);
		key = resources.getString(R.string.prefs_background_color_key);
		colorString = resources
				.getString(R.string.prefs_background_default_color);
		backgroundColor = Color.parseColor(preferences.getString(key,
				colorString));
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(this.key))
			backgroundColor = Color.parseColor(preferences.getString(key,
					colorString));
	}
}
