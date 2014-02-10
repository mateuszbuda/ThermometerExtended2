package pl.narfsoftware.thermometer.ui.listeners;

import java.util.HashMap;

import pl.narfsoftware.thermometer.ui.SensorsListViewAdapter;
import pl.narfsoftware.thermometer.utils.SensorRow;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.content.Context;
import android.hardware.Sensor;

public class UIListenersFactory {

	Context context;
	SensorsListViewAdapter adapter;
	HashMap<Integer, SensorRow> sensorRows;

	public UIListenersFactory(Context context, SensorsListViewAdapter adapter,
			HashMap<Integer, SensorRow> sensorRows) {
		this.context = context;
		this.adapter = adapter;
		this.sensorRows = sensorRows;
	}

	/**
	 * Create UIListener according to passed key, that is sesnors' type
	 * 
	 * @param key
	 *            Listeners' key
	 * @return Listener
	 * @throws IllegalArgumentException
	 *             when unsupported key is passed
	 */
	public BaseUIListener createListener(int key)
			throws IllegalArgumentException {
		switch (key) {
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
			return new TemperatureListener(context, adapter,
					sensorRows.get(key));
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			return new RelativeHumidityListener(context, adapter,
					sensorRows.get(key));
		case Sensors.TYPE_ABSOLUTE_HUMIDITY:
			return new AbsoluteHumidityListener(context, adapter,
					sensorRows.get(key));
		case Sensor.TYPE_PRESSURE:
			return new PressureListener(context, adapter, sensorRows.get(key));
		case Sensors.TYPE_DEW_POINT:
			return new DewPointListener(context, adapter, sensorRows.get(key));
		case Sensor.TYPE_LIGHT:
			return new LightListener(context, adapter, sensorRows.get(key));
		case Sensor.TYPE_MAGNETIC_FIELD:
			return new MagneticFieldListener(context, adapter,
					sensorRows.get(key));

		default:
			throw new IllegalArgumentException(
					"There is no listener for given type " + key);
		}
	}
}
