package pl.narfsoftware.thermometer.service.listeners;

import java.sql.Timestamp;
import java.util.Date;

import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.db.DbHelper;
import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;

public class PressureListener extends DataSavingListener {
	static final String TAG = ".service.listeners.PressureListener";

	public PressureListener(Context context) {
		super(context);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (app.saveAmbientConditionData[ThermometerApp.PRESSURE_INDEX]
				&& event.sensor.equals(app.getPressureSensor())) {
			value = event.values[0];

			sensorData.insert(DbHelper.TABLE_PRESSURE, (new Timestamp(
					new Date().getTime()).getTime()), value);

			Log.d(TAG, "Got pressure sensor event with value: " + value);
		}
	}

}
