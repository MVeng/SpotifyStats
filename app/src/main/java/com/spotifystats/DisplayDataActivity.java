package com.spotifystats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DisplayDataActivity extends AppCompatActivity {


    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;
    private String mAccessToken;

    //Request params
    private final String[] time_range = {"short_term","medium_term","long_term"};
    private int limit = 10;
    private int index = 0;


    //DATA
    private static JSONObject data;
    private static ArrayList<Artist> artistsList;
    private static ArrayList<Track> tracksList;
    private String type;
    ViewPager viewPager;
    ListView artistListView;
    ListView trackListView;

    Request request;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        actionBar = getSupportActionBar();


        viewPager = findViewById(R.id.viewpager);
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());


        viewPager.setAdapter(pagerAdapter);
        //viewPager.setCurrentItem(1);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //setContentView(R.layout.activity_user_top_artists);
        mAccessToken = getIntent().getStringExtra("ACCESS_TOKEN");

    }



    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void makeToast(String s){
        Toast toast = Toast.makeText(this,s,Toast.LENGTH_SHORT);
        toast.show();
    }


    /**
     * Set limit on amount of data request.
     * @param limit
     */
    public void setLimit(int limit){
        this.limit = limit;
    }


    /**
     * Generate request from the custom parameters
     * @param type
     */
    protected void generateRequest(String type){
        System.out.println("Genereate " + type);
        this.type = type;
        String url = "https://api.spotify.com/v1/me/top/"+ type + "?time_range=" + time_range[index] +"&limit=" + limit;
        this.request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer " + mAccessToken)
                .build();

        callRequestResponse(request);
    }


    /**
     * Send API request and grab response
     * @param request
     */
    private void callRequestResponse(Request request){
        cancelCall();

        mCall = mOkHttpClient.newCall(request);

        final JSONObject[] jsonObject = {null};

        mCall.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if(response.isSuccessful()) {
                    final String response_data = response.body().string();
                    try {
                        jsonObject[0] = new JSONObject(response_data);
                        setData(jsonObject[0]);
                        //Check if request was an artist or track type
                        if(type.equals("artists")) {

                            parseUserTopArtists();
                            System.out.println("PARSE ARTISTS");

                        }else{
                            parseUserTopTracks();
                            System.out.println("PARSE TRACKS");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setData(JSONObject jsonObject){
        this.data = jsonObject;
    }

    /**
     * Create a track list data received from Spotify
     * @throws JSONException
     */
    private void parseUserTopArtists() throws JSONException, IOException {
        artistsList = new ArrayList<>();
        JSONArray items = data.getJSONArray("items");

        for(int i=0; i < items.length(); i++){
            JSONObject item = items.getJSONObject(i);
            Artist artist = new Artist(item);
            artistsList.add(artist);

        }
        for (Artist t: artistsList) {
            System.out.println("Name: " + t.getName());

        }
        updateArtists();
    }

    /**
     * Parse the user top tracks request and store them to tracks list
     * @throws JSONException
     * @throws IOException
     */
    private void parseUserTopTracks() throws JSONException, IOException {
        tracksList = new ArrayList();
        JSONArray items = data.getJSONArray("items");

        for(int i=0; i < items.length(); i++){
            JSONObject item = items.getJSONObject(i);
            com.spotifystats.Track track = new com.spotifystats.Track(item);
            tracksList.add(track);
        }
//        for (Track t: tracksList) {
//            System.out.println("Name: " + t.getName());
//
//        }
        updateTracks();
    }

    /**
     * Update the artist list UI
     */
    private void updateArtists(){

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                actionBar.setDisplayHomeAsUpEnabled(true);
                artistListView = findViewById(R.id.listview1);
                CustomListview customListview = new CustomListview(DisplayDataActivity.this,R.layout.fragment_top_artists, artistsList);
                artistListView.setAdapter(customListview);

            }
        });
    }

    /**
     * Update the track list UI
     */
    private void updateTracks(){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                trackListView = findViewById(R.id.listview2);
                CustomListview customListview = new CustomListview(DisplayDataActivity.this, R.layout.fragment_top_tracks,tracksList);
                trackListView.setAdapter(customListview);

            }
        });
    }


    public void setIndex(int index) {
        this.index = index;
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }


}