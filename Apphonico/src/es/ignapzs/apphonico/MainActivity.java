package es.ignapzs.apphonico;

import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockActivity implements OnClickListener,
		OnInitListener {
	// The button for speaking
	private Button speakButton;
	// The text for speaking
	private EditText enteredText;
	// TTS object
	private static TextToSpeech myTTS;
	// status check code
	private static int myDataCheckCode = 0;
	// Speach params
	private HashMap<String, String> speechParams;
	// The default locale
	private Locale defLocale;
	// The shared preferences
	private SharedPreferences sp;

	// create the Activity
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(getResources().getString(R.string.app_name), "onCreate()");

		setContentView(R.layout.activity_main);
		// get a reference to the button element listed in the XML layout
		speakButton = (Button) findViewById(R.id.speak_button);
		// listen for clicks
		speakButton.setOnClickListener(this);

		enteredText = (EditText) findViewById(R.id.textToSpeakEditText);

		// set the parameters for the speech
		speechParams = new HashMap<String, String>();
		speechParams
				.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");

		// check for TTS data
		Intent checkTTSIntent = new Intent();
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		// start the activity
		startActivityForResult(checkTTSIntent, myDataCheckCode);

		// the default locale will be used later.
		defLocale = null;
		// get the shared preferences, so we could get different values later
		sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		// Change the menu icons
		MenuItem item1 = menu.findItem(R.id.clear_text);
		MenuItem item2 = menu.findItem(R.id.preferences);
		item1.setIcon(R.drawable.clear_button_state);
		item2.setIcon(R.drawable.menu_button_state);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// if we select the "clear text" option, we do it!
		case R.id.clear_text:
			enteredText.getText().clear();
			return true;
			// Show the preferences
		case R.id.preferences:
			Intent in = new Intent(MainActivity.this,
					MyPreferencesActivity.class);
			startActivity(in);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		Log.d(getResources().getString(R.string.app_name), "onResume()");
		if (myTTS != null) {
			if (Build.VERSION.SDK_INT >= 18) {
				defLocale = myTTS.getDefaultLanguage();
			} else {
				defLocale = myTTS.getLanguage();
			}
			// TODO When the lollipop comes...
			// defLocale = myTTS.getDefaultVoice().getLocale();
			myTTS.setLanguage(defLocale);

			if (myTTS.isSpeaking()) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						speakButton.setText(getResources().getString(
								R.string.shutup));
					}
				});
			} else {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						speakButton.setText(getResources().getString(
								R.string.speak));
					}
				});
			}
		}
		super.onResume();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onPause() {
		Log.d(getResources().getString(R.string.app_name), "onPause()");
		if (myTTS != null) {
			if (Build.VERSION.SDK_INT < 18) {
				defLocale = myTTS.getLanguage();
			} else {
				defLocale = myTTS.getDefaultLanguage();
			}
			// TODO When the lollipop comes
			// defLocale = myTTS.getVoice().getLocale();
			myTTS.stop();
		}
		super.onPause();
	}

	@Override
	public void onDestroy() {
		Log.d(getResources().getString(R.string.app_name), "onDestroy()");
		// Don't forget to shutdown TTS!
		if (myTTS != null) {
			myTTS.stop();
			myTTS.shutdown();
		}
		super.onDestroy();
	}

	public void onClick(View v) {
		Log.d(getResources().getString(R.string.app_name),
				"onClick() del button");
		String buttonOnText = speakButton.getText().toString();
		if (buttonOnText.equals(getResources().getString(R.string.speak))) {
			String speech = enteredText.getText().toString();
			if (speech.equals("") || speech == null) {
				Toast.makeText(this, getString(R.string.instructions_text),
						Toast.LENGTH_SHORT).show();
			} else {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						speakButton.setText(getResources().getString(
								R.string.shutup));
					}
				});
				String pitch = sp.getString("pref_pitch", null);
				String rate = sp.getString("pref_rate", null);
				if (!((pitch == null) || (rate == null))) {
					myTTS.setPitch(Float.parseFloat(pitch));
					myTTS.setSpeechRate(Float.parseFloat(rate));
				}
				// TODO Check the method when the lollipop comes
				myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, speechParams);
			}
		} else if (buttonOnText.equals(getResources()
				.getString(R.string.shutup))
				|| buttonOnText.equals(getResources().getString(
						R.string.speaking))) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					speakButton.setText(getResources()
							.getString(R.string.speak));
				}
			});
			myTTS.stop();
		}

	}

	// act on result of TTS data check
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(getResources().getString(R.string.app_name), "onActivityResut()");
		if (requestCode == myDataCheckCode) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// the user has the necessary data - create the TTS
				myTTS = new TextToSpeech(this, this);
			} else {
				// no data - install it now
				Intent installTTSIntent = new Intent();
				installTTSIntent
						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installTTSIntent);
			}
		}
	}

	// setup TTS
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void onInit(int initStatus) {
		Log.d(getResources().getString(R.string.app_name), "onInit()");
		// check for successful instantiation
		if (initStatus == TextToSpeech.SUCCESS) {
			// Locale defLocale = myTTS.getDefaultLanguage();
			// myTTS.setLanguage(defLocale);
			if (Build.VERSION.SDK_INT >= 18) {
				Locale defLocale = myTTS.getDefaultLanguage();
				myTTS.setLanguage(defLocale);
				myTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {

					@Override
					public void onStart(String utteranceId) {
						Log.d(getResources().getString(R.string.app_name),
								"onInit->onStart()");
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								speakButton.setText(getResources().getString(
										R.string.speaking));
								speakButton.setText(getResources().getString(
										R.string.shutup));
							}
						});

					}

					@Override
					public void onError(String utteranceId) {
						Log.e(getResources().getString(R.string.app_name),
								"onInit->onError()");
					}

					@Override
					public void onDone(String utteranceId) {
						Log.d(getResources().getString(R.string.app_name),
								"onInit->onDone()");
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// UI changes
								speakButton.setText(getResources().getString(
										R.string.speak));
							}
						});

					}

				});
			} else {
				Locale defLocale = myTTS.getLanguage();
				myTTS.setLanguage(defLocale);
				myTTS.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
					@Override
					public void onUtteranceCompleted(String utteranceId) {
						Log.d(getResources().getString(R.string.app_name),
								"progress on Completed " + utteranceId);
					}
				});
			}
		} else if (initStatus == TextToSpeech.ERROR) {
			Log.e(getResources().getString(R.string.app_name),
					"onInitStatus()- ERROR - " + getString(R.string.tts_failed));
			Toast.makeText(this, getString(R.string.tts_failed),
					Toast.LENGTH_LONG).show();
		}
		if (initStatus == TextToSpeech.SUCCESS) {
			Log.d(getResources().getString(R.string.app_name),
					"onInit->Status Success");

		}
	}
}
