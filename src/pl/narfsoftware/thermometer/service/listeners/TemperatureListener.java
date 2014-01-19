package pl.narfsoftware.thermometer.service.listeners;

import java.sql.Timestamp;
import java.util.Date;

import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.db.DbHelper;
import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;

public class TemperatureListener extends DataSavingListener {
	static final String TAG = ".service.listeners.TemperatureListener";

	public TemperatureListener(Context context) {
		super(context);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (app.saveAmbientConditionData[ThermometerApp.TEMPERATURE_INDEX]
				&& event.sensor.equals(app.getTemperatureSensor())) {
			value = event.values[0];

			sensorData.insert(DbHelper.TABLE_TEMPERATUE, (new Timestamp(
					new Date().getTime()).getTime()), value);

			Log.d(TAG, "Got temperature sensor event with value: " + value);
		}
	}
}
