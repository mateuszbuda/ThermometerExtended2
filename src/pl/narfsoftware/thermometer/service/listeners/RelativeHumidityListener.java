package pl.narfsoftware.thermometer.service.listeners;

import java.sql.Timestamp;
import java.util.Date;

import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.db.DbHelper;
import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;

public class RelativeHumidityListener extends DataSavingListener {
	static final String TAG = ".service.listeners.RelativeHumidityListener";

	public RelativeHumidityListener(Context context) {
		super(context);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (app.saveAmbientConditionData[ThermometerApp.RELATIVE_HUMIDITY_INDEX]
				&& event.sensor.equals(app.getRelativeHumiditySensor())) {
			value = event.values[0];

			sensorData.insert(DbHelper.TABLE_RELATIVE_HUMIDITY, (new Timestamp(
					new Date().getTime()).getTime()), value);

			Log.d(TAG, "Got relative humidity sensor event with value: "
					+ value);
		}
	}

}
