package pl.narfsoftware.thermometer.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.narfsoftware.thermometer.R;
import pl.narfsoftware.thermometer.ThermometerApp;
import pl.narfsoftware.thermometer.utils.Preferences;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class HelpTitlesFragment extends ListFragment {
	static final String TAG = "HelpTitlesFragment";

	final static String[] FROM = { "help_topic" };
	final static int[] TO = { R.id.helpTitle };
	List<HashMap<String, String>> content = new ArrayList<HashMap<String, String>>();

	Activity activity;
	ThermometerApp app;
	Preferences preferences;
	SimpleAdapter adapter;

	OnHelpTopicSelectedListener callback;

	public interface OnHelpTopicSelectedListener {
		/** Called by HelpTitlesFragment when a list item is selected */
		public void onTopicSelected(int position);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;

		try {
			callback = (OnHelpTopicSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHelpTopicSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (ThermometerApp) activity.getApplication();
		preferences = new Preferences(activity);
	}

	@Override
	public void onStart() {
		super.onStart();

		// When in two-pane layout, set the listview to highlight the selected
		// list item
		// (We do this during onStart because at the point the listview is
		// available.)
		if (getFragmentManager().findFragmentById(R.id.help_titles_fragment) != null) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		String[] helpTitles = getResources()
				.getStringArray(R.array.help_titles);
		// Application recreates after user changes language?
		// TODO do it in broadcast receiver registered on language changed
		// intent (if exists ;)
		content.clear();
		for (String title : helpTitles) {
			HashMap<String, String> values = new HashMap<String, String>();
			values.put(FROM[0], title);
			content.add(values);
		}
		adapter = new SimpleAdapter(activity, content, R.layout.help_title_row,
				FROM, TO);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Notify the parent activity of selected item
		callback.onTopicSelected(position);
		// Set the item as checked to be highlighted when in two-pane layout
		getListView().setItemChecked(position, true);
	}
}
