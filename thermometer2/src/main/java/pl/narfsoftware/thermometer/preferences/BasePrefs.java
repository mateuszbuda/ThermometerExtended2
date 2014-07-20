package pl.narfsoftware.thermometer.preferences;

import pl.narfsoftware.thermometer.utils.Listener;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.preference.PreferenceManager;

public abstract class BasePrefs implements Listener,
		OnSharedPreferenceChangeListener {

	protected Context context;
	protected SharedPreferences preferences;
	protected Resources resources;
	/**
	 * Key of particular preference should be set in its own constructor.
	 */
	protected String key;

	public BasePrefs(Context context) {
		this.context = context;
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		resources = context.getResources();
	}

	@Override
	public boolean register() {
		preferences.registerOnSharedPreferenceChangeListener(this);
		return true;
	}

	@Override
	public void unregister() {
		preferences.unregisterOnSharedPreferenceChangeListener(this);
	}
}
