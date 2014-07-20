package pl.narfsoftware.thermometer.ui;

import pl.narfsoftware.thermometer.R;
import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.db.DbHelper;
import pl.narfsoftware.thermometer.db.SensorData;
import pl.narfsoftware.thermometer.preferences.Preferences;
import pl.narfsoftware.thermometer.service.SensorsDataSavingService;
import pl.narfsoftware.thermometer.utils.Constants;
import pl.narfsoftware.thermometer.utils.Label;
import pl.narfsoftware.thermometer.utils.RefresherRunnable;
import pl.narfsoftware.thermometer.utils.TimerRunnable;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
	Preferences preferences;
	final static String ARG_KEY = "sensor_key";
	/**
	 * Key of ambient condition as in HashMaps, not index on the list in
	 * SensorsFragment
	 */
	int currentKey = Sensor.TYPE_AMBIENT_TEMPERATURE;

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

	OnSensorDataSaveStateChangedListener callback;

	public interface OnSensorDataSaveStateChangedListener {
		/**
		 * Called by PlotFragment when a dataSave switch checked state changes
		 * 
		 * @param key
		 *            Sensors' type - key in dictionary
		 */
		public void onSensorDataSaveStateChanged(int key);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try {
			callback = (OnSensorDataSaveStateChangedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnSensorDataSaveStateChangedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = ((ThermometerApp) activity.getApplication());
		preferences = app.getPrefs();
		textSize = getResources().getInteger(R.integer.plot_label_text_size);
//		verticalLabelsWidth = getResources().getInteger(
//				R.integer.plot_vertical_labels_widht);
        Paint paint = new Paint();
        paint.setTextSize(sp2px(activity, textSize));
        Rect rect = new Rect();
        paint.getTextBounds("000.00", 0, 5, rect);
        verticalLabelsWidth = rect.width();

		sensorData = app.getSensorData();

		initGraphView();
	}

    private float sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return spValue * fontScale + 0.5f;
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
			currentKey = savedInstanceState.getInt(ARG_KEY);
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
			// updatePlotFragment(args.getInt(ARG_KEY));
			currentKey = args.getInt(ARG_KEY);
		} else if (currentKey < 0) {
			// Set plot based on saved instance state defined during
			// onCreateView
			// updatePlotFragment(currentKey);
			currentKey = Sensor.TYPE_AMBIENT_TEMPERATURE;
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		initDataForGraphView();

		if (dataSeries.getValues().length <= 1) {
			// hide vertical labels
			graphView.getGraphViewStyle().setVerticalLabelsWidth(1);
			if (!preferences.dataHintToastShowed())
				showDataHindToast();
		} else
			showData();

		runGraphView();
	}

	private void initDataForGraphView() {
		tvUnit.setText("");
		tvUnit.setTypeface(preferences.getTypeface());
		saveDataSwitch.setChecked(app.saveData(currentKey));
		saveDataSwitch.setTypeface(preferences.getTypeface());
		tableName = DbHelper.TABLE_NAMES.get(currentKey);
		saveData = app.saveData(currentKey);
		dataSeries = new GraphViewSeries(sensorData.query(tableName,
				preferences.getTempUnitCode()));
		Log.d(TAG, "data rows count: " + dataSeries.getValues().length);
	}

	private void showDataHindToast() {
		// this notification is showed only once
		preferences.setDataHintToastShowed();
		String toastText = getResources()
				.getString(R.string.no_data_info_toast);
		toastText += "\n"
				+ getResources().getString(R.string.no_data_hint_toast);
		Toast.makeText(activity, toastText, Toast.LENGTH_LONG).show();
	}

	private void showData() {
		if (tvUnit != null)
			tvUnit.setText(Constants.UNITS[preferences.getTempUnitCode()]
					.get(currentKey));

		graphView.setCustomLabelFormatter(new Label(dataSeries));

		graphView.getGraphViewStyle().setVerticalLabelsWidth(
				verticalLabelsWidth);

		graphView.addSeries(dataSeries);
		graphView.setViewPort(
				dataSeries.getValues()[0].getX(),
				dataSeries.getValues()[dataSeries.getValues().length - 1]
						.getX() - dataSeries.getValues()[0].getX());
		graphView.setScalable(true);
	}

	private void runGraphView() {
		graphView.setScrollable(true);
		// add graph view
		if (backgroundLayout != null && graphView.getParent() == null)
			backgroundLayout.addView(graphView);

		timer = new TimerRunnable(activity, saveData, dataSeries, sensorData,
				tableName, graphView, handler);
		refresher = new RefresherRunnable(saveData, dataSeries, tvUnit,
				Constants.UNITS[preferences.getTempUnitCode()].get(currentKey),
				verticalLabelsWidth, graphView, handler);

		handler.postDelayed(timer, Constants.ONE_SECOND);
		handler.postDelayed(refresher, Constants.ONE_SECOND);
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

	public void updatePlotFragment(int key) {
		currentKey = key;

		handler.removeCallbacks(timer);
		handler.removeCallbacks(refresher);

		graphView.removeAllSeries();

		initDataForGraphView();

		if (dataSeries.getValues().length <= 1) {
			// hide vertical labels
			graphView.getGraphViewStyle().setVerticalLabelsWidth(1);
			if (!preferences.dataHintToastShowed())
				showDataHindToast();
		} else
			showData();

		runGraphView();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the current ambient condition selection in case we need to
		// recreate the fragment
		outState.putInt(ARG_KEY, currentKey);
	}

	private void initGraphView() {
		graphView = new LineGraphView(activity, "");
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
		app.setSaveData(currentKey, saveData = isChecked);
		if (refresher != null)
			refresher.setSaveData(isChecked);
		if (timer != null)
			timer.setSaveData(isChecked);
		if (((SensorsActivity) activity).sensorsFragment != null) {
			((SensorsActivity) activity).sensorsFragment.initIcons();
			((SensorsActivity) activity).sensorsFragment.adapter
					.notifyDataSetChanged();
		}
		callback.onSensorDataSaveStateChanged(currentKey);

		if (app.saveAnyData())
			activity.startService(new Intent(activity,
					SensorsDataSavingService.class));
		else
			activity.stopService(new Intent(activity,
					SensorsDataSavingService.class));
	}
}
