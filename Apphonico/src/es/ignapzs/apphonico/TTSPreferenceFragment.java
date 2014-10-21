package es.ignapzs.apphonico;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import es.ignapzs.apphonico.R;

@SuppressLint("NewApi")
public class TTSPreferenceFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference_tts);
	}

}
