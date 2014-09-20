package com.parse.integratingfacebooktutorial;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class IntegratingFacebookTutorialApplication extends Application {

	static final String TAG = "MyApp";

	@Override
	public void onCreate() {
		super.onCreate();

		Parse.initialize(this, "vC9hwbmPrAT9graUMjSYvGgHnSnYorWwfEn5RNPE",
				"WjKJPDrgeEKw5BhmdwzxtsWaUSYvmYozWoj8y3ug");

		// Set your Facebook App Id in strings.xml
		ParseFacebookUtils.initialize(getString(R.string.app_id));

	}

}