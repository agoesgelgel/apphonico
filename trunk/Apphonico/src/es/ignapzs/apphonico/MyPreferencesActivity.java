package es.ignapzs.apphonico;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class MyPreferencesActivity extends SherlockPreferenceActivity {

	// Preferences
	public static final String SETTING_DISPLAY = "prefs_display";
	public static final String SETTING_INFO = "prefs_info";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String action = getIntent().getAction();

		int preferenceFile_toLoad = -1;

		if (action != null && action.equals(SETTING_DISPLAY)) {
			preferenceFile_toLoad = R.xml.preference_tts;
		} else if (action != null && action.equals(SETTING_INFO)) {
			preferenceFile_toLoad = R.xml.preference_contact;
		} else {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				preferenceFile_toLoad = R.xml.preference_headers_legacy;
			}
		}

		// TODO Borrar si es necesario
		if (preferenceFile_toLoad > -1) {
			addPreferencesFromResource(preferenceFile_toLoad);
		}

	}

	@SuppressLint("NewApi")
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preference_headers, target);
	}

	@Override
	protected boolean isValidFragment(String fragmentName) {
		return (ContactPreferenceFragment.class.getName().equals(fragmentName) || TTSPreferenceFragment.class
				.getName().equals(fragmentName));

	}
}