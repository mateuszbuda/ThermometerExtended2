package pl.narfsoftware.thermometer2;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class PlotFragment extends Fragment {
	static final String TAG = "PlotFragment";

	final static String ARG_POSITION = "position";
	int currentPosition = -1;

	boolean saveData;

	static final String INTENT_ORIGIN = "intent_origin";
	static final String INTENT_EXTRA_TABLE_NAME = "tabe_name";
	static final String INTENT_EXTRA_UNIT = "unit";

	static final String UNIT_TEMPERATURE = "[" + (char) 0x00B0 + "C]";
	static final String UNIT_RELATIVE_HUMIDITY = "[%]";
	static final String UNIT_ABSOLUTE_HUMIDITY = Html.fromHtml(
			"[g/m<sup><small>3</small></sup>]").toString();
	static final String UNIT_PRESSURE = "[hPa]";
	static final String UNIT_DEW_POINT = "[" + (char) 0x00B0 + "C]";
	static final String UNIT_LIGHT = "[lx]";
	static final String UNIT_MAGNETIC_FIELD = "[" + (char) 0x03BC + "T]";

	static final String DATE_FORMAT_TODAY = "HH:mm:ss";
	static final String DATE_FORMAT_OLDER = "d/M/yy";

	static final long DAY = 24 * 60 * 60 * 1000;

	LinearLayout backgroundLayout;
	TextView tvUnit;
	GraphView graphView;

	SensorData sensorData;
	GraphViewSeries dataSeries;

	float textSize;
	int verticalLabelsWidth;
	static final int HORIZONTAL_LABELS_COUNT = 4;
	static final int VERTICAL_LABELS_COUNT = 6;

	private final Handler handler = new Handler();
	private Runnable timer;
	private Runnable refresher;

	static final long ONE_SECOND = 1000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		textSize = getResources().getInteger(R.integer.plot_label_text_size);
		verticalLabelsWidth = getResources().getInteger(
				R.integer.plot_vertical_labels_widht);

		sensorData = ((ThermometerApp) getActivity().getApplication())
				.getSensorData();

		backgroundLayout = (LinearLayout) getActivity()
				.findViewById(R.id.graph);

		tvUnit = (TextView) getActivity().findViewById(R.id.tvUnit);

		graphView = new LineGraphView(getActivity(), "");

		graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);
		graphView.getGraphViewStyle().setGridColor(Color.GRAY);
		graphView.getGraphViewStyle().setTextSize(textSize);
		graphView.getGraphViewStyle().setNumHorizontalLabels(
				HORIZONTAL_LABELS_COUNT);
		graphView.getGraphViewStyle().setNumVerticalLabels(
				VERTICAL_LABELS_COUNT);
		graphView.getGraphViewStyle().setVerticalLabelsWidth(
				verticalLabelsWidth);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// If activity recreated (such as from screen rotate), restore
		// the previous article selection set by onSaveInstanceState().
		// This is primarily necessary when in the two-pane layout.
		if (savedInstanceState != null) {
			currentPosition = savedInstanceState.getInt(ARG_POSITION);
		}

		((LinearLayout) getActivity().findViewById(R.id.dateAndTime))
				.setVisibility(LinearLayout.GONE);

		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.plot_view, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		// During startup, check if there are arguments passed to the fragment.
		// onStart is a good place to do this because the layout has already
		// been applied to the fragment at this point so we can safely call the
		// method below that sets the article text.
		Bundle args = getArguments();
		if (args != null) {
			// Set plot based on argument passed in
			updatePlotFragment(args.getInt(ARG_POSITION));
		} else if (currentPosition != -1) {
			// Set plot based on saved instance state defined during
			// onCreateView
			updatePlotFragment(currentPosition);
		}
	}

	public void updatePlotFragment(int position) {
		tvUnit = (TextView) getActivity().findViewById(R.id.tvUnit);

		currentPosition = position;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the current article selection in case we need to recreate the
		// fragment
		outState.putInt(ARG_POSITION, currentPosition);
	}
}
