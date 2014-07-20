package pl.narfsoftware.thermometer.utils;

import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.db.SensorData;
import pl.narfsoftware.thermometer.preferences.Preferences;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;

/**
 * Updates graph view when data saving is turned on
 */
public class TimerRunnable implements Runnable {
	static final String TAG = "TimerRunnable";

	boolean saveData;
	GraphViewSeries dataSeries;
	SensorData sensorData;
	String tableName;
	GraphView graphView;
	Handler handler;
	Preferences prefs;

	public TimerRunnable(Context context, boolean sd, GraphViewSeries gvs,
			SensorData sda, String tn, GraphView gv, Handler h) {
		saveData = sd;
		dataSeries = gvs;
		sensorData = sda;
		tableName = tn;
		graphView = gv;
		handler = h;
		prefs = ((ThermometerApp) context.getApplicationContext()).getPrefs();
	}

	@Override
	public void run() {
		if (saveData) {
			dataSeries.resetData(sensorData.query(tableName,
					prefs.getTempUnitCode()));
			Log.d(TAG, "data rows count: " + dataSeries.getValues().length);

			graphView.scrollToEnd();
		}
		handler.postDelayed(this, Constants.ONE_SECOND);
	}

	public void setSaveData(boolean saveData) {
		this.saveData = saveData;
	}

}
