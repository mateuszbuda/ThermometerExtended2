package pl.narfsoftware.thermometer2.utils;

import android.graphics.Color;
import android.os.Handler;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;

public class RefresherRunnable implements Runnable {

	boolean saveData;
	GraphViewSeries dataSeries;
	TextView tvUnit;
	int verticalLabelsWidth;
	GraphView graphView;
	Label label;
	Handler handler;

	public RefresherRunnable(boolean sd, GraphViewSeries ds, TextView tv,
			int vlw, GraphView gv, Handler h) {
		saveData = sd;
		dataSeries = ds;
		tvUnit = tv;
		verticalLabelsWidth = vlw;
		graphView = gv;
		handler = h;
	}

	@Override
	public void run() {
		if (saveData && dataSeries.getValues().length > 1) {
			graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);
			graphView.getGraphViewStyle().setVerticalLabelsWidth(
					verticalLabelsWidth);

			graphView.addSeries(dataSeries);
			graphView.setViewPort(
					dataSeries.getValues()[0].getX(),
					dataSeries.getValues()[dataSeries.getValues().length - 1]
							.getX() - dataSeries.getValues()[0].getX());
			graphView.setScalable(true);
			graphView.setCustomLabelFormatter(new Label(dataSeries));
		} else
			handler.postDelayed(this, Constants.ONE_SECOND);
	}

	public void setSaveDate(boolean saveData) {
		this.saveData = saveData;
	}

}
