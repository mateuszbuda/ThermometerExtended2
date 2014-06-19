package pl.narfsoftware.thermometer.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import pl.narfsoftware.thermometer.R;
import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.preferences.Preferences;
import pl.narfsoftware.thermometer.ui.listeners.BaseUIListener;
import pl.narfsoftware.thermometer.ui.listeners.UIListenersFactory;
import pl.narfsoftware.thermometer.utils.SensorRow;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.app.Activity;
import android.app.ListFragment;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SensorsFragment extends ListFragment
{
	static final String TAG = "SensorsFragment";

	Activity activity;
	ThermometerApp app;
	Preferences prefs;

	String noData;
	String sensorUnavailavle;

	HashMap<Integer, SensorRow> sensorRows = new HashMap<Integer, SensorRow>();
	HashMap<Integer, String> sensorTitles = new HashMap<Integer, String>();
	HashMap<Integer, String> values = new HashMap<Integer, String>();
	HashMap<Integer, Integer> iconIds = new HashMap<Integer, Integer>();

	HashMap<Integer, BaseUIListener> listeners = new HashMap<Integer, BaseUIListener>();

	OnSensorSelectedListener callback;

	List<SensorRow> sensorsList;
	SensorsListViewAdapter adapter;

	// The container Activity must implement this interface so the frag can
	// deliver messages
	public interface OnSensorSelectedListener
	{
		/**
		 * Called by SensorsFragment when a list item is selected
		 * 
		 * @param key
		 *            Sensors' type - key in dictionary
		 */
		public void onSensorSelected(int key);
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		this.activity = activity;
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try
		{
			callback = (OnSensorSelectedListener) activity;
		} catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString()
					+ " must implement OnSensorSelectedListener");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		getListView().setClickable(true);
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		app = (ThermometerApp) activity.getApplication();
		prefs = app.getPrefs();
		noData = getResources().getString(R.string.sensor_no_data);
		sensorUnavailavle = getResources().getString(
				R.string.sensor_unavailable);
		init();

		Log.d(TAG, "onCreated");
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// When in two-pane layout, set the listview to highlight the selected
		// list item
		// (We do this during onStart because at the point the listview is
		// available.)
		if (getFragmentManager().findFragmentById(R.id.sensors_fragment) != null)
		{
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();

		initIcons();
		initDateAndTime();
		setAdapter();
		initListeners();
		registerChosenListeners();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		unregisterListeners();
		Log.d(TAG, "Sensors unregistered");
	}

	@Override
	public void onStop()
	{
		// O.o'?
		((LinearLayout) getActivity().findViewById(R.id.dateAndTime))
				.setVisibility(LinearLayout.GONE);
		super.onStop();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		v.setSelected(true);

		// Notify the parent activity of selected items' key
		int selectedItemKey = -1;
		SensorRow selected = (SensorRow) getListView().getItemAtPosition(
				position);
		for (int key : sensorRows.keySet())
			if (sensorRows.get(key).equals(selected))
			{
				selectedItemKey = key;
				break;
			}

		callback.onSensorSelected(selectedItemKey);
		updateIcon(selectedItemKey);
		// Set the item as checked to be highlighted when in two-pane layout
		// (tiaa)
		// getListView().setItemChecked(position, true);
	}

	private void setAdapter()
	{
		sensorsList = new ArrayList<SensorRow>();
		int key;
		if (prefs.getShowDataDict().get(key = Sensor.TYPE_AMBIENT_TEMPERATURE))
		{
			sensorRows.put(key,
					new SensorRow(iconIds.get(key), sensorTitles.get(key),
							values.get(key)));
			sensorsList.add(sensorRows.get(key));
		}
		if (prefs.getShowDataDict().get(key = Sensor.TYPE_RELATIVE_HUMIDITY))
		{
			sensorRows.put(key,
					new SensorRow(iconIds.get(key), sensorTitles.get(key),
							values.get(key)));
			sensorsList.add(sensorRows.get(key));
		}
		if (prefs.getShowDataDict().get(key = Sensors.TYPE_ABSOLUTE_HUMIDITY))
		{
			sensorRows.put(key,
					new SensorRow(iconIds.get(key), sensorTitles.get(key),
							values.get(key)));
			sensorsList.add(sensorRows.get(key));
		}
		if (prefs.getShowDataDict().get(key = Sensor.TYPE_PRESSURE))
		{
			sensorRows.put(key,
					new SensorRow(iconIds.get(key), sensorTitles.get(key),
							values.get(key)));
			sensorsList.add(sensorRows.get(key));
		}
		if (prefs.getShowDataDict().get(key = Sensors.TYPE_DEW_POINT))
		{
			sensorRows.put(key,
					new SensorRow(iconIds.get(key), sensorTitles.get(key),
							values.get(key)));
			sensorsList.add(sensorRows.get(key));
		}
		if (prefs.getShowDataDict().get(key = Sensor.TYPE_LIGHT))
		{
			sensorRows.put(key,
					new SensorRow(iconIds.get(key), sensorTitles.get(key),
							values.get(key)));
			sensorsList.add(sensorRows.get(key));
		}
		if (prefs.getShowDataDict().get(key = Sensor.TYPE_MAGNETIC_FIELD))
		{
			sensorRows.put(key,
					new SensorRow(iconIds.get(key), sensorTitles.get(key),
							values.get(key)));
			sensorsList.add(sensorRows.get(key));
		}

		adapter = new SensorsListViewAdapter(getActivity(),
				R.layout.sensor_row, sensorsList);

		adapter.registerDataSetObserver(new DataSetObserver()
		{
		});
		getListView().setDivider(null);
		setListAdapter(adapter);
	}

	private void initListeners()
	{
		UIListenersFactory factory = new UIListenersFactory(activity, adapter,
				sensorRows);
		for (int key : sensorTitles.keySet())
			listeners.put(key, factory.createListener(key));
	}

	private void registerChosenListeners()
	{
		for (int key : listeners.keySet())
			if (prefs.getShowDataDict().get(key))
				listeners.get(key).register();
	}

	private void unregisterListeners()
	{
		for (int key : listeners.keySet())
			listeners.get(key).unregister();
	}

	private void init()
	{
		initSensorTitles();
		initValues();
	}

	void initIcons()
	{
		iconIds.put(
				Sensor.TYPE_AMBIENT_TEMPERATURE,
				app.saveData(Sensor.TYPE_AMBIENT_TEMPERATURE) ? R.drawable.temprature
						: R.drawable.temprature_disabled);
		iconIds.put(
				Sensors.TYPE_ABSOLUTE_HUMIDITY,
				app.saveData(Sensors.TYPE_ABSOLUTE_HUMIDITY) ? R.drawable.absolute_humidity
						: R.drawable.absolute_humidity_disabled);
		iconIds.put(
				Sensor.TYPE_RELATIVE_HUMIDITY,
				app.saveData(Sensor.TYPE_RELATIVE_HUMIDITY) ? R.drawable.relative_humidity
						: R.drawable.relative_humidity_disabled);
		iconIds.put(Sensor.TYPE_PRESSURE,
				app.saveData(Sensor.TYPE_PRESSURE) ? R.drawable.pressure
						: R.drawable.pressure_disabled);
		iconIds.put(Sensors.TYPE_DEW_POINT, app
				.saveData(Sensors.TYPE_DEW_POINT) ? R.drawable.dew_point
				: R.drawable.dew_point_disabled);
		iconIds.put(Sensor.TYPE_LIGHT,
				app.saveData(Sensor.TYPE_LIGHT) ? R.drawable.light
						: R.drawable.light_disabled);
		iconIds.put(
				Sensor.TYPE_MAGNETIC_FIELD,
				app.saveData(Sensor.TYPE_MAGNETIC_FIELD) ? R.drawable.magnetic_field
						: R.drawable.magnetic_field_disabled);
	}

	private void initValues()
	{
		HashMap<Integer, Boolean> dict = app.getSensors().hasSensor;
		for (int key : dict.keySet())
			values.put(key, dict.get(key) ? noData : sensorUnavailavle);

		values.put(
				Sensors.TYPE_ABSOLUTE_HUMIDITY,
				dict.get(Sensor.TYPE_AMBIENT_TEMPERATURE)
						&& dict.get(Sensor.TYPE_RELATIVE_HUMIDITY) ? noData
						: sensorUnavailavle);
		values.put(
				Sensors.TYPE_DEW_POINT,
				dict.get(Sensor.TYPE_AMBIENT_TEMPERATURE)
						&& dict.get(Sensor.TYPE_RELATIVE_HUMIDITY) ? noData
						: sensorUnavailavle);
	}

	private void initSensorTitles()
	{
		Resources res = activity.getResources();
		sensorTitles.put(Sensor.TYPE_AMBIENT_TEMPERATURE,
				res.getString(R.string.ambient_temp_title));
		sensorTitles.put(Sensors.TYPE_ABSOLUTE_HUMIDITY,
				res.getString(R.string.absolute_humidity_title));
		sensorTitles.put(Sensor.TYPE_RELATIVE_HUMIDITY,
				res.getString(R.string.relative_humidity_title));
		sensorTitles.put(Sensor.TYPE_PRESSURE,
				res.getString(R.string.pressure_title));
		sensorTitles.put(Sensors.TYPE_DEW_POINT,
				res.getString(R.string.dew_point_title));
		sensorTitles
				.put(Sensor.TYPE_LIGHT, res.getString(R.string.light_title));
		sensorTitles.put(Sensor.TYPE_MAGNETIC_FIELD,
				res.getString(R.string.magnetic_field_title));
	}

	private void initDateAndTime()
	{
		LinearLayout dateAndTime = ((LinearLayout) activity
				.findViewById(R.id.dateAndTime));
		TextView date = (TextView) activity.findViewById(R.id.date);
		TextView time = (TextView) activity.findViewById(R.id.time);
		date.setTypeface(prefs.getTypeface());
		time.setTypeface(prefs.getTypeface());

		if (prefs.getDateFormat().equals("")
				&& prefs.getTimeFormat().equals(""))
		{
			dateAndTime.setVisibility(LinearLayout.GONE);
			return;
		} else if (prefs.getDateFormat().equals("")
				&& !prefs.getTimeFormat().equals(""))
		{
			dateAndTime.setVisibility(LinearLayout.VISIBLE);
			date.setVisibility(LinearLayout.GONE);
			time.setText(new SimpleDateFormat(prefs.getTimeFormat(), Locale
					.getDefault()).format(new Date().getTime()));
			return;
		} else if (prefs.getTimeFormat().equals("")
				&& !prefs.getDateFormat().equals(""))
		{
			dateAndTime.setVisibility(LinearLayout.VISIBLE);
			time.setVisibility(LinearLayout.GONE);
			date.setText(new SimpleDateFormat(prefs.getDateFormat(), Locale
					.getDefault()).format(new Date().getTime()));
			return;
		}

		dateAndTime.setVisibility(LinearLayout.VISIBLE);
		time.setVisibility(LinearLayout.VISIBLE);
		date.setVisibility(LinearLayout.VISIBLE);

		time.setText(new SimpleDateFormat(prefs.getTimeFormat(), Locale
				.getDefault()).format(new Date().getTime()));
		date.setText(new SimpleDateFormat(prefs.getDateFormat(), Locale
				.getDefault()).format(new Date().getTime()));
	}

	public void updateSensorsFragment(int key)
	{
		updateIcon(key);
	}

	private void updateIcon(int key)
	{
		int iconId = 0;
		switch (key)
		{
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
			iconId = app.saveData(key) ? R.drawable.temprature
					: R.drawable.temprature_disabled;
			break;
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			iconId = app.saveData(key) ? R.drawable.relative_humidity
					: R.drawable.relative_humidity_disabled;
			break;
		case Sensor.TYPE_PRESSURE:
			iconId = app.saveData(key) ? R.drawable.pressure
					: R.drawable.pressure_disabled;
			break;
		case Sensor.TYPE_LIGHT:
			iconId = app.saveData(key) ? R.drawable.light
					: R.drawable.light_disabled;
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			iconId = app.saveData(key) ? R.drawable.magnetic_field
					: R.drawable.magnetic_field_disabled;
			break;
		case Sensors.TYPE_ABSOLUTE_HUMIDITY:
			iconId = app.saveData(key) ? R.drawable.absolute_humidity
					: R.drawable.absolute_humidity_disabled;
			break;
		case Sensors.TYPE_DEW_POINT:
			iconId = app.saveData(key) ? R.drawable.dew_point
					: R.drawable.dew_point_disabled;
			break;
		}

		if (adapter == null)
		{
			adapter = (SensorsListViewAdapter) getListAdapter();
			if (adapter == null)
				return;
		}

		SensorRow sensorRow = sensorRows.get(key);

		if (sensorRow != null)
		{
			sensorRow.setIcon(iconId);
			adapter.notifyDataSetChanged();
		}
	}
}
