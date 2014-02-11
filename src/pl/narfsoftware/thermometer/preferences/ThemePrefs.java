package pl.narfsoftware.thermometer.preferences;

import pl.narfsoftware.thermometer.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

public class ThemePrefs extends BasePrefs {
	private String theme;
	private Drawable themeDrawable = null;

	public ThemePrefs(Context context) {
		super(context);
		key = resources.getString(R.string.prefs_theme_key);
		theme = resources.getString(R.string.prefs_default_theme);
		theme = preferences.getString(key, theme);

		if (!theme.equals("")) {
			try {
				int id = resources.getIdentifier(theme, "drawable", context
						.getApplicationContext().getPackageName());
				themeDrawable = resources.getDrawable(id);
			} catch (OutOfMemoryError e) {
				Toast.makeText(
						context,
						resources.getString(R.string.out_of_memory_error_toast),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public boolean isThemeSet() {
		return !theme.equals("");
	}

	public Drawable getThemeDrawable() {
		return themeDrawable;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(this.key)) {
			theme = preferences.getString(key, theme);

			if (!theme.equals("")) {
				try {
					int id = resources.getIdentifier(theme, "drawable", context
							.getApplicationContext().getPackageName());
					themeDrawable = resources.getDrawable(id);
				} catch (OutOfMemoryError e) {
					Toast.makeText(
							context,
							resources
									.getString(R.string.out_of_memory_error_toast),
							Toast.LENGTH_LONG).show();
				}
			}
		}
	}
}
