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
public class RelationshipListActivity extends Activity {

    private RelationshipAdapter adapter;
    public static Relationship selected;

    // private List<Relationship> Relationships = new ArrayList<Relationship>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent startIntent = new Intent(this, GPSTracker.class);
	    startService(startIntent);
        
	    setContentView(R.layout.relationshiplist);
        ListView list = (ListView) findViewById(R.id.relationshipList);
        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));*/
        adapter = new RelationshipAdapter(this, true);
        list.setAdapter(adapter);
        ParseUser user = ParseUser.getCurrentUser();

        Relationship.findInBackground(user, new FindCallback<Relationship>() {
            @Override
            public void done(List<Relationship> Relationships, ParseException e) {
                //Favorites.get().addListener(MyScheduleActivity.this);

                if (e != null) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            e.getMessage(), Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                if (Relationships != null) {
                    for (Relationship Relationship : Relationships) {
                        adapter.add(Relationship);
                    }
                }

            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Relationship Relationship = adapter.getItem(position);
                selected = Relationship;
                showUserDetailsActivity();
            }
        });
    }

    private void showUserDetailsActivity() {
        Intent intent = new Intent(this, UserDetailsActivity.class);
        intent.putExtra("parent", "RelationshipListActivity");
        startActivity(intent);
    }
}