package pl.narfsoftware.thermometer.preferences;

import pl.narfsoftware.thermometer.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

public class TypefacePrefs extends BasePrefs {
	private String typefaceString;
	private Typeface typeface;

	public TypefacePrefs(Context context) {
		super(context);
		key = resources.getString(R.string.prefs_font_key);
		typefaceString = resources.getString(R.string.prefs_default_typeface);
		typefaceString = preferences.getString(key, typefaceString);
		typeface = Typeface
				.createFromAsset(context.getAssets(), typefaceString);
	}

	public Typeface getTypeface() {
		return typeface;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(this.key)) {
			typefaceString = preferences.getString(key, typefaceString);
			typeface = Typeface.createFromAsset(context.getAssets(),
					typefaceString);
		}
	}
}
