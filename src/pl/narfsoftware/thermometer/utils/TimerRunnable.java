package pl.narfsoftware.thermometer.utils;

import pl.narfsoftware.thermometer.db.SensorData;
import android.content.Context;
import android.os.Handler;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;

public class TimerRunnable implements Runnable {

	Context context;
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
		prefs = new Preferences(context);
	}

	@Override
	public void run() {
		if (saveData) {
			dataSeries.resetData(sensorData.query(tableName,
					prefs.temperatureUnitCode));

			graphView.scrollToEnd();
		}
		handler.postDelayed(this, Constants.ONE_SECOND);
	}

	public void setSaveData(boolean saveData) {
		this.saveData = saveData;
	}

}
