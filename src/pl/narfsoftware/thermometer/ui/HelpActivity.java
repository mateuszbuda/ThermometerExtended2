package pl.narfsoftware.thermometer.ui;

import pl.narfsoftware.thermometer.R;
import pl.narfsoftware.thermometer.utils.Preferences;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class HelpActivity extends Activity implements
		HelpTitlesFragment.OnHelpTopicSelectedListener {
	static final String TAG = "HelpTitlesFragment";

	Preferences prefs;
	HelpDetailsFragment helpDetailsFragment;
	HelpTitlesFragment helpTitlesFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_view);
		// Show the Up button in the action bar.
		setupActionBar();
		prefs = new Preferences(this);

		// Check whether the activity is using the layout version with
		// the fragment_container FrameLayout. If so, we must add the first
		// fragment
		if (findViewById(R.id.help_fragment_container) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				return;
			}

			// Create an instance of SensorsFragment
			helpTitlesFragment = new HelpTitlesFragment();

			// In case this activity was started with special instructions from
			// an Intent, pass the Intent's extras to the fragment as arguments
			helpTitlesFragment.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getFragmentManager().beginTransaction()
					.add(R.id.help_fragment_container, helpTitlesFragment)
					.commit();
		}

		Log.d(TAG, "onCreated");
	}

	@Override
	protected void onResume() {
		super.onResume();

		LinearLayout backgroundLayout = ((LinearLayout) findViewById(R.id.backgroundLayoutHelp));
		if (prefs.theme.equals(""))
			backgroundLayout.setBackgroundColor(prefs.backgroundColor);
		else
			backgroundLayout.setBackgroundDrawable(prefs.themeDrawable);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTopicSelected(int position) {
		// The user selected the help topic from the HelpTitlesFragment

		// Capture the help details fragment from the activity layout
		helpDetailsFragment = (HelpDetailsFragment) getFragmentManager()
				.findFragmentById(R.id.help_details_fragment);

		if (helpDetailsFragment != null) {
			// If help details frag is available, we're in two-pane layout...

			// Call a method in the HelpDetailsFragment to update its content
			helpDetailsFragment.updateHelpDetailsFragment(position);

		} else {
			// If the frag is not available, we're in the one-pane layout and
			// must swap frags...

			// Create fragment and give it an argument for the selected topic
			HelpDetailsFragment newFragment = new HelpDetailsFragment();
			Bundle args = new Bundle();
			args.putInt(HelpDetailsFragment.ARG_INDEX, position);
			newFragment.setArguments(args);
			FragmentTransaction transaction = getFragmentManager()
					.beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment, and add the transaction to the back stack so the user
			// can navigate back
			transaction.replace(R.id.help_fragment_container, newFragment);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		}
	}

}
