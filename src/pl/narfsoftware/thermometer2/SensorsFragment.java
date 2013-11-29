package pl.narfsoftware.thermometer2;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
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

	static final double A = 6.112;
	static final double M = 17.62;
	static final double TN = 243.12;
	static final double ZERO_ABSOLUTE = 273.15;
	static final double HUNDRED_PERCENT = 100.0;
	static final double FAHRENHEIT_FACTOR = 5 / 9;
	static final double FAHRENHEIT_CONSTANT = 32;
	static final double ABSOLUTE_HUMIDITY_CONSTANT = 216.7;

	SensorManager sensorManager;
	Sensor[] sensors = new Sensor[SENSORS_COUNT];

	static final int S_TEMPRATURE = 0;
	static final int S_RELATIVE_HUMIDITY = 1;
	static final int S_PRESSURE = 2;
	static final int S_LIGHT = 3;
	static final int S_MAGNETIC_FIELD = 4;
	static final int SENSORS_COUNT = 5;

	boolean showTemprature;
	boolean showRelativeHumidity;
	boolean showAbsoluteHumidity;
	boolean showPressure;
	boolean showDewPoint;
	boolean showLight;
	boolean showMagneticField;

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

	// The container Activity must implement this interface so the frag can
	// deliver messages
	public interface OnSensorSelectedListener {
		/** Called by SensorsFragment when a list item is selected */
		public void onSensorSelected(int position);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sensorsList = new ArrayList<SensorRow>();
		for (int i = 0; i < SENSORS.length; i++)
			sensorsList.add(new SensorRow(ICONS[i], SENSORS[i], VALUES[i]));

		setListAdapter(new SensorsListViewAdapter(getActivity(),
				R.layout.sensor_row, sensorsList));
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
	public void onAttach(Activity activity) {
		super.onAttach(activity);

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

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if ((showTemprature || showDewPoint || showAbsoluteHumidity)
				&& event.sensor.equals(sensors[S_TEMPRATURE])) {
			temperature = event.values[0];

			if (temperatureUnit.equals(getResources().getStringArray(
					R.array.prefs_temp_unit_vals)[CELSIUS]))
				tvTemprature.setText(String.format("%.0f", temperature) + " "
						+ (char) 0x00B0 + "C");
			else if (temperatureUnit.equals(getResources().getStringArray(
					R.array.prefs_temp_unit_vals)[FAHRENHEIT]))
				tvTemprature.setText(String.format("%.0f",
						temperature * 9 / 5 + 32) + " " + (char) 0x00B0 + "F");
			else
				tvTemprature.setText(String.format("%.0f", temperature + 273)
						+ " K");

			Log.d(TAG, "Got temperature sensor event: " + temperature);
		}

		if ((showRelativeHumidity || showDewPoint || showAbsoluteHumidity)
				&& event.sensor.equals(sensors[S_RELATIVE_HUMIDITY])) {
			relativeHumidity = event.values[0];

			tvRelativeHumidity.setText(String.format("%.0f", relativeHumidity)
					+ " %");

			Log.d(TAG, "Got relative humidity sensor event: "
					+ relativeHumidity);
		}

		if (showAbsoluteHumidity
				&& (event.sensor.equals(sensors[S_TEMPRATURE]) || event.sensor
						.equals(sensors[S_RELATIVE_HUMIDITY]))) {
			updateAbsoluteHumidity();
		}

		if (showPressure && event.sensor.equals(sensors[S_PRESSURE])) {
			pressure = event.values[0];

			tvPressure.setText(String.format("%.0f", pressure) + " hPa");

			Log.d(TAG, "Got pressure sensor event: " + pressure);
		}

		if (showDewPoint
				&& (event.sensor.equals(sensors[S_TEMPRATURE]) || event.sensor
						.equals(sensors[S_RELATIVE_HUMIDITY]))) {
			updateDewPoint();
		}

		if (showLight && event.sensor.equals(sensors[S_LIGHT])) {
			light = event.values[0];

			tvLight.setText(String.format("%.0f", light) + " lx");

			Log.d(TAG, "Got light sensor event: " + light);
		}

		if (showMagneticField && event.sensor.equals(sensors[S_MAGNETIC_FIELD])) {
			float magneticFieldX = event.values[0];
			float magneticFieldY = event.values[1];
			float magneticFieldZ = event.values[2];
			magneticField = magneticFieldX + magneticFieldY + magneticFieldZ;

			tvMagneticField.setText(String.format("%.0f", magneticField) + " "
					+ (char) 0x03BC + "T");

			Log.d(TAG, "Got magnetic field sensor event: " + magneticField);
		}
	}

	private void updateAbsoluteHumidity() {
		absoluteHumidity = (float) (ABSOLUTE_HUMIDITY_CONSTANT * (relativeHumidity
				/ HUNDRED_PERCENT
				* A
				* Math.exp(M * temperature / (TN + temperature)) / (ZERO_ABSOLUTE + temperature)));

		tvAbsoluteHumidity.setText(Html.fromHtml(String.format("%.0f",
				absoluteHumidity) + " g/m<sup><small>3</small></sup>"));

		Log.d(TAG, "Absolute humidity updated: " + absoluteHumidity);
	}

	private void updateDewPoint() {
		double h = Math.log(relativeHumidity / HUNDRED_PERCENT)
				+ (M * temperature) / (TN + temperature);
		dewPoint = (float) (TN * h / (M - h));

		if (temperatureUnit.equals(getResources().getStringArray(
				R.array.prefs_temp_unit_vals)[CELSIUS]))
			tvDewPoint.setText(String.format("%.0f", dewPoint) + " "
					+ (char) 0x00B0 + "C");
		else if (temperatureUnit.equals(getResources().getStringArray(
				R.array.prefs_temp_unit_vals)[FAHRENHEIT]))
			tvDewPoint.setText(String.format("%.0f", dewPoint
					* FAHRENHEIT_FACTOR + FAHRENHEIT_CONSTANT)
					+ " " + (char) 0x00B0 + "F");
		else
			tvDewPoint.setText(String.format("%.0f", dewPoint + ZERO_ABSOLUTE)
					+ " K");

		Log.d(TAG, "Dew point updated: " + dewPoint);
	}

}
