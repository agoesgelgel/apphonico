package es.ignapzs.apphonico;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Write_Email extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.putExtra(Intent.EXTRA_EMAIL,
				new String[] { "ipzs.dev@gmail.com" });
		// Hope I wont regret this...
		emailIntent.setType("message/rfc822");
		emailIntent
				.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
				getString(R.string.what_do_you_want));

		startActivity(Intent.createChooser(emailIntent,
				getString(R.string.contact_developer)));
		this.onBackPressed();
	}
}
