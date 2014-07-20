package pl.narfsoftware.thermometer.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class DataHintToastPrefs extends BasePrefs {
	private static final String KEY = "data_hint_toast";
	private boolean dataHintToastShowed;

	public DataHintToastPrefs(Context context) {
		super(context);
		key = KEY;
		dataHintToastShowed = preferences.getBoolean(key, false);
	}

	public boolean dataHintToastShowed() {
		return dataHintToastShowed;
	}

	public void setDataHintToastShowed() {
		dataHintToastShowed = true;
		preferences.edit().putBoolean(key, dataHintToastShowed).commit();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		// this preference is never registered and will be deleted
		// (vide issue #14 on github)
	}

}
