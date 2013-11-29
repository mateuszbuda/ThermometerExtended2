package pl.narfsoftware.thermometer2;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SensorsListViewAdapter extends ArrayAdapter<SensorRow> {
	static final String TAG = "SensorsListViewAdapter";

	Context context;

	public SensorsListViewAdapter(Context context, int resource,
			List<SensorRow> objects) {
		super(context, resource, objects);
		this.context = context;
	}

	private class ViewHolder {
		ImageView sensorIcon;
		TextView sensorType;
		TextView sensorData;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		SensorRow sensorRow = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.sensor_row, null);
			holder = new ViewHolder();
			holder.sensorData = (TextView) convertView
					.findViewById(R.id.sensorData);
			holder.sensorType = (TextView) convertView
					.findViewById(R.id.sensorType);
			holder.sensorIcon = (ImageView) convertView
					.findViewById(R.id.sensorIcon);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.sensorData.setText(sensorRow.getStringValue());
		holder.sensorType.setText(sensorRow.getSensorName());
		holder.sensorIcon.setImageResource(sensorRow.getIcon());

		return convertView;
	}
}
