package com.capraraedefrancescosoft.progettomobidev.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.capraraedefrancescosoft.progettomobidev.models.Journals;
import com.capraraedefrancescosoft.progettomobidev.R;
import com.capraraedefrancescosoft.progettomobidev.models.Journal;
import com.capraraedefrancescosoft.progettomobidev.rest.AddTokenCallback;
import com.capraraedefrancescosoft.progettomobidev.rest.GetAllJournalsCallback;
import com.capraraedefrancescosoft.progettomobidev.rest.ServerCalls;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;


public class LoginActivity extends Activity {
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private static final String[] WRITE_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private static final String[] INTERNET_PERMS = {
            Manifest.permission.ACCESS_NETWORK_STATE,
    };

    private static final int INITIAL_REQUEST = 1337;

    private static int SPLASH_TIME_OUT = 1000;

    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;
    private int status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        status = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if(status != ConnectionResult.SUCCESS){
            // errore google play services
            new AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("ERROR WITH GOOGLE PLAY SERVICES, UPDATE VERSION TO 9.0.2")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(1);
                        }
                    }).show();
        } else {
            // richiede i permessi
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(WRITE_PERMS, INITIAL_REQUEST);
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(INTERNET_PERMS, INITIAL_REQUEST);
            }

            //inizializza l'SDK di Facebook prima di utilizzare altre operazioni correlate
            FacebookSdk.sdkInitialize(getApplicationContext());
            callbackManager = CallbackManager.Factory.create();

            //verifico la presenza di un Access Token (login Facebook)
            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                    updateWithToken(newAccessToken);
                }
            };

            accessToken = AccessToken.getCurrentAccessToken();
            updateWithToken(accessToken);

            // carico la libreria per comprire i video
            loadFFmpeg();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    /*
    Verifica se il token corrente è nullo o meno. Se è nullo inizializza la schermata di Login con Facebook
    altrimenti rimanda alla schermata principale dell'applicazione.
     */
    private void updateWithToken(AccessToken currentAccessToken) {
        if (currentAccessToken != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Profile profile = Profile.getCurrentProfile();
                    System.out.println("NOME: " + profile.getFirstName() + " COGNOME: " + profile.getLastName()
                            + " ID UTENTE FACEBOOK: " + profile.getId() + "\nURI User Picture: " + profile.getProfilePictureUri(100, 100));

                    //salvataggio token ID
                    sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken());
                    // schermata caricamento
                    setContentView(R.layout.loading_layout);
                    ((TextView)findViewById(R.id.textViewLoading)).setText(getString(R.string.loading));

                    // scarico tutti i journal perche' ora ho l'id utente
                    ServerCalls.downloadAllJournals(LoginActivity.this, new GetAllJournalsCallback() {
                        @Override
                        public void onJournalsLoaded(ArrayList<Journal> journals) {
                            // ha scaricato tutti i journals
                            Journals.getInstance().setJournals(journals);
                            System.out.println("Scaricati tutti i journal");

                            // avvio dell'activity wall e chiusura di questa activity
                            Intent intent = new Intent(LoginActivity.this, WallActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, Long.valueOf(Profile.getCurrentProfile().getId()));
                }
            }, SPLASH_TIME_OUT);
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    setContentView(R.layout.activity_login);
                    callbackManager = CallbackManager.Factory.create();
                    LoginButton loginButton = (LoginButton) findViewById(R.id.facebook_login_button);
                    loginButton.setReadPermissions("user_friends");
                    loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            System.out.println("Login Successful.");
                        }

                        @Override
                        public void onCancel() {
                            System.out.println("Cancel operation.");
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            System.out.println("Facebook Exception");
                            exception.printStackTrace();
                        }
                    });
                }
            }, SPLASH_TIME_OUT);
        }
    }


    /*
        Manda la registrazione del token al server
     */
    public void sendRegistrationToServer(String refreshedToken) {
        ServerCalls.addToken(new AddTokenCallback() {
            @Override
            public void tokenUploaded(boolean b) {

            }
        }, Profile.getCurrentProfile().getId(),refreshedToken);
    }

    /*
        Libreria per compressione video
     */
    private void loadFFmpeg() {
        try {
            FFmpeg.getInstance(getApplicationContext()).loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess() {
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_ffmpeg), Toast.LENGTH_LONG);
        }
    }

}


