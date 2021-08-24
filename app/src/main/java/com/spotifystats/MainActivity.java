package com.spotifystats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;


import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;

import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp;
import com.spotify.android.appremote.api.error.NotLoggedInException;
import com.spotify.android.appremote.api.error.UserNotAuthorizedException;


import com.spotify.protocol.types.Image;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {


    private String TAG = MainActivity.class.getName();
    private static final String CLIENT_ID = "768adbd969cd44668adc38cdc95360ea";
    private static final String REDIRECT_URI = "my-spotify-stats://callback";
    private static SpotifyAppRemote mSpotifyAppRemote;
    private static String mAccessToken;


    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;

    Button connectButton , displayDataButton;
    ImageView playButton, skipNextButton, skipPrevButton, currentTrackIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectButton = findViewById(R.id.connectButton);
        displayDataButton = findViewById(R.id.displayDataButton);

        currentTrackIcon = findViewById(R.id.currentTrackIcon);

        playButton = findViewById(R.id.playIcon);
        skipNextButton = findViewById(R.id.skipNextIcon);
        skipPrevButton = findViewById(R.id.skipPrevIcon);
        onDisconnect();
        connect();
    }


    @Override
    protected void onStop() {
        super.onStop();
        //SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void onDisconnect(){
        connectButton.setEnabled(true);
        playButton.setEnabled(false);
        skipNextButton.setEnabled(false);
        skipPrevButton.setEnabled(false);
        displayDataButton.setEnabled(false);
    }


    private void onConnect(){
        connectButton.setEnabled(false);
        playButton.setEnabled(true);
        skipNextButton.setEnabled(true);
        skipPrevButton.setEnabled(true);
        displayDataButton.setEnabled(true);
        subscribeToPlayerState();
    }

    private void connect(){
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

        SpotifyAppRemote.connect(
                getApplication(),
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build(),
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        requestToken();

                        onConnect();
                        Log.d(TAG,"SUCCESS");

                    }

                    @Override
                    public void onFailure(Throwable error) {
                        Log.d(TAG,"ERROR");
                        if (error instanceof NotLoggedInException || error instanceof UserNotAuthorizedException) {
                            // Show login button and trigger the login flow from auth library when clicked
                            System.out.println("NEED TO LOGIN");
                        } else if (error instanceof CouldNotFindSpotifyApp) {
                            // Show button to download Spotify
                            System.out.println("CANNT FIND SPOTIFY");
                        }else{
                            System.out.println(error);
                        }
                    }
                });


    }

    /**
     * Remote app connection
     * @param view
     */
    public void connectButton(View view) {
        connect();
    }

    /**
     * Play/Pause button
     */
    public void onPlayPauseButtonClicked(View view) {
        System.out.println("WORK");
        // Play a playlist
        mSpotifyAppRemote
                .getPlayerApi()
                .getPlayerState()
                .setResultCallback(
                        playerState -> {
                            if (playerState.isPaused) {
                                mSpotifyAppRemote
                                        .getPlayerApi()
                                        .resume();
                                playButton.setImageResource(R.drawable.ic_pause);
                                System.out.println("PLAY");
                            } else {
                                mSpotifyAppRemote
                                        .getPlayerApi()
                                        .pause();
                                playButton.setImageResource(R.drawable.ic_play);
                                System.out.println("PAUSE");
                            }
                        });
    }


    private void subscribeToPlayerState(){
        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    mSpotifyAppRemote
                            .getImagesApi()
                            .getImage(playerState.track.imageUri, Image.Dimension.LARGE)
                            .setResultCallback(
                                    bitmap -> {
                                        currentTrackIcon.setImageBitmap(bitmap);

                                    });

                });


    }

    private Bitmap getImage(URL url) throws IOException {
        return BitmapFactory.decodeStream(url.openConnection().getInputStream());
    }

    public void onSkipNextButtonClicked(View view) {
        //Skip to next
        mSpotifyAppRemote
                .getPlayerApi()
                .skipNext();
    }

    public void onSkipPreviousButtonClicked(View view) {
        //Skip to previous
        mSpotifyAppRemote
                .getPlayerApi()
                .skipPrevious();
    }



    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, REDIRECT_URI)
                //.setShowDialog(false)
                .setScopes(new String[]{"user-top-read",
                        "user-read-recently-played",
                        "user-read-playback-position","user-read-private",
                        "user-read-email", "playlist-read-private",
                        "user-library-read", "user-library-modify",
                        "user-top-read","playlist-read-collaborative",
                        "playlist-modify-public","playlist-modify-private",
                        "ugc-image-upload","user-follow-read",
                        "user-follow-modify","user-read-playback-state",
                        "user-modify-playback-state","user-read-currently-playing"})
                //.setCampaign("your-campaign-token")
                .build();
    }

    private void requestToken(){
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (requestCode == AUTH_TOKEN_REQUEST_CODE) {
            mAccessToken = response.getAccessToken();

        }
    }

    public void onDisplayDataClicked(View view) {
        Intent intent = new Intent(getBaseContext(), DisplayDataActivity.class);
        intent.putExtra("ACCESS_TOKEN", mAccessToken);
        startActivity(intent);
    }


//    private Request generateRequest(String type, int limit, int offset){
//
//        String url = "https://api.spotify.com/v1/me";
//        if(type != null){
//            url += type;
//        }
//        url += "?limit=" + limit + "&offset=" +offset;
//        System.out.println(url);
//        System.out.println(mAccessToken);
//        return new Request.Builder()
//                .url(url)
//                .addHeader("Authorization","Bearer " + mAccessToken)
//                .build();
//
//
//    }
//
//    public void onTopArtistClicked(View view) {
//
//        Request request = generateRequest("/top/artists", limit, offset);
//        callRequestResponse(request);
//
//    }
//






}