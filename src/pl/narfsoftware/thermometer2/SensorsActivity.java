package pl.narfsoftware.thermometer2;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SensorsActivity extends Activity implements
		SensorsFragment.OnSensorSelectedListener {
	static final String TAG = "SensorsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sensors_view);

		// Check whether the activity is using the layout version with
		// the fragment_container FrameLayout. If so, we must add the first
		// fragment
		if (findViewById(R.id.fragment_container) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				return;
			}

			// Create an instance of SensorsFragment
			SensorsFragment sensorsFragment = new SensorsFragment();

			// In case this activity was started with special instructions from
			// an Intent, pass the Intent's extras to the fragment as arguments
			sensorsFragment.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getFragmentManager().beginTransaction()
					.add(R.id.fragment_container, sensorsFragment).commit();
		}

		Log.d(TAG, "onCreated");
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sensors, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;

		case R.id.action_help:
			// startActivity(new Intent(this, HelpActivity.class));
			return true;

		case R.id.action_about:
			// startActivity(new Intent(this, AboutActivity.class));
			return true;

		case R.id.action_clear_data:

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSensorSelected(int position) {
		// The user selected the sensor from the SensorsFragment

		// Capture the plot fragment from the activity layout
		PlotFragment plotFrag = (PlotFragment) getFragmentManager()
				.findFragmentById(R.id.plot_fragment);

		if (plotFrag != null) {
			// If plot frag is available, we're in two-pane layout...

			// Call a method in the PlotFragment to update its content
			plotFrag.updatePlotFragment(position);

		} else {
			// If the frag is not available, we're in the one-pane layout and
			// must swap frags...

			// Create fragment and give it an argument for the selected sensor
			PlotFragment newFragment = new PlotFragment();
			Bundle args = new Bundle();
			args.putInt(PlotFragment.ARG_POSITION, position);
			newFragment.setArguments(args);
			FragmentTransaction transaction = getFragmentManager()
					.beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment, and add the transaction to the back stack so the user
			// can navigate back
			transaction.replace(R.id.fragment_container, newFragment);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		}
	}

}
