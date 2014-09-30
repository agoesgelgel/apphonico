package es.ignapzs.apphonico;

import java.util.HashMap;
import java.util.Locale;

import es.ignapzs.apphonico.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener,
		OnInitListener {
	// The button for speaking
	private Button speakButton;
	// The text for speaking
	private EditText enteredText;
	// TTS object
	private TextToSpeech myTTS;
	// status check code
	private int myDataCheckCode = 0;
	// Speach params
	private HashMap<String, String> speechParams;

	// create the Activity
	public void onCreate(Bundle savedInstanceState) {
		Log.d(getResources().getString(R.string.app_name), "onCreate()");

		super.onCreate(savedInstanceState);

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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// inflate the menu
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// preparation code here
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// if we select the "clear text" option, we do it!
		if (item.getItemId() == R.id.clear_text) {
			enteredText.getText().clear();
		}
		// if we choose the "settings", launch the tts settings
		if (item.getItemId() == R.id.action_settings) {
			Intent intent = new Intent();
			intent.setAction("com.android.settings.TTS_SETTINGS");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(intent);
		}
		// if we click on the "decrease pitch" option, let it sound like a moose
		if (item.getItemId() == R.id.pitch_half) {
			myTTS.setPitch((float) 0.5);
		}
		// sound like a chipmunk if you say "increase pitch"
		if (item.getItemId() == R.id.pitch_double) {
			myTTS.setPitch(2);
		}
		// speak slowly
		if (item.getItemId() == R.id.rate_half) {
			myTTS.setSpeechRate((float) 0.5);
		}
		// speak twice faster
		if (item.getItemId() == R.id.rate_double) {
			myTTS.setSpeechRate(2);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		Log.d(getResources().getString(R.string.app_name), "onResume()");
		if (myTTS != null) {
			if (myTTS.isSpeaking()) {
				speakButton.setText(getResources().getString(R.string.shutup));
			} else {
				speakButton.setText(getResources().getString(R.string.speak));
			}
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d(getResources().getString(R.string.app_name), "onPause()");
		if (myTTS != null) {
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
			speakButton.setText(getResources().getString(R.string.shutup));
			String speech = enteredText.getText().toString();
			myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, speechParams);
		} else if (buttonOnText.equals(getResources()
				.getString(R.string.shutup))) {
			speakButton.setText(getResources().getString(R.string.speak));
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
	public void onInit(int initStatus) {
		Log.d(getResources().getString(R.string.app_name), "onInit()");
		// check for successful instantiation
		if (initStatus == TextToSpeech.SUCCESS) {
			if (myTTS.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
				myTTS.setLanguage(Locale.US);

			myTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {

				@Override
				public void onStart(String utteranceId) {
					Log.d(getResources().getString(R.string.app_name),
							"onInit->onStart()");
					speakButton.setText(getResources().getString(
							R.string.speaking));
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
