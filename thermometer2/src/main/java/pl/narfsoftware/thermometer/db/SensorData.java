package pl.narfsoftware.thermometer.db;

import java.util.ArrayList;
import java.util.List;

import pl.narfsoftware.thermometer.preferences.Preferences;
import pl.narfsoftware.thermometer.utils.Converter;
import pl.narfsoftware.thermometer.utils.Sensors;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.util.Log;

import com.jjoe64.graphview.GraphView.GraphViewData;

public class SensorData {
	static final String TAG = "SensorData";

	private static int ID = 0;

	Context context;
	DbHelper dbHelper;
	SQLiteDatabase database;

	public SensorData(Context context) {
		this.context = context;
		dbHelper = new DbHelper(context);
	}

	public void insert(String table, long timestamp, double value) {
		database = dbHelper.getWritableDatabase();

		ContentValues values = getAsContentVaues(timestamp, value);

		database.insertWithOnConflict(table, null, values,
				SQLiteDatabase.CONFLICT_IGNORE);

		Log.d(TAG, "insert into " + table + " values (" + timestamp + ", "
				+ value + ")");
	}

	/**
	 * If table is not DbHelper.TABLE_TEMPERATUE and not
	 * DbHelper.TABLE_DEW_POINT unitCode can by any value, otherwise it should
	 * be unitCode defined in Preferences as static constants.
	 * 
	 * @param table
	 * @param unitCode
	 * @return
	 */
	public GraphViewData[] query(String table, int unitCode) {
		database = dbHelper.getReadableDatabase();

		if (table != DbHelper.TABLE_NAMES.get(Sensor.TYPE_AMBIENT_TEMPERATURE)
				&& table != DbHelper.TABLE_NAMES.get(Sensors.TYPE_DEW_POINT))
			// value returned by converter will remain unchanged
			unitCode = Preferences.CELSIUS;

		Cursor cursor = database.query(table, null, null, null, null, null,
				DbHelper.C_TIMESTAMP + " asc");

		List<GraphViewData> graphViewData = new ArrayList<GraphViewData>();

		while (cursor.moveToNext()) {
			long timestamp = cursor.getLong(cursor
					.getColumnIndex(DbHelper.C_TIMESTAMP));
			double value = cursor.getDouble(cursor
					.getColumnIndex(DbHelper.C_VALUE));
			graphViewData.add(new GraphViewData(timestamp, Converter
					.ConvertTemperature((float) value, unitCode)));
		}

		cursor.close();

		Log.d(TAG, "data rows count: " + graphViewData.size() + "from table"
				+ table);
		return graphViewData.toArray(new GraphViewData[0]);
	}

	public void deleteAll() {
		database = dbHelper.getWritableDatabase();

		for (int key : DbHelper.TABLE_NAMES.keySet())
			database.delete(DbHelper.TABLE_NAMES.get(key), null, null);

		database.close();
	}

	public void delete(int sensorType) {
		database = dbHelper.getWritableDatabase();
		database.delete(DbHelper.TABLE_NAMES.get(sensorType), null, null);
		database.close();
	}

	public void delete(String tableName) {
		database = dbHelper.getWritableDatabase();
		database.delete(tableName, null, null);
		database.close();
	}

	public void close() {
		if (database != null)
			database.close();
		if (dbHelper != null)
			dbHelper.close();
	}

	private ContentValues getAsContentVaues(long timestamp, double value) {
		ContentValues values = new ContentValues();

		values.put(DbHelper.C_ID, SensorData.ID++);
		if ((Long) timestamp != null)
			values.put(DbHelper.C_TIMESTAMP, timestamp);
		if ((Double) value != null)
			values.put(DbHelper.C_VALUE, value);

		return values;
	}
}