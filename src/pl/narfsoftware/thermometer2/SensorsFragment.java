package pl.narfsoftware.thermometer2;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.database.DataSetObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class SensorsFragment extends ListFragment implements
		OnItemClickListener, SensorEventListener {
	static final String TAG = "SensorsFragment";

	Activity activity;
	ThermometerApp app;

	// TODO Create class Constants
	static final double A = 6.112;
	static final double M = 17.62;
	static final double TN = 243.12;
	static final double ZERO_ABSOLUTE = 273.15;
	static final double HUNDRED_PERCENT = 100.0;
	static final double FAHRENHEIT_FACTOR = 5 / 9;
	static final double FAHRENHEIT_CONSTANT = 32;
	static final double ABSOLUTE_HUMIDITY_CONSTANT = 216.7;

	SensorManager sensorManager;

	float temperature;
	float relativeHumidity;
	float absoluteHumidity;
	float pressure;
	float dewPoint;
	float light;
	float magneticField;

	String temperatureUnit;
	static final int CELSIUS = 0;
	static final int FAHRENHEIT = 1;
	static final int KELVIN = 2;

	static final String NO_DATA = "No Data";

	SensorRow[] sensorRows = new SensorRow[ThermometerApp.AMBIENT_CONDITIONS_COUNT];

	static final String[] SENSORS = { "Temperature", "Relative Humidity",
			"Absolute Humidity", "Pressure", "DewPoint", "Light",
			"Magnetic Field" };

	String[] VALUES = { NO_DATA, NO_DATA, NO_DATA, NO_DATA, NO_DATA, NO_DATA,
			NO_DATA };

	Integer[] ICONS = { R.drawable.temprature, R.drawable.relative_humidity,
			R.drawable.absolute_humidity, R.drawable.pressure,
			R.drawable.dew_point, R.drawable.light, R.drawable.magnetic_field };

	OnSensorSelectedListener callback;

	List<SensorRow> sensorsList;
	SensorsListViewAdapter adapter;

	// The container Activity must implement this interface so the frag can
	// deliver messages
	public interface OnSensorSelectedListener {
		/** Called by SensorsFragment when a list item is selected */
		public void onSensorSelected(int position);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (ThermometerApp) activity.getApplication();

		sensorManager = (SensorManager) activity
				.getSystemService(android.content.Context.SENSOR_SERVICE);

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

		sensorsList = new ArrayList<SensorRow>();
		for (int i = 0; i < SENSORS.length; i++)
			if (app.showAmbientCondition[i])
				sensorsList.add(sensorRows[i] = new SensorRow(ICONS[i],
						SENSORS[i], VALUES[i]));

		adapter = new SensorsListViewAdapter(getActivity(),
				R.layout.sensor_row, sensorsList);
		adapter.registerDataSetObserver(new DataSetObserver() {
		});
		setListAdapter(adapter);
	}

	@Override
	public void onPause() {
		super.onPause();

		// unregister sensors, yet no longer needed
		sensorManager.unregisterListener(this);
		Log.d(TAG, "Sensors unregistered");
	}

	@Override
	public void onDestroy() {
		sensorManager.unregisterListener(this);
		super.onDestroy();
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
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Notify the parent activity of selected item
		callback.onSensorSelected(position);

		// Set the item as checked to be highlighted when in two-pane layout
		getListView().setItemChecked(position, true);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Toast toast = Toast.makeText(getActivity(), "Item " + (position + 1)
				+ ": " + sensorsList.get(position), Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}

	private void registerChosenSensors() {
		// TODO Register sensors on their own listener (?)
		if (app.hasSensor[ThermometerApp.S_TEMPRATURE]
				&& (app.showAmbientCondition[ThermometerApp.TEMPERATURE_INDEX]
						|| app.showAmbientCondition[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX] || app.showAmbientCondition[ThermometerApp.DEW_POINT_INDEX])) {
			sensorManager.registerListener(this, app.getTemperatureSensor(),
					SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "Temperature sensor registered");
		}
		if (app.hasSensor[ThermometerApp.S_RELATIVE_HUMIDITY]
				&& (app.showAmbientCondition[ThermometerApp.RELATIVE_HUMIDITY_INDEX]
						|| app.showAmbientCondition[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX] || app.showAmbientCondition[ThermometerApp.DEW_POINT_INDEX])) {
			sensorManager.registerListener(this,
					app.getRelativeHumiditySensor(),
					SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "Relative humidity sensor registered");
		}
		if (app.hasSensor[ThermometerApp.S_PRESSURE]
				&& app.showAmbientCondition[ThermometerApp.PRESSURE_INDEX]) {
			sensorManager.registerListener(this, app.getPressureSensor(),
					SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "Pressure sensor registered");
		}
		if (app.hasSensor[ThermometerApp.S_LIGHT]
				&& app.showAmbientCondition[ThermometerApp.LIGHT_INDEX]) {
			sensorManager.registerListener(this, app.getLightSensor(),
					SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "Light sensor registered");
		}
		if (app.hasSensor[ThermometerApp.S_MAGNETIC_FIELD]
				&& app.showAmbientCondition[ThermometerApp.MAGNETIC_FIELD_INDEX]) {
			sensorManager.registerListener(this, app.getMagneticFieldSensor(),
					SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "Magnetic field sensor registered");
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if ((app.showAmbientCondition[ThermometerApp.TEMPERATURE_INDEX]
				|| app.showAmbientCondition[ThermometerApp.DEW_POINT_INDEX] || app.showAmbientCondition[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX])
				&& event.sensor.equals(app.getTemperatureSensor())) {
			temperature = event.values[0];

			if (temperatureUnit.equals(getResources().getStringArray(
					R.array.prefs_temp_unit_vals)[CELSIUS]))
				VALUES[ThermometerApp.TEMPERATURE_INDEX] = (String.format(
						"%.0f", temperature) + " " + (char) 0x00B0 + "C");
			else if (temperatureUnit.equals(getResources().getStringArray(
					R.array.prefs_temp_unit_vals)[FAHRENHEIT]))
				VALUES[ThermometerApp.TEMPERATURE_INDEX] = (String.format(
						"%.0f", temperature * 9 / 5 + 32) + " " + (char) 0x00B0 + "F");
			else
				VALUES[ThermometerApp.TEMPERATURE_INDEX] = (String.format(
						"%.0f", temperature + 273) + " K");

			adapter.getItem(
					adapter.getPosition(sensorRows[ThermometerApp.TEMPERATURE_INDEX]))
					.setStringValue(VALUES[ThermometerApp.TEMPERATURE_INDEX]);

			Log.d(TAG, "Got temperature sensor event: " + temperature);
		}

		if ((app.showAmbientCondition[ThermometerApp.RELATIVE_HUMIDITY_INDEX]
				|| app.showAmbientCondition[ThermometerApp.DEW_POINT_INDEX] || app.showAmbientCondition[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX])
				&& event.sensor.equals(app.getRelativeHumiditySensor())) {
			relativeHumidity = event.values[0];

			VALUES[ThermometerApp.RELATIVE_HUMIDITY_INDEX] = (String.format(
					"%.0f", relativeHumidity) + " %");

			adapter.getItem(
					adapter.getPosition(sensorRows[ThermometerApp.RELATIVE_HUMIDITY_INDEX]))
					.setStringValue(
							VALUES[ThermometerApp.RELATIVE_HUMIDITY_INDEX]);

			Log.d(TAG, "Got relative humidity sensor event: "
					+ relativeHumidity);
		}

		if (app.showAmbientCondition[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX]
				&& (event.sensor.equals(app.getTemperatureSensor()) || event.sensor
						.equals(app.getRelativeHumiditySensor()))) {
			updateAbsoluteHumidity();

			adapter.getItem(
					adapter.getPosition(sensorRows[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX]))
					.setStringValue(
							VALUES[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX]);
		}

		if (app.showAmbientCondition[ThermometerApp.PRESSURE_INDEX]
				&& event.sensor.equals(app.getPressureSensor())) {
			pressure = event.values[0];

			VALUES[ThermometerApp.PRESSURE_INDEX] = (String.format("%.0f",
					pressure) + " hPa");

			adapter.getItem(
					adapter.getPosition(sensorRows[ThermometerApp.PRESSURE_INDEX]))
					.setStringValue(VALUES[ThermometerApp.PRESSURE_INDEX]);

			Log.d(TAG, "Got pressure sensor event: " + pressure);
		}

		if (app.showAmbientCondition[ThermometerApp.DEW_POINT_INDEX]
				&& (event.sensor.equals(app.getTemperatureSensor()) || event.sensor
						.equals(app.getRelativeHumiditySensor()))) {
			updateDewPoint();

			adapter.getItem(
					adapter.getPosition(sensorRows[ThermometerApp.DEW_POINT_INDEX]))
					.setStringValue(VALUES[ThermometerApp.DEW_POINT_INDEX]);
		}

		if (app.showAmbientCondition[ThermometerApp.LIGHT_INDEX]
				&& event.sensor.equals(app.getLightSensor())) {
			light = event.values[0];

			VALUES[ThermometerApp.LIGHT_INDEX] = (String.format("%.0f", light) + " lx");

			adapter.getItem(
					adapter.getPosition(sensorRows[ThermometerApp.LIGHT_INDEX]))
					.setStringValue(VALUES[ThermometerApp.LIGHT_INDEX]);

			Log.d(TAG, "Got light sensor event: " + light);
		}

		if (app.showAmbientCondition[ThermometerApp.MAGNETIC_FIELD_INDEX]
				&& event.sensor.equals(app.getMagneticFieldSensor())) {
			float magneticFieldX = event.values[0];
			float magneticFieldY = event.values[1];
			float magneticFieldZ = event.values[2];
			magneticField = magneticFieldX + magneticFieldY + magneticFieldZ;

			VALUES[ThermometerApp.MAGNETIC_FIELD_INDEX] = (String.format(
					"%.0f", magneticField) + " " + (char) 0x03BC + "T");

			adapter.getItem(
					adapter.getPosition(sensorRows[ThermometerApp.MAGNETIC_FIELD_INDEX]))
					.setStringValue(VALUES[ThermometerApp.MAGNETIC_FIELD_INDEX]);

			Log.d(TAG, "Got magnetic field sensor event: " + magneticField);
		}

		adapter.notifyDataSetChanged();
	}

	private void updateAbsoluteHumidity() {
		absoluteHumidity = (float) (ABSOLUTE_HUMIDITY_CONSTANT * (relativeHumidity
				/ HUNDRED_PERCENT
				* A
				* Math.exp(M * temperature / (TN + temperature)) / (ZERO_ABSOLUTE + temperature)));

		VALUES[ThermometerApp.ABSOLUTE_HUMIDITY_INDEX] = (Html.fromHtml(String
				.format("%.0f", absoluteHumidity)
				+ " g/m<sup><small>3</small></sup>")).toString();

		Log.d(TAG, "Absolute humidity updated: " + absoluteHumidity);
	}

	private void updateDewPoint() {
		double h = Math.log(relativeHumidity / HUNDRED_PERCENT)
				+ (M * temperature) / (TN + temperature);
		dewPoint = (float) (TN * h / (M - h));

		if (temperatureUnit.equals(getResources().getStringArray(
				R.array.prefs_temp_unit_vals)[CELSIUS]))
			VALUES[ThermometerApp.DEW_POINT_INDEX] = (String.format("%.0f",
					dewPoint) + " " + (char) 0x00B0 + "C");
		else if (temperatureUnit.equals(getResources().getStringArray(
				R.array.prefs_temp_unit_vals)[FAHRENHEIT]))
			VALUES[ThermometerApp.DEW_POINT_INDEX] = (String.format("%.0f",
					dewPoint * FAHRENHEIT_FACTOR + FAHRENHEIT_CONSTANT)
					+ " "
					+ (char) 0x00B0 + "F");
		else
			VALUES[ThermometerApp.DEW_POINT_INDEX] = (String.format("%.0f",
					dewPoint + ZERO_ABSOLUTE) + " K");

		Log.d(TAG, "Dew point updated: " + dewPoint);
	}

}
