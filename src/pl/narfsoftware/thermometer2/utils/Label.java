package pl.narfsoftware.thermometer2.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;


import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphViewSeries;

public class Label implements CustomLabelFormatter {

	GraphViewSeries dataSeries;

	public Label(GraphViewSeries gvs) {
		dataSeries = gvs;
	}

	@Override
	public String formatLabel(double value, boolean isValueX) {
		if (isValueX) {
			String date;
			String time;

			long now = new Timestamp(new Date().getTime()).getTime();

			Date d = new Date((long) value);

			date = new SimpleDateFormat(Constants.DATE_FORMAT_OLDER).format(d);

			time = new SimpleDateFormat(Constants.DATE_FORMAT_TODAY).format(d);

			return ((now - ((long) dataSeries.getValues()[0].getX())) < Constants.DAY) ? time
					: date + "\n" + time;

		}
		return null;
	}

}
