package com.parse.integratingfacebooktutorial;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

public class IntegratingFacebookTutorialApplication extends Application {

	static final String TAG = "MyApp";

	@Override
	public void onCreate() {
		super.onCreate();
        ParseObject.registerSubclass(Relationship.class);

		Parse.initialize(this, "dtobAK5ZzqbPxzglwZ2ecOzo9FzqiGWdU2JnFBR8",
				"bPLRSYLUAVppB4Iqzva43FidTGYFMQUb94FrzBf4");

		// Set your Facebook App Id in strings.xml
		ParseFacebookUtils.initialize(getString(R.string.app_id));

	}

}
