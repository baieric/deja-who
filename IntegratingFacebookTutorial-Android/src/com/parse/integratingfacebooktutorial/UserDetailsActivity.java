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
	private Button logoutButton;
    private ListView userLikes;
    public static ParseUser friend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userdetails);
        if(getIntent().getStringExtra("parent").equals("EncounterListActivity")) {
            friend = EncounterListActivity.selectedUser;
        }

		userProfilePictureView = (ProfilePictureView) findViewById(R.id.userProfilePicture);
		userLocationView = (TextView) findViewById(R.id.userLocation);
		userGenderView = (TextView) findViewById(R.id.userGender);
		userRelationshipView = (TextView) findViewById(R.id.userRelationship);
        userLikes = (ListView) findViewById(R.id.lsvLikes);

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


        logoutButton = (Button) findViewById(R.id.logoutButton);
		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLogoutButtonClicked();
			}
		});

		// Fetch Facebook user info if the session is active
		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {
			makeMeRequest();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		ParseUser user = friend;
		if (user != null) {
			// Check if the user is currently logged
			// and show any cached content
			updateViewsWithProfileInfo();
		} else {
			// If the user is not logged in, go to the
			// activity showing the login view.
			startLoginActivity();
		}
	}

	private void makeMeRequest() {
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (user != null) {
							// Create a JSON object to hold the profile info
							JSONObject userProfile = new JSONObject();
							try {
								// Populate the JSON object

								userProfile.put("facebookId", user.getId());
								userProfile.put("firstName", user.getFirstName());
                                userProfile.put("lastName", user.getLastName());
                                if (user.getLocation().getProperty("name") != null) {
                                    userProfile.put("location", (String) user
                                            .getLocation().getProperty("name"));
                                }
								if (user.getProperty("gender") != null) {
									userProfile.put("gender",
											(String) user.getProperty("gender"));
								}
								if (user.getBirthday() != null) {
									userProfile.put("birthday",
											user.getBirthday());
								}
								if (user.getProperty("relationship_status") != null) {
									userProfile
											.put("relationship_status",
													(String) user
															.getProperty("relationship_status"));
								}

								// Save the user profile info in a user property
								ParseUser currentUser = friend;
								currentUser.put("profile", userProfile);
								currentUser.saveInBackground();

								// Show the user info
								updateViewsWithProfileInfo();
							} catch (JSONException e) {
								Log.d(IntegratingFacebookTutorialApplication.TAG,
										"Error parsing returned user data.");
							}

						} else if (response.getError() != null) {
							if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
									|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
								Log.d(IntegratingFacebookTutorialApplication.TAG,
										"The facebook session was invalidated.");
								onLogoutButtonClicked();
							} else {
								Log.d(IntegratingFacebookTutorialApplication.TAG,
										"Some other error: "
												+ response.getError()
														.getErrorMessage());
							}
						}
					}
				});
		request.executeAsync();

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
                if (userProfile.getString("location") != null) {
                    userLocationView.setText(userProfile.getString("location"));
                } else {
                    userLocationView.setText("");
                }
				if (userProfile.getString("gender") != null) {
					userGenderView.setText(userProfile.getString("gender"));
				} else {
					userGenderView.setText("");
				}
                if (userProfile.getString("firstName") != null) {
                    getActionBar().setTitle(userProfile.getString("firstName"));
                } else {
                    getActionBar().setTitle("");
                }
				if (userProfile.getString("relationship_status") != null) {
					userRelationshipView.setText(userProfile
							.getString("relationship_status"));
				} else {
					userRelationshipView.setText("Not specified");
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