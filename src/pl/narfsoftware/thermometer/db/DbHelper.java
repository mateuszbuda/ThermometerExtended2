package pl.narfsoftware.thermometer.db;

import java.util.HashMap;
import java.util.Hashtable;

import pl.narfsoftware.thermometer.utils.Sensors;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Sensor;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	static final String TAG = "DbHelper";

	static final String DB_NAME = "sensors_data.db";
	static final int DB_VER = 1;

	private static final String TABLE_TEMPERATUE = "Temperature";
	private static final String TABLE_RELATIVE_HUMIDITY = "RelativeHumidity";
	private static final String TABLE_ABSOLUTE_HUMIDITY = "AbsoluteHumidity";
	private static final String TABLE_PRESSURE = "Pressure";
	private static final String TABLE_DEW_POINT = "DewPoint";
	private static final String TABLE_LIGHT = "Light";
	private static final String TABLE_MAGNETIC_FIELD = "MagneticField";

	public static final HashMap<Integer, String> TABLE_NAMES = new HashMap<Integer, String>() {
		{
			put(Sensor.TYPE_AMBIENT_TEMPERATURE, TABLE_TEMPERATUE);
			put(Sensor.TYPE_RELATIVE_HUMIDITY, TABLE_RELATIVE_HUMIDITY);
			put(Sensors.TYPE_ABSOLUTE_HUMIDITY, TABLE_ABSOLUTE_HUMIDITY);
			put(Sensor.TYPE_PRESSURE, TABLE_PRESSURE);
			put(Sensors.TYPE_DEW_POINT, TABLE_DEW_POINT);
			put(Sensor.TYPE_LIGHT, TABLE_LIGHT);
			put(Sensor.TYPE_MAGNETIC_FIELD, TABLE_MAGNETIC_FIELD);
		};
	};

	public static final String C_ID = "_id";
	public static final String C_TIMESTAMP = "timestamp";
	public static final String C_VALUE = "value";

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VER);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlCreate = "create table if not exists %s "
				+ "(%s int primary key not null, %s int, %s real)";
		String sql;

		for (int key : TABLE_NAMES.keySet()) {
			sql = String.format(sqlCreate, TABLE_NAMES.get(key), C_ID,
					C_TIMESTAMP, C_VALUE);
			db.execSQL(sql);
			Log.d(TAG, "onCreate with SQL: " + sql);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// O.o'?
		// db.execSQL("drop table if exists " + TABLE_TEMPERATUE);
		// db.execSQL("drop table if exists " + TABLE_RELATIVE_HUMIDITY);
		// db.execSQL("drop table if exists " + TABLE_ABSOLUTE_HUMIDITY);
		// db.execSQL("drop table if exists " + TABLE_PRESSURE);
		// db.execSQL("drop table if exists " + TABLE_DEW_POINT);
		// db.execSQL("drop table if exists " + TABLE_LIGHT);
		// db.execSQL("drop table if exists " + TABLE_MAGNETIC_FIELD);
		// onCreate(db);
	}
}
