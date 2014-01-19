package pl.narfsoftware.thermometer.service.listeners;

import java.sql.Timestamp;
import java.util.Date;

import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.db.DbHelper;
import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;

public class LightListener extends DataSavingListener {
	static final String TAG = ".service.listeners.LightListener";

	public LightListener(Context context) {
		super(context);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (app.saveAmbientConditionData[ThermometerApp.LIGHT_INDEX]
				&& event.sensor.equals(app.getLightSensor())) {
			value = event.values[0];

			sensorData.insert(DbHelper.TABLE_LIGHT,
					(new Timestamp(new Date().getTime()).getTime()), value);

			Log.d(TAG, "Got light sensor event with value " + value);
		}
	}

}
