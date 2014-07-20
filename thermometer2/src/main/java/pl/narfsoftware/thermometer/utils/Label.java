package pl.narfsoftware.thermometer.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphViewSeries;

public class Label implements CustomLabelFormatter {

	private GraphViewSeries dataSeries;
	private SimpleDateFormat formatToday;
	private SimpleDateFormat formatOlder;
	String date;
	String time;

	public Label(GraphViewSeries gvs) {
		dataSeries = gvs;
		formatToday = new SimpleDateFormat(Constants.DATE_FORMAT_TODAY);
		formatOlder = new SimpleDateFormat(Constants.DATE_FORMAT_OLDER);
	}

	@Override
	public String formatLabel(double value, boolean isValueX) {
		if (isValueX) {
			if (dataSeries.getValues() != null
					&& dataSeries.getValues().length > 0) {
				long now = new Timestamp(new Date().getTime()).getTime();
				Date d = new Date((long) value);
				date = formatOlder.format(d);
				time = formatToday.format(d);
				return ((now - ((long) dataSeries.getValues()[0].getX())) < Constants.DAY) ? time
						: date + "\n" + time;
			}
		}
		return null;
	}
}
