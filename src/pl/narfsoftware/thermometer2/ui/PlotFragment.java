package pl.narfsoftware.thermometer2.ui;

import pl.narfsoftware.thermometer2.R;
import pl.narfsoftware.thermometer2.SensorService;
import pl.narfsoftware.thermometer2.ThermometerApp;
import pl.narfsoftware.thermometer2.db.DbHelper;
import pl.narfsoftware.thermometer2.db.SensorData;
import pl.narfsoftware.thermometer2.utils.Constants;
import pl.narfsoftware.thermometer2.utils.Label;
import pl.narfsoftware.thermometer2.utils.Preferences;
import pl.narfsoftware.thermometer2.utils.RefresherRunnable;
import pl.narfsoftware.thermometer2.utils.TimerRunnable;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class PlotFragment extends Fragment implements OnCheckedChangeListener {
	static final String TAG = "PlotFragment";

	Activity activity;
	ThermometerApp app;
	final static String ARG_INDEX = "sensor_index";
	/**
	 * Index of ambient condition as in ThermometerApp, not index on the list in
	 * SensorsFragment
	 */
	int currentIndex = 0;

	boolean saveData;

	LinearLayout backgroundLayout;
	TextView tvUnit;
	GraphView graphView;
	Switch saveDataSwitch;

	SensorData sensorData;
	GraphViewSeries dataSeries;
	String tableName = "null";

	float textSize;
	int verticalLabelsWidth;
	Label label;

	private final Handler handler = new Handler();
	private TimerRunnable timer;
	private RefresherRunnable refresher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		activity = getActivity();
		textSize = getResources().getInteger(R.integer.plot_label_text_size);
		verticalLabelsWidth = getResources().getInteger(
				R.integer.plot_vertical_labels_widht);
		app = ((ThermometerApp) activity.getApplication());
		sensorData = ((ThermometerApp) activity.getApplication())
				.getSensorData();

		initGraphView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View inflatedView = inflater.inflate(R.layout.plot_view, container,
				false);
		backgroundLayout = (LinearLayout) inflatedView.findViewById(R.id.graph);
		saveDataSwitch = (Switch) inflatedView.findViewById(R.id.saveData);
		tvUnit = (TextView) inflatedView.findViewById(R.id.tvUnit);
		saveDataSwitch.setOnCheckedChangeListener(this);

		// If activity recreated (such as from screen rotate), restore
		// the previous sensor selection set by onSaveInstanceState().
		// This is primarily necessary when in the two-pane layout.
		if (savedInstanceState != null) {
			currentIndex = savedInstanceState.getInt(ARG_INDEX);
		}

		// Inflate the layout for this fragment
		return inflatedView;
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
			updatePlotFragment(args.getInt(ARG_INDEX));
		} else if (currentIndex != -1) {
			// Set plot based on saved instance state defined during
			// onCreateView
			updatePlotFragment(currentIndex);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		saveData = false;
		tableName = DbHelper.TABLE_NAMES[currentIndex];
		saveData = app.saveAmbientConditionData[currentIndex];
		// :D
		// if (backgroundLayout != null)
		// backgroundLayout
		// .setBackgroundColor(Color
		// .parseColor(PreferenceManager
		// .getDefaultSharedPreferences(activity)
		// .getString(
		// getResources()
		// .getString(
		// R.string.prefs_background_color_key),
		// Integer.toString(Preferences.BACKGROUND_DEFAULT_COLOR))));
		tableName = DbHelper.TABLE_NAMES[currentIndex];
		dataSeries = new GraphViewSeries(sensorData.query(tableName));
		if (dataSeries.getValues().length <= 1) {
			graphView
					.getGraphViewStyle()
					.setVerticalLabelsColor(
							Color.parseColor(PreferenceManager
									.getDefaultSharedPreferences(activity)
									.getString(
											getResources()
													.getString(
															R.string.prefs_background_color_key),
											Integer.toString(Preferences.BACKGROUND_DEFAULT_COLOR))));
			graphView.getGraphViewStyle().setVerticalLabelsWidth(1);
			String toastText = getResources().getString(
					R.string.no_data_info_toast);
			if (!saveData)
				toastText += "\n"
						+ getResources().getString(R.string.no_data_hint_toast);
			Toast.makeText(activity, toastText, Toast.LENGTH_SHORT).show();
		} else {
			// set unit
			tvUnit.setText(Constants.UNITS[currentIndex]);

			graphView.setCustomLabelFormatter(new Label(dataSeries));

			graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);
			graphView.getGraphViewStyle().setVerticalLabelsWidth(
					verticalLabelsWidth);

			graphView.addSeries(dataSeries);
			graphView.setViewPort(
					dataSeries.getValues()[0].getX(),
					dataSeries.getValues()[dataSeries.getValues().length - 1]
							.getX() - dataSeries.getValues()[0].getX());
			graphView.setScalable(true);
		}

		graphView.setScrollable(true);
		// add graph view
		if (backgroundLayout != null)
			backgroundLayout.addView(graphView);

		timer = new TimerRunnable(saveData, dataSeries, sensorData, tableName,
				graphView, handler);
		refresher = new RefresherRunnable(saveData, dataSeries, tvUnit,
				verticalLabelsWidth, graphView, handler);

		handler.postDelayed(timer, Constants.ONE_SECOND);
		handler.postDelayed(refresher, Constants.ONE_SECOND);

		if (saveDataSwitch != null)
			saveDataSwitch
					.setChecked(app.saveAmbientConditionData[currentIndex]);
	}

	@Override
	public void onPause() {
		super.onPause();
		handler.removeCallbacks(timer);
		handler.removeCallbacks(refresher);
	}

	@Override
	public void onStop() {
		super.onStop();

		graphView.removeAllSeries();

		if (graphView.getParent() != null)
			backgroundLayout.removeView(graphView);

		if (!saveData)
			sensorData.close();
	}

	public void updatePlotFragment(int index) {
		tvUnit = (TextView) activity.findViewById(R.id.tvUnit);
		currentIndex = index;
		tableName = DbHelper.TABLE_NAMES[currentIndex];
		tvUnit.setText(Constants.UNITS[currentIndex]);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the current article selection in case we need to recreate the
		// fragment
		outState.putInt(ARG_INDEX, currentIndex);
	}

	private void initGraphView() {
		graphView = new LineGraphView(getActivity(), "");

		graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);
		graphView.getGraphViewStyle().setGridColor(Color.GRAY);
		graphView.getGraphViewStyle().setTextSize(textSize);
		graphView.getGraphViewStyle().setNumHorizontalLabels(
				Constants.HORIZONTAL_LABELS_COUNT);
		graphView.getGraphViewStyle().setNumVerticalLabels(
				Constants.VERTICAL_LABELS_COUNT);
		graphView.getGraphViewStyle().setVerticalLabelsWidth(
				verticalLabelsWidth);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		saveData = app.saveAmbientConditionData[currentIndex] = isChecked;
		refresher.setSaveDate(isChecked);
		timer.setSaveDate(isChecked);
		((SensorsActivity) activity).sensorsFragment.initIcons();
		((SensorsActivity) activity).sensorsFragment.adapter
				.notifyDataSetChanged();
		activity.stopService(new Intent(activity, SensorService.class));
		if (app.saveAnyAmbientCondition())
			activity.startService(new Intent(activity, SensorService.class));
	}
}
