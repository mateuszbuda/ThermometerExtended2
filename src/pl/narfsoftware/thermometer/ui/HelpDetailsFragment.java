package pl.narfsoftware.thermometer.ui;

import pl.narfsoftware.thermometer.R;
import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.utils.Preferences;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class HelpDetailsFragment extends Fragment {
	static final String TAG = "HelpDetailsFragment";

	Activity activity;
	ThermometerApp app;
	Preferences preferences;
	final static String ARG_INDEX = "help_title_index";
	int currentIndex = 0;
	String[] helpDetails;
	static final int DEW_POINT_HELP_INDEX = 4;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		activity = getActivity();
		preferences = new Preferences(activity);
		app = ((ThermometerApp) activity.getApplication());

		// If activity recreated (such as from screen rotate), restore
		// the previous sensor selection set by onSaveInstanceState().
		// This is primarily necessary when in the two-pane layout.
		if (savedInstanceState != null) {
			currentIndex = savedInstanceState.getInt(ARG_INDEX);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View inflatedView = inflater.inflate(R.layout.help_details_view,
				container, false);

		// Inflate the layout for this fragment
		return inflatedView;
	}

	@Override
	public void onStart() {
		super.onStart();

		// During startup, check if there are arguments passed to the fragment.
		// onStart is a good place to do this because the layout has already
		// been applied to the fragment at this point so we can safely call the
		// method below that sets the article text.
		Bundle args = getArguments();
		if (args != null) {
			// Set plot based on argument passed in
			updateHelpDetailsFragment(args.getInt(ARG_INDEX));
		} else if (currentIndex != -1) {
			// Set plot based on saved instance state defined during
			// onCreateView
			updateHelpDetailsFragment(currentIndex);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		helpDetails = getResources().getStringArray(R.array.help_details);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the current article selection in case we need to recreate the
		// fragment
		outState.putInt(ARG_INDEX, currentIndex);
	}

	public void updateHelpDetailsFragment(int position) {
		helpDetails = getResources().getStringArray(R.array.help_details);
		currentIndex = position;
		((TextView) activity.findViewById(R.id.textViewHelpContent))
				.setText(helpDetails[position]);
		if (position == DEW_POINT_HELP_INDEX)
			((ImageView) activity.findViewById(R.id.imageHelp))
					.setImageResource(R.drawable.dewpoint);
		else
			((ImageView) activity.findViewById(R.id.imageHelp))
					.setImageResource(0);

		if (position > 1)
			((TextView) activity.findViewById(R.id.textViewHelpSource))
					.setText(R.string.help_text_source);
		else
			((TextView) activity.findViewById(R.id.textViewHelpSource))
					.setText("");
	}
}
