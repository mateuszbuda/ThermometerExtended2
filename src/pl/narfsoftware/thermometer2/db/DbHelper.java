package pl.narfsoftware.thermometer2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	static final String TAG = "DbHelper";

	static final String DB_NAME = "sensors_data.db";
	static final int DB_VER = 1;

	public static final String TABLE_TEMPERATUE = "Temperature";
	public static final String TABLE_RELATIVE_HUMIDITY = "RelativeHumidity";
	public static final String TABLE_ABSOLUTE_HUMIDITY = "AbsoluteHumidity";
	public static final String TABLE_PRESSURE = "Pressure";
	public static final String TABLE_DEW_POINT = "DewPoint";
	public static final String TABLE_LIGHT = "Light";
	public static final String TABLE_MAGNETIC_FIELD = "MagneticField";

	public static final String[] TABLE_NAMES = { TABLE_TEMPERATUE,
			TABLE_RELATIVE_HUMIDITY, TABLE_ABSOLUTE_HUMIDITY, TABLE_PRESSURE,
			TABLE_DEW_POINT, TABLE_LIGHT, TABLE_MAGNETIC_FIELD };

	public static final String C_ID = "_id";
	public static final String C_TIMESTAMP = "timestamp";
	public static final String C_VALUE = "value";

	private SQLiteDatabase db;

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VER);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlCreate = "create table if not exists %s "
				+ "(%s int primary key not null, %s int, %s real)";
		String sql;

		sql = String.format(sqlCreate, TABLE_TEMPERATUE, C_ID, C_TIMESTAMP,
				C_VALUE);
		db.execSQL(sql);
		Log.d(TAG, "onCreate with SQL: " + sql);

		sql = String.format(sqlCreate, TABLE_RELATIVE_HUMIDITY, C_ID,
				C_TIMESTAMP, C_VALUE);
		db.execSQL(sql);
		Log.d(TAG, "onCreate with SQL: " + sql);

		sql = String.format(sqlCreate, TABLE_ABSOLUTE_HUMIDITY, C_ID,
				C_TIMESTAMP, C_VALUE);
		db.execSQL(sql);
		Log.d(TAG, "onCreate with SQL: " + sql);

		sql = String.format(sqlCreate, TABLE_PRESSURE, C_ID, C_TIMESTAMP,
				C_VALUE);
		db.execSQL(sql);
		Log.d(TAG, "onCreate with SQL: " + sql);

		sql = String.format(sqlCreate, TABLE_DEW_POINT, C_ID, C_TIMESTAMP,
				C_VALUE);
		db.execSQL(sql);
		Log.d(TAG, "onCreate with SQL: " + sql);

		sql = String.format(sqlCreate, TABLE_LIGHT, C_ID, C_TIMESTAMP, C_VALUE);
		db.execSQL(sql);
		Log.d(TAG, "onCreate with SQL: " + sql);

		sql = String.format(sqlCreate, TABLE_MAGNETIC_FIELD, C_ID, C_TIMESTAMP,
				C_VALUE);
		db.execSQL(sql);
		Log.d(TAG, "onCreate with SQL: " + sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_TEMPERATUE);
		db.execSQL("drop table if exists " + TABLE_RELATIVE_HUMIDITY);
		db.execSQL("drop table if exists " + TABLE_ABSOLUTE_HUMIDITY);
		db.execSQL("drop table if exists " + TABLE_PRESSURE);
		db.execSQL("drop table if exists " + TABLE_DEW_POINT);
		db.execSQL("drop table if exists " + TABLE_LIGHT);
		db.execSQL("drop table if exists " + TABLE_MAGNETIC_FIELD);
		onCreate(db);
	}
}
