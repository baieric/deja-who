package com.parse.integratingfacebooktutorial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class MainActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Determine whether the current user is an anonymous user
		ParseUser currentUser = ParseUser.getCurrentUser();
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			Intent intent = new Intent(MainActivity.this, RelationshipListActivity.class);
			startActivity(intent);
			finish();
		}else{
			Intent intent = new Intent(MainActivity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
		}

	}
}
