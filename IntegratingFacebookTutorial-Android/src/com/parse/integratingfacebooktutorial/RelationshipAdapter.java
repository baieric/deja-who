package com.parse.integratingfacebooktutorial;

/**
 * Created by Eric on 2014-09-25.
 */

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.parse.ParseImageView;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class RelationshipAdapter extends ArrayAdapter<Relationship> {
    private boolean isFavoritesView = false;
    private LayoutInflater inflater;

    public RelationshipAdapter(Context context, boolean isFavorites) {
        super(context, 0);
        isFavoritesView = isFavorites;
        inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        // If a view hasn't been provided inflate on
        if (view == null) {
            view = inflater.inflate(R.layout.relationshiplist_item, parent, false);
            // Cache view components into the view holder
            holder = new ViewHolder();
            holder.nameView = (TextView) view.findViewById(R.id.name_view);
            holder.dateView = (TextView) view.findViewById(R.id.date_view);
            holder.numEncountersView = (TextView) view.findViewById(R.id.numEncounters_view);
            holder.userProfilePictureView = (ProfilePictureView) view.findViewById(R.id.userProfilePicture);
            /*holder.titleView = (TextView) view.findViewById(R.id.title);
            holder.speakerName = (TextView) view
                    .findViewById(R.id.speaker_name);
            holder.photo = (ParseImageView) view
                    .findViewById(R.id.speaker_photo);
            holder.favoriteButton = (ImageButton) view
                    .findViewById(R.id.favorite_button);*/
            // Tag for lookup later
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final Relationship Relationship = getItem(position);

        /*if (isFavoritesView) {
            LinearLayout talkLayout = holder.talkLayout;

            int displayColor = talk.getRoom().getColor();
            talkLayout.setBackgroundColor(displayColor);
        }*/

        ParseUser user = Relationship.getUser();
        SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy h:mm a", Locale.US);
        TextView nameView = holder.nameView;
        TextView dateView = holder.dateView;
        TextView numEncountersView = holder.numEncountersView;
        dateView.setText("Last Passed: " + df.format(Relationship.get("lastMetAt")));
        int numEncounters = Relationship.getInt("numEncounters");
        if(numEncounters == 1){
        	numEncountersView.setText("Passed 1 time");
        }else{
        	numEncountersView.setText("Passed " + numEncounters + " times");
        }
        
        ProfilePictureView pictureView = holder.userProfilePictureView;

        if (user.get("profile") != null) {
            JSONObject userProfile = user.getJSONObject("profile");
            try {
                if (userProfile.getString("facebookId") != null) {
                    String facebookId = userProfile.get("facebookId")
                            .toString();
                    pictureView.setProfileId(facebookId);
                } else {
                    // Show the default, blank user profile picture
                    pictureView.setProfileId(null);
                }
                if (userProfile.getString("firstName") != null) {
                    nameView.setText(userProfile.getString("firstName"));
                } else {
                    nameView.setText("Anonymous");
                }
            } catch (JSONException e) {
                Log.d(IntegratingFacebookTutorialApplication.TAG,
                        "Error parsing saved user data.");
            }

        }

        /*TextView titleView = holder.titleView;
        TextView speakerName = holder.speakerName;
        titleView.setText(talk.getTitle());

        List<Speaker> speakers = talk.getSpeakers();

        final ParseImageView photo = holder.photo;

        if (!speakers.isEmpty()) {
            final Speaker primarySpeaker = speakers.get(0);
            speakerName.setText(primarySpeaker.getName());
            photo.setParseFile(primarySpeaker.getPhoto());
            photo.loadInBackground();
        }

        if (talk.isAlwaysFavorite()) {
            photo.setParseFile(talk.getIcon());
            photo.loadInBackground();
        }

        final ImageButton favoriteButton = holder.favoriteButton;
        if (Favorites.get().contains(talk)) {
            if (isFavoritesView) {
                favoriteButton.setImageResource(R.drawable.x);
            } else {
                favoriteButton
                        .setImageResource(R.drawable.light_rating_important);
            }
        } else {
            favoriteButton
                    .setImageResource(R.drawable.light_rating_not_important);
        }
        favoriteButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Favorites favorites = Favorites.get();
                if (favorites.contains(talk)) {
                    favorites.remove(talk);
                    favoriteButton
                            .setImageResource(R.drawable.light_rating_not_important);
                } else {
                    favorites.add(talk);
                    if (isFavoritesView) {
                        favoriteButton.setImageResource(R.drawable.x);
                    } else {
                        favoriteButton
                                .setImageResource(R.drawable.light_rating_important);
                    }
                }
                favorites.save(getContext());
            }
        });
        favoriteButton.setFocusable(false);

        if (talk.isAlwaysFavorite()) {
            favoriteButton.setVisibility(View.GONE);
            photo.setBackgroundResource(android.R.color.transparent);
        } else if (talk.isBreak()) {
            favoriteButton.setVisibility(View.GONE);
            photo.setVisibility(View.INVISIBLE);
        } else {
            favoriteButton.setVisibility(View.VISIBLE);
        }*/

        return view;
    }

    private static class ViewHolder {
        LinearLayout talkLayout;
        TextView nameView;
        TextView dateView;
        TextView numEncountersView;
        ProfilePictureView userProfilePictureView;
        /*TextView titleView;
        TextView speakerName;
        ParseImageView photo;
        ImageButton favoriteButton;*/
    }
}
