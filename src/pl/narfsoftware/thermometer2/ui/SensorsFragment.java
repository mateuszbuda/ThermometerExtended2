package pl.narfsoftware.thermometer2.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.narfsoftware.thermometer2.R;
import pl.narfsoftware.thermometer2.SensorRow;
import pl.narfsoftware.thermometer2.SensorsListViewAdapter;
import pl.narfsoftware.thermometer2.ThermometerApp;
import pl.narfsoftware.thermometer2.utils.Constants;
import pl.narfsoftware.thermometer2.utils.Preferences;
import android.app.Activity;
import android.app.ListFragment;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SensorsFragment extends ListFragment implements
		SensorEventListener {
	static final String TAG = "SensorsFragment";

	Activity activity;
	ThermometerApp app;
	SensorManager sensorManager;
	Preferences preferences;

	float temperature;
	float relativeHumidity;
	float absoluteHumidity;
	float pressure;
	float dewPoint;
	float light;
	float magneticField;

	String noData;
	String sensorUnavailavle;

	SensorRow[] sensorRows = new SensorRow[ThermometerApp.AMBIENT_CONDITIONS_COUNT];
	String[] sensorTitles = new String[ThermometerApp.AMBIENT_CONDITIONS_COUNT];
	String[] values = new String[ThermometerApp.AMBIENT_CONDITIONS_COUNT];
	Integer[] iconIds = new Integer[ThermometerApp.AMBIENT_CONDITIONS_COUNT];

	OnSensorSelectedListener callback;

	List<SensorRow> sensorsList;
	SensorsListViewAdapter adapter;

	// The container Activity must implement this interface so the frag can
	// deliver messages
	public interface OnSensorSelectedListener {
		/** Called by SensorsFragment when a list item is selected */
		public void onSensorSelected(int index);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try {
			callback = (OnSensorSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnSensorSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (ThermometerApp) activity.getApplication();
		preferences = new Preferences(activity);
		sensorManager = (SensorManager) activity
				.getSystemService(android.content.Context.SENSOR_SERVICE);
		noData = getResources().getString(R.string.sensor_no_data);
		sensorUnavailavle = getResources().getString(
				R.string.sensor_unavailable);
		init();

		Log.d(TAG, "onCreated");
	}

	@Override
	public void onStart() {
		super.onStart();

		// When in two-pane layout, set the listview to highlight the selected
		// list item
		// (We do this during onStart because at the point the listview is
		// available.)
		if (getFragmentManager().findFragmentById(R.id.sensors_fragment) != null) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		sensorManager.unregisterListener(this);
		Log.d(TAG, "Sensors unregistered");

		registerChosenSensors();
		initIcons();
		initDateAndTime();
		setAdapter();
	}

	@Override
	public void onPause() {
		super.onPause();
		// unregister sensors, yet no longer needed
		sensorManager.unregisterListener(this);
		Log.d(TAG, "Sensors unregistered");
	}

	@Override
	public void onStop() {
		((LinearLayout) getActivity().findViewById(R.id.dateAndTime))
				.setVisibility(LinearLayout.GONE);
		super.onStop();
	}

	@Override
	public void onDestroy() {
		sensorManager.unregisterListener(this);
		super.onDestroy();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Notify the parent activity of selected item
		int index = -1;
		SensorRow selected = (SensorRow) getListView().getItemAtPosition(
				position);
		for (int i = 0; i < sensorRows.length; i++)
			if (sensorRows[i] == selected) {
				index = i;
				break;
			}

		callback.onSensorSelected(index);
		// Set the item as checked to be highlighted when in two-pane layout
		getListView().setItemChecked(position, true);
	}

	private void setAdapter() {
		sensorsList = new ArrayList<SensorRow>();
		for (int i = 0; i < sensorTitles.length; i++)
			if (preferences.showAmbientCondition[i])
				sensorsList.add(sensorRows[i] = new SensorRow(iconIds[i],
						sensorTitles[i], values[i]));

		adapter = new SensorsListViewAdapter(getActivity(),
				R.layout.sensor_row, sensorsList);
		adapter.registerDataSetObserver(new DataSetObserver() {
		});
		setListAdapter(adapter);
	}

	private void init() {
		initSensorTitles();
		initValues();
	}

	void initIcons() {
		iconIds[ThermometerApp.TEMPERATURE_INDEX] = app.saveAmbientConditionData[ThermometerApp.TEMPERATURE_INDEX] ? R.drawable.temprature
				: R.drawable.temprature_disabled;
		iconIds[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX] = app.saveAmbientConditionData[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX] ? R.drawable.absolute_humidity
				: R.drawable.absolute_humidity_disabled;
		iconIds[ThermometerApp.RELATIVE_HUMIDITY_INDEX] = app.saveAmbientConditionData[ThermometerApp.RELATIVE_HUMIDITY_INDEX] ? R.drawable.relative_humidity
				: R.drawable.relative_humidity_disabled;
		iconIds[ThermometerApp.PRESSURE_INDEX] = app.saveAmbientConditionData[ThermometerApp.PRESSURE_INDEX] ? R.drawable.pressure
				: R.drawable.pressure_disabled;
		iconIds[ThermometerApp.DEW_POINT_INDEX] = app.saveAmbientConditionData[ThermometerApp.DEW_POINT_INDEX] ? R.drawable.dew_point
				: R.drawable.dew_point_disabled;
		iconIds[ThermometerApp.LIGHT_INDEX] = app.saveAmbientConditionData[ThermometerApp.LIGHT_INDEX] ? R.drawable.light
				: R.drawable.light_disabled;
		iconIds[ThermometerApp.MAGNETIC_FIELD_INDEX] = app.saveAmbientConditionData[ThermometerApp.MAGNETIC_FIELD_INDEX] ? R.drawable.magnetic_field
				: R.drawable.magnetic_field_disabled;
	}

	private void initValues() {
		values[ThermometerApp.TEMPERATURE_INDEX] = app.hasSensor[ThermometerApp.S_TEMPRATURE] ? noData
				: sensorUnavailavle;
		values[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX] = app.hasSensor[ThermometerApp.S_RELATIVE_HUMIDITY]
				&& app.hasSensor[ThermometerApp.S_TEMPRATURE] ? noData
				: sensorUnavailavle;
		values[ThermometerApp.RELATIVE_HUMIDITY_INDEX] = app.hasSensor[ThermometerApp.S_RELATIVE_HUMIDITY] ? noData
				: sensorUnavailavle;
		values[ThermometerApp.DEW_POINT_INDEX] = app.hasSensor[ThermometerApp.S_RELATIVE_HUMIDITY]
				&& app.hasSensor[ThermometerApp.S_TEMPRATURE] ? noData
				: sensorUnavailavle;
		values[ThermometerApp.PRESSURE_INDEX] = app.hasSensor[ThermometerApp.S_PRESSURE] ? noData
				: sensorUnavailavle;
		values[ThermometerApp.LIGHT_INDEX] = app.hasSensor[ThermometerApp.S_LIGHT] ? noData
				: sensorUnavailavle;
		values[ThermometerApp.MAGNETIC_FIELD_INDEX] = app.hasSensor[ThermometerApp.S_MAGNETIC_FIELD] ? noData
				: sensorUnavailavle;
	}

	private void initSensorTitles() {
		sensorTitles[ThermometerApp.TEMPERATURE_INDEX] = activity
				.getResources().getString(R.string.ambient_temp_title);
		sensorTitles[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX] = activity
				.getResources().getString(R.string.absolute_humidity_title);
		sensorTitles[ThermometerApp.RELATIVE_HUMIDITY_INDEX] = activity
				.getResources().getString(R.string.relative_humidity_title);
		sensorTitles[ThermometerApp.PRESSURE_INDEX] = activity.getResources()
				.getString(R.string.pressure_title);
		sensorTitles[ThermometerApp.LIGHT_INDEX] = activity.getResources()
				.getString(R.string.light_title);
		sensorTitles[ThermometerApp.DEW_POINT_INDEX] = activity.getResources()
				.getString(R.string.dew_point_title);
		sensorTitles[ThermometerApp.MAGNETIC_FIELD_INDEX] = activity
				.getResources().getString(R.string.magnetic_field_title);
	}

	private void initDateAndTime() {
		LinearLayout dateAndTime = ((LinearLayout) activity
				.findViewById(R.id.dateAndTime));
		TextView date = (TextView) activity.findViewById(R.id.date);
		TextView time = (TextView) activity.findViewById(R.id.time);
		date.setTypeface(Typeface.createFromAsset(activity.getAssets(),
				preferences.fontTypeface));
		time.setTypeface(Typeface.createFromAsset(activity.getAssets(),
				preferences.fontTypeface));

		if (preferences.dateFormat.equals("")
				&& preferences.timeFormat.equals("")) {
			dateAndTime.setVisibility(LinearLayout.GONE);
			return;
		} else if (preferences.dateFormat.equals("")
				&& !preferences.timeFormat.equals("")) {
			dateAndTime.setVisibility(LinearLayout.VISIBLE);
			date.setVisibility(LinearLayout.GONE);
			time.setText(new SimpleDateFormat(preferences.timeFormat, Locale
					.getDefault()).format(new Date().getTime()));
			return;
		} else if (preferences.timeFormat.equals("")
				&& !preferences.dateFormat.equals("")) {
			dateAndTime.setVisibility(LinearLayout.VISIBLE);
			time.setVisibility(LinearLayout.GONE);
			date.setText(new SimpleDateFormat(preferences.dateFormat, Locale
					.getDefault()).format(new Date().getTime()));
			return;
		}

		dateAndTime.setVisibility(LinearLayout.VISIBLE);
		time.setVisibility(LinearLayout.VISIBLE);
		date.setVisibility(LinearLayout.VISIBLE);

		time.setText(new SimpleDateFormat(preferences.timeFormat, Locale
				.getDefault()).format(new Date().getTime()));
		date.setText(new SimpleDateFormat(preferences.dateFormat, Locale
				.getDefault()).format(new Date().getTime()));
	}

	private void registerChosenSensors() {
		// TODO Register sensors on their own listener (?)
		if (app.hasSensor[ThermometerApp.S_TEMPRATURE]
				&& (preferences.showAmbientCondition[ThermometerApp.TEMPERATURE_INDEX]
						|| preferences.showAmbientCondition[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX] || preferences.showAmbientCondition[ThermometerApp.DEW_POINT_INDEX])) {
			sensorManager.registerListener(this, app.getTemperatureSensor(),
					SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "Temperature sensor registered");
		}
		if (app.hasSensor[ThermometerApp.S_RELATIVE_HUMIDITY]
				&& (preferences.showAmbientCondition[ThermometerApp.RELATIVE_HUMIDITY_INDEX]
						|| preferences.showAmbientCondition[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX] || preferences.showAmbientCondition[ThermometerApp.DEW_POINT_INDEX])) {
			sensorManager.registerListener(this,
					app.getRelativeHumiditySensor(),
					SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "Relative humidity sensor registered");
		}
		if (app.hasSensor[ThermometerApp.S_PRESSURE]
				&& preferences.showAmbientCondition[ThermometerApp.PRESSURE_INDEX]) {
			sensorManager.registerListener(this, app.getPressureSensor(),
					SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "Pressure sensor registered");
		}
		if (app.hasSensor[ThermometerApp.S_LIGHT]
				&& preferences.showAmbientCondition[ThermometerApp.LIGHT_INDEX]) {
			sensorManager.registerListener(this, app.getLightSensor(),
					SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "Light sensor registered");
		}
		if (app.hasSensor[ThermometerApp.S_MAGNETIC_FIELD]
				&& preferences.showAmbientCondition[ThermometerApp.MAGNETIC_FIELD_INDEX]) {
			sensorManager.registerListener(this, app.getMagneticFieldSensor(),
					SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "Magnetic field sensor registered");
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		String text = sensor.getName();
		if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_HIGH)
			text += getResources().getString(R.string.accuracy_high_toast_text);
		else if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM)
			text += getResources().getString(
					R.string.accuracy_medium_toast_text);
		else if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW)
			text += getResources().getString(R.string.accuracy_low_toast_text);
		else if (accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
			text = getResources().getString(
					R.string.accuracy_unreliable_toast_text_part_1)
					+ sensor.getName()
					+ getResources().getString(
							R.string.accuracy_unreliable_toast_text_part_2);

		Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if ((preferences.showAmbientCondition[ThermometerApp.TEMPERATURE_INDEX]
				|| preferences.showAmbientCondition[ThermometerApp.DEW_POINT_INDEX] || preferences.showAmbientCondition[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX])
				&& event.sensor.equals(app.getTemperatureSensor())) {
			temperature = event.values[0];

			if (preferences.temperatureUnit
					.equals(getResources().getStringArray(
							R.array.prefs_temp_unit_vals)[Preferences.CELSIUS]))
				values[ThermometerApp.TEMPERATURE_INDEX] = (String.format(
						"%.0f", temperature) + " " + (char) 0x00B0 + "C");
			else if (preferences.temperatureUnit
					.equals(getResources().getStringArray(
							R.array.prefs_temp_unit_vals)[Preferences.FAHRENHEIT]))
				values[ThermometerApp.TEMPERATURE_INDEX] = (String.format(
						"%.0f", temperature * 9 / 5 + 32) + " " + (char) 0x00B0 + "F");
			else
				values[ThermometerApp.TEMPERATURE_INDEX] = (String.format(
						"%.0f", temperature + 273) + " K");

			adapter.getItem(
					adapter.getPosition(sensorRows[ThermometerApp.TEMPERATURE_INDEX]))
					.setStringValue(values[ThermometerApp.TEMPERATURE_INDEX]);

			Log.d(TAG, "Got temperature sensor event: " + temperature);
		}

		if ((preferences.showAmbientCondition[ThermometerApp.RELATIVE_HUMIDITY_INDEX]
				|| preferences.showAmbientCondition[ThermometerApp.DEW_POINT_INDEX] || preferences.showAmbientCondition[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX])
				&& event.sensor.equals(app.getRelativeHumiditySensor())) {
			relativeHumidity = event.values[0];

			values[ThermometerApp.RELATIVE_HUMIDITY_INDEX] = (String.format(
					"%.0f", relativeHumidity) + " %");

			adapter.getItem(
					adapter.getPosition(sensorRows[ThermometerApp.RELATIVE_HUMIDITY_INDEX]))
					.setStringValue(
							values[ThermometerApp.RELATIVE_HUMIDITY_INDEX]);

			Log.d(TAG, "Got relative humidity sensor event: "
					+ relativeHumidity);
		}

		if (preferences.showAmbientCondition[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX]
				&& (event.sensor.equals(app.getTemperatureSensor()) || event.sensor
						.equals(app.getRelativeHumiditySensor()))) {
			updateAbsoluteHumidity();

			adapter.getItem(
					adapter.getPosition(sensorRows[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX]))
					.setStringValue(
							values[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX]);
		}

		if (preferences.showAmbientCondition[ThermometerApp.PRESSURE_INDEX]
				&& event.sensor.equals(app.getPressureSensor())) {
			pressure = event.values[0];

			values[ThermometerApp.PRESSURE_INDEX] = (String.format("%.0f",
					pressure) + " hPa");

			adapter.getItem(
					adapter.getPosition(sensorRows[ThermometerApp.PRESSURE_INDEX]))
					.setStringValue(values[ThermometerApp.PRESSURE_INDEX]);

			Log.d(TAG, "Got pressure sensor event: " + pressure);
		}

		if (preferences.showAmbientCondition[ThermometerApp.DEW_POINT_INDEX]
				&& (event.sensor.equals(app.getTemperatureSensor()) || event.sensor
						.equals(app.getRelativeHumiditySensor()))) {
			updateDewPoint();

			adapter.getItem(
					adapter.getPosition(sensorRows[ThermometerApp.DEW_POINT_INDEX]))
					.setStringValue(values[ThermometerApp.DEW_POINT_INDEX]);
		}

		if (preferences.showAmbientCondition[ThermometerApp.LIGHT_INDEX]
				&& event.sensor.equals(app.getLightSensor())) {
			light = event.values[0];

			values[ThermometerApp.LIGHT_INDEX] = (String.format("%.0f", light) + " lx");

			adapter.getItem(
					adapter.getPosition(sensorRows[ThermometerApp.LIGHT_INDEX]))
					.setStringValue(values[ThermometerApp.LIGHT_INDEX]);

			Log.d(TAG, "Got light sensor event: " + light);
		}

		if (preferences.showAmbientCondition[ThermometerApp.MAGNETIC_FIELD_INDEX]
				&& event.sensor.equals(app.getMagneticFieldSensor())) {
			float magneticFieldX = event.values[0];
			float magneticFieldY = event.values[1];
			float magneticFieldZ = event.values[2];
			magneticField = (float) Math.sqrt(magneticFieldX * magneticFieldX
					+ magneticFieldY * magneticFieldY + magneticFieldZ
					* magneticFieldZ);

			values[ThermometerApp.MAGNETIC_FIELD_INDEX] = (String.format(
					"%.0f", magneticField) + " " + (char) 0x03BC + "T");

			adapter.getItem(
					adapter.getPosition(sensorRows[ThermometerApp.MAGNETIC_FIELD_INDEX]))
					.setStringValue(values[ThermometerApp.MAGNETIC_FIELD_INDEX]);

			Log.d(TAG, "Got magnetic field sensor event: " + magneticField);
		}

		adapter.notifyDataSetChanged();
	}

	private void updateAbsoluteHumidity() {
		absoluteHumidity = (float) (Constants.ABSOLUTE_HUMIDITY_CONSTANT * (relativeHumidity
				/ Constants.HUNDRED_PERCENT
				* Constants.A
				* Math.exp(Constants.M * temperature
						/ (Constants.TN + temperature)) / (Constants.ZERO_ABSOLUTE + temperature)));

		values[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX] = (Html.fromHtml(String
				.format("%.0f", absoluteHumidity)
				+ " g/m<sup><small>3</small></sup>")).toString();

		adapter.getItem(
				adapter.getPosition(sensorRows[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX]))
				.setStringValue(values[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX]);

		Log.d(TAG, "Absolute humidity updated: " + absoluteHumidity);
	}

	private void updateDewPoint() {
		double h = Math.log(relativeHumidity / Constants.HUNDRED_PERCENT)
				+ (Constants.M * temperature) / (Constants.TN + temperature);
		dewPoint = (float) (Constants.TN * h / (Constants.M - h));

		if (preferences.temperatureUnit.equals(getResources().getStringArray(
				R.array.prefs_temp_unit_vals)[Preferences.CELSIUS]))
			values[ThermometerApp.DEW_POINT_INDEX] = (String.format("%.0f",
					dewPoint) + " " + (char) 0x00B0 + "C");
		else if (preferences.temperatureUnit
				.equals(getResources().getStringArray(
						R.array.prefs_temp_unit_vals)[Preferences.FAHRENHEIT]))
			values[ThermometerApp.DEW_POINT_INDEX] = (String.format("%.0f",
					dewPoint * Constants.FAHRENHEIT_FACTOR
							+ Constants.FAHRENHEIT_CONSTANT)
					+ " " + (char) 0x00B0 + "F");
		else
			values[ThermometerApp.DEW_POINT_INDEX] = (String.format("%.0f",
					dewPoint + Constants.ZERO_ABSOLUTE) + " K");

		adapter.getItem(
				adapter.getPosition(sensorRows[ThermometerApp.DEW_POINT_INDEX]))
				.setStringValue(values[ThermometerApp.DEW_POINT_INDEX]);

		Log.d(TAG, "Dew point updated: " + dewPoint);
	}

	public void updateSensorsFragment(int index) {
		updateIcon(index);
	}

	private void updateIcon(int index) {
		int iconId = 0;
		if (index == ThermometerApp.TEMPERATURE_INDEX)
			iconId = app.saveAmbientConditionData[ThermometerApp.TEMPERATURE_INDEX] ? R.drawable.temprature
					: R.drawable.temprature_disabled;
		else if (index == ThermometerApp.RELATIVE_HUMIDITY_INDEX)
			iconId = app.saveAmbientConditionData[ThermometerApp.TEMPERATURE_INDEX] ? R.drawable.relative_humidity
					: R.drawable.relative_humidity_disabled;
		else if (index == ThermometerApp.ABSOLUTE_HUMIDITY_INDEX)
			iconId = app.saveAmbientConditionData[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX] ? R.drawable.absolute_humidity
					: R.drawable.absolute_humidity_disabled;
		else if (index == ThermometerApp.PRESSURE_INDEX)
			iconId = app.saveAmbientConditionData[ThermometerApp.PRESSURE_INDEX] ? R.drawable.pressure
					: R.drawable.pressure_disabled;
		else if (index == ThermometerApp.DEW_POINT_INDEX)
			iconId = app.saveAmbientConditionData[ThermometerApp.DEW_POINT_INDEX] ? R.drawable.dew_point
					: R.drawable.dew_point_disabled;
		else if (index == ThermometerApp.LIGHT_INDEX)
			iconId = app.saveAmbientConditionData[ThermometerApp.LIGHT_INDEX] ? R.drawable.light
					: R.drawable.light_disabled;
		else if (index == ThermometerApp.MAGNETIC_FIELD_INDEX)
			iconId = app.saveAmbientConditionData[ThermometerApp.MAGNETIC_FIELD_INDEX] ? R.drawable.magnetic_field
					: R.drawable.magnetic_field_disabled;
		adapter.getItem(adapter.getPosition(sensorRows[index])).setIcon(iconId);
		adapter.notifyDataSetChanged();
	}
}
