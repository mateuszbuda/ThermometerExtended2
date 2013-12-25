package pl.narfsoftware.thermometer2;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jjoe64.graphview.GraphView.GraphViewData;

public class SensorData
{
	static final String TAG = "SensorData";

	private static int ID = 0;

	Context context;

	DbHelper dbHelper;
	SQLiteDatabase database;

	public SensorData(Context context)
	{
		this.context = context;
		dbHelper = new DbHelper(context);
	}

	public void insert(String table, long timestamp, double value)
	{
		database = dbHelper.getWritableDatabase();

		ContentValues values = getAsContentVaues(timestamp, value);

		database.insertWithOnConflict(table, null, values,
				SQLiteDatabase.CONFLICT_IGNORE);

		Log.d(TAG, "insert into " + table + " values (" + timestamp + ", "
				+ value + ")");
	}

	public GraphViewData[] query(String table)
	{
		database = dbHelper.getReadableDatabase();

		Cursor cursor = database.query(table, null, null, null, null, null,
				DbHelper.C_TIMESTAMP + " asc");

		List<GraphViewData> graphViewData = new ArrayList<GraphViewData>();

		while (cursor.moveToNext())
		{
			long timestamp = cursor.getLong(cursor
					.getColumnIndex(DbHelper.C_TIMESTAMP));
			double value = cursor.getDouble(cursor
					.getColumnIndex(DbHelper.C_VALUE));
			graphViewData.add(new GraphViewData(timestamp, value));
		}

		cursor.close();

		return graphViewData.toArray(new GraphViewData[0]);
	}

	public void deleteAll()
	{
		database = dbHelper.getWritableDatabase();
		database.delete(DbHelper.TABLE_TEMPERATUE, null, null);
		database.delete(DbHelper.TABLE_ABSOLUTE_HUMIDITY, null, null);
		database.delete(DbHelper.TABLE_RELATIVE_HUMIDITY, null, null);
		database.delete(DbHelper.TABLE_PRESSURE, null, null);
		database.delete(DbHelper.TABLE_DEW_POINT, null, null);
		database.delete(DbHelper.TABLE_LIGHT, null, null);
		database.delete(DbHelper.TABLE_MAGNETIC_FIELD, null, null);
	}

	public void close()
	{
		if (database != null)
			database.close();
		if (dbHelper != null)
			dbHelper.close();
	}

	private ContentValues getAsContentVaues(long timestamp, double value)
	{
		ContentValues values = new ContentValues();

		values.put(DbHelper.C_ID, SensorData.ID++);
		if ((Long) timestamp != null)
			values.put(DbHelper.C_TIMESTAMP, timestamp);
		if ((Double) value != null)
			values.put(DbHelper.C_VALUE, value);

		return values;
	}
}