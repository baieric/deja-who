package com.parse.integratingfacebooktutorial;

/**
 * Created by Eric on 2014-09-25.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.net.Uri;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;

@ParseClassName("Encounter")
public class Encounter extends ParseObject {

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching
     * policy, but only call the callback once, with the first data available.
     */
    private abstract static class EncounterFindCallback extends FindCallback<Encounter> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<Encounter> objects, ParseException e) {
            if (!calledCallback) {
                if (objects != null) {
                    // We got a result, use it.
                    calledCallback = true;
                    doneOnce(objects, null);
                } else if (!isCachedResult) {
                    // We got called back twice, but got a null result both
                    // times. Pass on the latest error.
                    doneOnce(null, e);
                }
            }
            isCachedResult = false;
        }

        /**
         * Override this method with the callback that should only be called
         * once.
         */
        protected abstract void doneOnce(List<Encounter> objects, ParseException e);
    }

    /**
     * Creates a query for talks with all the includes
     */
    private static ParseQuery<Encounter> createQuery() {
        ParseQuery<Encounter> query = new ParseQuery<Encounter>(Encounter.class);
        query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
        return query;
    }

    /**
     * Gets the objectId of the Talk associated with the given URI.
     */
    public static String getEncounterId(Uri uri) {
        List<String> path = uri.getPathSegments();
        if (path.size() != 2 || !"encounter".equals(path.get(0))) {
            throw new RuntimeException("Invalid URI for encounter: " + uri);
        }
        return path.get(1);
    }

    /**
     * Retrieves the set of all talks, ordered by time. Uses the cache if
     * possible.
     */
    public static void findInBackground(ParseUser user,
                                        final FindCallback<Encounter> callback) {
        ParseQuery<Encounter> query1 = Encounter.createQuery();
        query1.whereEqualTo("user1", user);
        ParseQuery<Encounter> query2 = Encounter.createQuery();
        query1.whereEqualTo("user2", user);
        List<ParseQuery<Encounter>> queries = new ArrayList<ParseQuery<Encounter>>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<Encounter> mainQuery = ParseQuery.or(queries);
        mainQuery.orderByDescending("createdAt");
        mainQuery.include("user1");
        mainQuery.include("user2");

        mainQuery.findInBackground(new EncounterFindCallback() {
            @Override
            protected void doneOnce(List<Encounter> objects, ParseException e) {
                if (e == null) {
                    Log.d("encounters", "Retrieved " + objects.size() + " encounters");
                } else {
                    Log.d("encounters", "Error: " + e.getMessage());
                }
                if (objects != null) {

                }
                callback.done(objects, e);
            }
        });
    }

    /**
     * Gets the data for a single talk. We use this instead of calling fetch on
     * a ParseObject so that we can use query cache if possible.
     */
    public static void getInBackground(final String objectId,
                                       final GetCallback<Encounter> callback) {
        ParseQuery<Encounter> query = Encounter.createQuery();
        query.whereEqualTo("objectId", objectId);
        query.findInBackground(new EncounterFindCallback() {
            @Override
            protected void doneOnce(List<Encounter> objects, ParseException e) {
                if (objects != null) {
                    // Emulate the behavior of getFirstInBackground by using
                    // only the first result.
                    if (objects.size() < 1) {
                        callback.done(null, new ParseException(
                                ParseException.OBJECT_NOT_FOUND,
                                "No encounter with id " + objectId + " was found."));
                    } else {
                        callback.done(objects.get(0), e);
                    }
                } else {
                    callback.done(null, e);
                }
            }
        });
    }

    /*public static void findInBackground(String title,
                                        final GetCallback<Encounter> callback) {
        ParseQuery<Encounter> talkQuery = ParseQuery.getQuery(Encounter.class);
        talkQuery.whereEqualTo("title", title);
        talkQuery.getFirstInBackground(new GetCallback<Encounter>() {

            @Override
            public void done(Encounter encounter, ParseException e) {
                if (e == null) {
                    callback.done(encounter, null);
                } else {
                    callback.done(null, e);
                }
            }
        });
    }*/

    /**
     * Returns a URI to use in Intents to represent this talk. The format is
     * f8://talk/theObjectId
     */
   /* public Uri getUri() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("f8");
        builder.path("encounter/" + getObjectId());
        return builder.build();
    }*/

    public ParseUser getUser() {
        //((ParseUser) get("user1")).equals(ParseUser.getCurrentUser())
        if ( ((ParseUser) (get("user1"))).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            return (ParseUser) get("user2");
        }else{
            return (ParseUser) get("user2");
        }
    }

    /*public String getVideoID() {
        String videoID = getString("videoID");
        if (videoID == null) {
            videoID = "";
        }
        return videoID;
    }

    public String getAbstract() {
        String talkAbstract = getString("abstract");
        if (talkAbstract == null) {
            talkAbstract = "";
        }
        return talkAbstract;
    }

    public ParseFile getIcon() {
        return getParseFile("icon");
    }

    public List<Speaker> getSpeakers() {
        return getList("speakers");
    }

    public Slot getSlot() {
        return (Slot) get("slot");
    }

    public Room getRoom() {
        return (Room) get("room");
    } */

    /**
     * Items like breaks and the after party are marked as "alwaysFavorite" so
     * they always show up on the Favorites tab of the schedule. We also color
     * them slightly differently to make the UI prettier.
     */
    /*public boolean isAlwaysFavorite() {
        return getBoolean("alwaysFavorite");
    }

    public boolean allDay() {
        return getBoolean("allDay");
    }

    public boolean isBreak() {
        return getBoolean("isBreak");
    }*/

}