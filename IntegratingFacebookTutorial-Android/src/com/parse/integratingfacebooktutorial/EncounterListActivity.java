package com.parse.integratingfacebooktutorial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Eric on 2014-09-24.
 */
public class EncounterListActivity extends Activity {

    private EncounterAdapter adapter;
    public static ParseUser selectedUser;

    // private List<Encounter> encounters = new ArrayList<Encounter>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent startIntent = new Intent(this, GPSTracker.class);
	    startService(startIntent);
        
	    setContentView(R.layout.encounterlist);
        ListView list = (ListView) findViewById(R.id.encounterList);
        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));*/
        adapter = new EncounterAdapter(this, true);
        list.setAdapter(adapter);
        ParseUser user = ParseUser.getCurrentUser();

        Encounter.findInBackground(user, new FindCallback<Encounter>() {
            @Override
            public void done(List<Encounter> encounters, ParseException e) {
                //Favorites.get().addListener(MyScheduleActivity.this);

                if (e != null) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            e.getMessage(), Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                if (encounters != null) {
                    for (Encounter encounter : encounters) {
                        adapter.add(encounter);
                    }
                }

            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Encounter encounter = adapter.getItem(position);
                selectedUser = encounter.getUser();
                showUserDetailsActivity();
            }
        });
    }

    private void showUserDetailsActivity() {
        Intent intent = new Intent(this, UserDetailsActivity.class);
        intent.putExtra("parent", "EncounterListActivity");
        startActivity(intent);
    }
}