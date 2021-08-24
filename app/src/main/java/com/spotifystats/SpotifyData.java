package com.spotifystats;

import android.graphics.Bitmap;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public abstract class SpotifyData {
    protected JSONObject jsonObject;
    protected String name = "Unknown";
    protected Bitmap image;
    protected Uri uri;

    public SpotifyData(JSONObject jsonObject) throws IOException, JSONException {
        this.jsonObject = jsonObject;
        this.name = jsonObject.getString("name");
        this.uri = Uri.parse(jsonObject.getString("uri"));
        setImage();
    }
    public abstract void setImage() throws JSONException, IOException;

    public Bitmap getImage(){
        return image;
    }
    public Uri getUri(){
        return uri;
    }
    public String getName(){
        return name;
    }
}
