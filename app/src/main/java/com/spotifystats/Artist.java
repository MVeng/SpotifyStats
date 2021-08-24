package com.spotifystats;

import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class Artist extends SpotifyData{



    public Artist(JSONObject jsonObject) throws JSONException, IOException {
        super(jsonObject);
    }

    @Override
    public void setImage() throws JSONException, IOException {
        JSONArray images = jsonObject.getJSONArray("images");
        JSONObject img = images.getJSONObject(0);
        String imageUrl = img.getString("url");
        image = BitmapFactory.decodeStream(new URL(imageUrl).openConnection().getInputStream());

    }


    //Get Tracks from top recent track
//    private void parseTrack() throws JSONException {
//
//        JSONObject track = jsonObject.getJSONObject("track");
//        JSONArray artists = track.getJSONArray("artists");
//
//        //Get all artists name
//        for(int i=0; i < artists.length(); i++){
//            JSONObject artist = artists.getJSONObject(i);
//            String artistName = artist.getString("name");
//            artistsName.add(artistName);
//        }
//
//        String trackName = track.getString("name");
//        this.name = trackName;
//    }


}
