package pl.narfsoftware.thermometer2;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PlotFragment extends Fragment {
	static final String TAG = "PlotFragment";

	final static String ARG_POSITION = "position";
	int currentPosition = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// If activity recreated (such as from screen rotate), restore
		// the previous article selection set by onSaveInstanceState().
		// This is primarily necessary when in the two-pane layout.
		if (savedInstanceState != null) {
			currentPosition = savedInstanceState.getInt(ARG_POSITION);
		}

		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.plot_view, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		// During startup, check if there are arguments passed to the fragment.
		// onStart is a good place to do this because the layout has already
		// been
		// applied to the fragment at this point so we can safely call the
		// method
		// below that sets the article text.
		Bundle args = getArguments();
		if (args != null) {
			// Set article based on argument passed in
			updatePlotFragment(args.getInt(ARG_POSITION));
		} else if (currentPosition != -1) {
			// Set article based on saved instance state defined during
			// onCreateView
			updatePlotFragment(currentPosition);
		}
	}

	public void updatePlotFragment(int position) {
		TextView tmpValue = (TextView) getActivity()
				.findViewById(R.id.tmpValue);
		// tmpValue.setText(SensorsFragment.VALUES[position].toString());
		tmpValue.setText(TAG);
		currentPosition = position;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the current article selection in case we need to recreate the
		// fragment
		outState.putInt(ARG_POSITION, currentPosition);
	}
}
