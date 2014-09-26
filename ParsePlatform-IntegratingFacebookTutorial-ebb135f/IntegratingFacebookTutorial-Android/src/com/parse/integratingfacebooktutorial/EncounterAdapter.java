package com.parse.integratingfacebooktutorial;

/**
 * Created by Eric on 2014-09-25.
 */

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseImageView;

public class EncounterAdapter extends ArrayAdapter<Encounter> {
    private boolean isFavoritesView = false;
    private LayoutInflater inflater;

    public EncounterAdapter(Context context, boolean isFavorites) {
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
            view = inflater.inflate(R.layout.encounterlist_item, parent, false);
            // Cache view components into the view holder
            holder = new ViewHolder();
            holder.nameView = (TextView) view.findViewById(R.id.name_view);
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

        final Encounter encounter = getItem(position);

        /*if (isFavoritesView) {
            LinearLayout talkLayout = holder.talkLayout;

            int displayColor = talk.getRoom().getColor();
            talkLayout.setBackgroundColor(displayColor);
        }*/

        TextView nameView = holder.nameView;
        nameView.setText(encounter.getUser().getUsername());

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
        /*TextView titleView;
        TextView speakerName;
        ParseImageView photo;
        ImageButton favoriteButton;*/
    }
}
