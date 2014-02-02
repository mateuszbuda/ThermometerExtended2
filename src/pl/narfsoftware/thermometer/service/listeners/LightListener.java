package pl.narfsoftware.thermometer.service.listeners;

import java.sql.Timestamp;
import java.util.Date;

import pl.narfsoftware.thermometer.db.DbHelper;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

public class LightListener extends BaseServiceListener {
	static final String TAG = ".service.listeners.LightListener";

	public LightListener(Context context) {
		super(context);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (app.saveAmbientCondition.get(Sensor.TYPE_LIGHT)) {
			if (event.sensor.getType() != Sensor.TYPE_LIGHT)
				return;

			value = event.values[0];

			sensorData.insert(DbHelper.TABLE_LIGHT,
					(new Timestamp(new Date().getTime()).getTime()), value);

			Log.d(TAG, "Got light sensor event with value " + value);
		}
	}

	@Override
	public boolean register() {
		return sensors.sensorManager.registerListener(this,
				sensors.sensors.get(Sensor.TYPE_LIGHT),
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	public void unregister() {
		sensors.sensorManager.unregisterListener(this,
				sensors.sensors.get(Sensor.TYPE_LIGHT));
	}

}
