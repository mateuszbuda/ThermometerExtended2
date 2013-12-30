package pl.narfsoftware.thermometer2.utils;

import pl.narfsoftware.thermometer2.db.SensorData;
import android.os.Handler;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;

public class TimerRunnable implements Runnable {

	boolean saveData;
	GraphViewSeries dataSeries;
	SensorData sensorData;
	String tableName;
	GraphView graphView;
	Handler handler;

	public TimerRunnable(boolean sd, GraphViewSeries gvs, SensorData sda,
			String tn, GraphView gv, Handler h) {
		saveData = sd;
		dataSeries = gvs;
		sensorData = sda;
		tableName = tn;
		graphView = gv;
		handler = h;
	}

	@Override
	public void run() {
		if (saveData) {
			dataSeries.resetData(sensorData.query(tableName));

			graphView.scrollToEnd();
		}
		handler.postDelayed(this, Constants.ONE_SECOND);
	}

	public void setSaveData(boolean saveData) {
		this.saveData = saveData;
	}

}
