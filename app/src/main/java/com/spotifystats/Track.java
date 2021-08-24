package com.spotifystats;

import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class Track extends SpotifyData{


    public Track(JSONObject jsonObject) throws IOException, JSONException {
        super(jsonObject);
    }

    @Override
    public void setImage() throws JSONException, IOException {
        JSONObject album = jsonObject.getJSONObject("album");
        JSONArray images = album.getJSONArray("images");
        JSONObject img = images.getJSONObject(0);
        String imageUrl = img.getString("url");
        image = BitmapFactory.decodeStream(new URL(imageUrl).openConnection().getInputStream());

    }
}
