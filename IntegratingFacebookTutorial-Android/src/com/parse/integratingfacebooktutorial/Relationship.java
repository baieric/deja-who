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

@ParseClassName("Relationship")
public class Relationship extends ParseObject {

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching
     * policy, but only call the callback once, with the first data available.
     */
    private abstract static class RelationshipFindCallback extends FindCallback<Relationship> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<Relationship> objects, ParseException e) {
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
        protected abstract void doneOnce(List<Relationship> objects, ParseException e);
    }

    /**
     * Creates a query for talks with all the includes
     */
    private static ParseQuery<Relationship> createQuery() {
        ParseQuery<Relationship> query = new ParseQuery<Relationship>(Relationship.class);
        query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
        return query;
    }

    /**
     * Gets the objectId of the Talk associated with the given URI.
     */
    public static String getRelationshipId(Uri uri) {
        List<String> path = uri.getPathSegments();
        if (path.size() != 2 || !"Relationship".equals(path.get(0))) {
            throw new RuntimeException("Invalid URI for Relationship: " + uri);
        }
        return path.get(1);
    }

    /**
     * Retrieves the set of all talks, ordered by time. Uses the cache if
     * possible.
     */
    public static void findInBackground(ParseUser user,
                                        final FindCallback<Relationship> callback) {
        ParseQuery<Relationship> query1 = Relationship.createQuery();
        query1.whereEqualTo("user1", user);
        ParseQuery<Relationship> query2 = Relationship.createQuery();
        query1.whereEqualTo("user2", user);
        List<ParseQuery<Relationship>> queries = new ArrayList<ParseQuery<Relationship>>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<Relationship> mainQuery = ParseQuery.or(queries);
        mainQuery.orderByDescending("createdAt");
        mainQuery.include("user1");
        mainQuery.include("user2");

        mainQuery.findInBackground(new RelationshipFindCallback() {
            @Override
            protected void doneOnce(List<Relationship> objects, ParseException e) {
                if (e == null) {
                    Log.d("Relationships", "Retrieved " + objects.size() + " Relationships");
                } else {
                    Log.d("Relationships", "Error: " + e.getMessage());
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
                                       final GetCallback<Relationship> callback) {
        ParseQuery<Relationship> query = Relationship.createQuery();
        query.whereEqualTo("objectId", objectId);
        query.findInBackground(new RelationshipFindCallback() {
            @Override
            protected void doneOnce(List<Relationship> objects, ParseException e) {
                if (objects != null) {
                    // Emulate the behavior of getFirstInBackground by using
                    // only the first result.
                    if (objects.size() < 1) {
                        callback.done(null, new ParseException(
                                ParseException.OBJECT_NOT_FOUND,
                                "No Relationship with id " + objectId + " was found."));
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
                                        final GetCallback<Relationship> callback) {
        ParseQuery<Relationship> talkQuery = ParseQuery.getQuery(Relationship.class);
        talkQuery.whereEqualTo("title", title);
        talkQuery.getFirstInBackground(new GetCallback<Relationship>() {

            @Override
            public void done(Relationship Relationship, ParseException e) {
                if (e == null) {
                    callback.done(Relationship, null);
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
        builder.path("Relationship/" + getObjectId());
        return builder.build();
    }*/

    public ParseUser getUser() {
        //((ParseUser) get("user1")).equals(ParseUser.getCurrentUser())
        if ( ((ParseUser) (get("user1"))).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            return (ParseUser) get("user2");
        }else{
            return (ParseUser) get("user1");
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