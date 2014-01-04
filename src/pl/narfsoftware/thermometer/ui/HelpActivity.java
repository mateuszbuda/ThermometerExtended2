package pl.narfsoftware.thermometer.ui;

import pl.narfsoftware.thermometer.R;
import pl.narfsoftware.thermometer.utils.Preferences;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

public class HelpActivity extends Activity {
	Preferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		// Show the Up button in the action bar.
		setupActionBar();
		prefs = new Preferences(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		ScrollView backgroundLayout = ((ScrollView) findViewById(R.id.scrollViewHelp));
		if (prefs.theme.equals(""))
			backgroundLayout.setBackgroundColor(prefs.backgroundColor);
		else
			backgroundLayout.setBackgroundDrawable(prefs.themeDrawable);

		((TextView) findViewById(R.id.textViewHelpAbsoluteHumidity))
				.setTypeface(prefs.typeface);
		((TextView) findViewById(R.id.textViewHelpAbsoluteHumidityHeader))
				.setTypeface(prefs.typeface);
		((TextView) findViewById(R.id.textViewHelpDewPoint))
				.setTypeface(prefs.typeface);
		((TextView) findViewById(R.id.textViewHelpDewPointHeader))
				.setTypeface(prefs.typeface);
		((TextView) findViewById(R.id.textViewHelpLight))
				.setTypeface(prefs.typeface);
		((TextView) findViewById(R.id.textViewHelpLightHeader))
				.setTypeface(prefs.typeface);
		((TextView) findViewById(R.id.textViewHelpMain))
				.setTypeface(prefs.typeface);
		((TextView) findViewById(R.id.textViewHelpMainHeader))
				.setTypeface(prefs.typeface);
		((TextView) findViewById(R.id.textViewHelpRelativeHumidity))
				.setTypeface(prefs.typeface);
		((TextView) findViewById(R.id.textViewHelpRelativeHumidityHeader))
				.setTypeface(prefs.typeface);
		((TextView) findViewById(R.id.textViewHelpSaveAndDisplayData))
				.setTypeface(prefs.typeface);
		((TextView) findViewById(R.id.textViewHelpSaveAndDisplayDataHeader))
				.setTypeface(prefs.typeface);
		((TextView) findViewById(R.id.textViewHelpSource))
				.setTypeface(prefs.typeface);
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

}