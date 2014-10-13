package com.parse.integratingfacebooktutorial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class UserDetailsActivity extends Activity {

	private ProfilePictureView userProfilePictureView;
    private TextView userLocationView;
	private TextView userGenderView;
	private TextView userRelationshipView;
	private TextView numEncounterView;
	private TextView lastPassedView;
	private Button interestButton;
	private Button logoutButton;
    private ListView userLikes;
    private static Relationship r;
    private static ParseUser friend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userdetails);
        if(getIntent().getStringExtra("parent").equals("RelationshipListActivity")) {
        	r = RelationshipListActivity.selected;
            friend = r.getUser();
            ParseUser currentuser = ParseUser.getCurrentUser();
            if(currentuser.hasSameId(r.getParseUser("user1"))){
            	if(!r.getBoolean("user1Read")){
            		r.put("user1Read", true);
            		r.saveInBackground();
            	}
            }
            if(friend.hasSameId(r.getParseUser("user1"))){
            	if(!r.getBoolean("user2Read")){
            		r.put("user2Read", true);
            		r.saveInBackground();
            	}
            }
        }

		userProfilePictureView = (ProfilePictureView) findViewById(R.id.userProfilePicture);
		userLocationView = (TextView) findViewById(R.id.userLocation);
		userGenderView = (TextView) findViewById(R.id.userGender);
		//userRelationshipView = (TextView) findViewById(R.id.userRelationship);
        userLikes = (ListView) findViewById(R.id.lsvLikes);
        numEncounterView = (TextView) findViewById(R.id.numEncounters);
        lastPassedView = (TextView) findViewById(R.id.lastpassed);
        

        //Query Parse for the
        //System.out.printf("1");
        // Getting mutual likes
        Session newSession = Session.getActiveSession();
//        String facebookID = "/10203769898401857"; //Parameter should be passed from Home
        String facebookID = "/me"; //Parameter should be passed from Home
        Bundle params = new Bundle();
        params.putString("fields", "context.fields(mutual_likes)");

        /* make the API call */
        RequestAsyncTask requestAsyncTask = new Request(
                newSession,
                facebookID,
                params,
                HttpMethod.GET,
                new Request.Callback() {
                    public void onCompleted(Response response) {
                        Log.i("222222", "2222");

                        GraphObject objMutualLikes = response.getGraphObject();


                        Map<String, Object> likes = objMutualLikes.asMap();
                        for (String like : likes.keySet()) {
                            Log.i("async", like);
                            Log.i("second:   ", likes.get(like).toString());
                        }
                        Log.i("length: " + likes.toString(),"length: " + likes.toString());
//                        JSONArray[] arrMutualLikes = objMutualLikes.getJSONArray("data");
//                        System.out.printf("length: %d", arrMutualLikes.length);
//                        for (int i = 0; i <= arrMutualLikes.length; i++) {
//                            String likeID = arrMutualLikes[i].getString("id");
//                            System.out.printf("id: %s", arrMutualLikes[i].getString("id"));
//
//                        }

                    }
                }
        ).executeAsync();
        
        interestButton = (Button) findViewById(R.id.interestbutton);
        

        logoutButton = (Button) findViewById(R.id.logoutButton);
		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLogoutButtonClicked();
			}
		});
        updateViewsWithProfileInfo();
	}

	@Override
	public void onResume() {
		super.onResume();
		updateViewsWithProfileInfo();
	}

	private void updateViewsWithProfileInfo() {
		ParseUser currentUser = friend;
		if (currentUser.get("profile") != null) {
			JSONObject userProfile = currentUser.getJSONObject("profile");
			try {
				if (userProfile.getString("facebookId") != null) {
					String facebookId = userProfile.get("facebookId")
							.toString();
					userProfilePictureView.setProfileId(facebookId);
				} else {
					// Show the default, blank user profile picture
					userProfilePictureView.setProfileId(null);
				}
				if (userProfile.getString("gender") != null) {
					userGenderView.setText(userProfile.getString("gender"));
				} else {
					userGenderView.setText("error");
				}
                if (userProfile.getString("firstName") != null) {
                    getActionBar().setTitle(userProfile.getString("firstName"));
                } else {
                    getActionBar().setTitle("error");
                }
				/*if (userProfile.getString("relationship_status") != null) {
					userRelationshipView.setText(userProfile
							.getString("relationship_status"));
				} else {
					userRelationshipView.setText("Not specified");
				}*/
                if (userProfile.getString("location") != null) {
                    userLocationView.setText(userProfile.getString("location"));
                } else {
                    userLocationView.setText("");
                }
			} catch (JSONException e) {
				Log.d(IntegratingFacebookTutorialApplication.TAG,
						"Error parsing saved user data.");
			}

		}
	}

	private void onLogoutButtonClicked() {
		// Log the user out
		ParseUser.logOut();

		// Go to the login view
		startLoginActivity();
	}

	private void startLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}
