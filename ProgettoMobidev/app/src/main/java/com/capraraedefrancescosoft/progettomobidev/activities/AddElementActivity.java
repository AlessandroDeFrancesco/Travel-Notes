package com.capraraedefrancescosoft.progettomobidev.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.capraraedefrancescosoft.progettomobidev.R;
import com.capraraedefrancescosoft.progettomobidev.models.Element;
import com.capraraedefrancescosoft.progettomobidev.models.ElementType;
import com.capraraedefrancescosoft.progettomobidev.utilities.ContentTypesUtility;
import com.capraraedefrancescosoft.progettomobidev.utilities.ListenerUtility;
import com.capraraedefrancescosoft.progettomobidev.widgets.ElementView;
import com.facebook.Profile;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;

import java.util.Date;

public class AddElementActivity extends AppCompatActivity implements LocationListener {

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final int INITIAL_REQUEST = 1337;

    private static final int PICK_IMAGE = 1564;
    private static final int PICK_VIDEO = 1484;

    private ElementType type;
    private Element element;

    private ElementView elementView;

    private Uri dataUri;

    private LocationManager locationManager;

    private Location currentLocation;

    private ProgressBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_element);

        // prendo il tipo di elemento
        type = (ElementType) getIntent().getSerializableExtra(WallActivity.EXTRA_TYPE_KEY);

        // creo l'elemento
        Profile profile = Profile.getCurrentProfile();
        element = new Element(type);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }

        //location Manager per geolocalizzazione
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        element.setOwnerName(profile.getName());
        element.setDate(new Date());

        elementView = (ElementView) findViewById(R.id.elementView);
        elementView.populateViewWithElement(element, true);
        elementView.disablePlaceButton();

        // richiedo di scegliere un immagine/video
        switch (type) {
            case IMAGE:
                if (getIntent().getSerializableExtra(WallActivity.EXTRA_ACTION) != null) {
                    Intent intentImg = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intentImg.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intentImg, PICK_IMAGE);
                    }
                } else {
                    Intent intentImg = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intentImg.addCategory(Intent.CATEGORY_OPENABLE);
                    intentImg.setType("image/*");
                    startActivityForResult(intentImg, PICK_IMAGE);
                }
                break;
            case VIDEO:
                if (getIntent().getSerializableExtra(WallActivity.EXTRA_ACTION) != null) {
                    Intent intentVid = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    if (intentVid.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intentVid, PICK_VIDEO);
                    }
                } else {
                    Intent intentVid = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intentVid.addCategory(Intent.CATEGORY_OPENABLE);
                    intentVid.setType("video/mp4");
                    startActivityForResult(intentVid, PICK_VIDEO);
                }
                break;
        }

        // setto la posizione sull'ultima conosciuta, in attesa di quella precisa
        setLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean dataIsRight = false;

        if (data != null && resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                if (ContentTypesUtility.checkIsImage(data.getData(), this.getApplicationContext())) {
                    dataIsRight = true;
                    // salvo l'uri dell'immagine o del video nell'element
                    elementView.setContent(data.getData().toString());
                    dataUri = data.getData();
                }
            } else if (requestCode == PICK_VIDEO) {
                if (ContentTypesUtility.checkIsVideo(data.getData(), this.getApplicationContext())) {
                    dataIsRight = true;
                    // salvo l'uri dell'immagine o del video nell'element
                    elementView.setContent(data.getData().toString());
                    dataUri = data.getData();
                }
            }
        }

        if (!dataIsRight) {
            Toast.makeText(this, "Error retrieving data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setLocation(Location location) {
        if (location != null) {
            float latitude = (float) location.getLatitude();
            float longitude = (float) location.getLongitude();
            element.setLatitude(latitude);
            element.setLongitude(longitude);

            elementView.setLocation(location);
            currentLocation = location;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // cosi il tasto indietro dell'action bar funziona come il back del tasto fisico
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onBackPressed() {
        ListenerUtility.getAbortAddElementDialogConfirmation(this).show();
        FFmpeg.getInstance(getApplicationContext()).killRunningProcesses();
    }

    // quando si preme per aggiungere alla wall, comprimo l'immagine o il video e lo salvo in cache
    public void AddElementToWall(View view) {
        if (isNetworkAvailable()) {
            if (checkLocation())
                publishElementToWall();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_connection), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void publishElementToWall() {
        switch (type) {
            case NOTE:
                String text = elementView.getText();
                if (TextUtils.isEmpty(text)) {
                    elementView.setError(getResources().getString(R.string.error_note));
                } else {
                    element.setContent(text);
                    returnToWallActivity();
                }
                break;
            case IMAGE:
                ContentTypesUtility.compressAndCacheImage(dataUri, getApplicationContext(), new ContentTypesUtility.CacheLoading() {
                    @Override
                    public void onCachingComplete(Uri uri) {
                        element.setContent(uri.toString());
                        element.setId(ContentTypesUtility.getHashFromUri(uri, getContentResolver()));
                        returnToWallActivity();
                    }

                    @Override
                    public void onCacheUpdate(int percentuale) {
                    }

                    @Override
                    public void onCacheFailed() {
                        Toast.makeText(getApplicationContext(), "Errore nella compressione e caching immagine", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                showLoading(true);
                break;
            case VIDEO:
                ContentTypesUtility.compressAndCacheVideo(dataUri, getApplicationContext(), new ContentTypesUtility.CacheLoading() {
                    @Override
                    public void onCachingComplete(Uri uri) {
                        element.setContent(uri.toString());
                        element.setId(ContentTypesUtility.getHashFromUri(uri, getContentResolver()));
                        returnToWallActivity();
                    }

                    @Override
                    public void onCacheUpdate(int percentuale) {
                        loadingBar.setProgress(percentuale);
                        System.out.println(percentuale + "% di compressione");
                    }

                    @Override
                    public void onCacheFailed() {
                        Toast.makeText(getApplicationContext(), "Errore nella compressione e caching video", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                showLoading(false);
                break;
        }
    }

    private boolean checkLocation() {
        String testoErrore = "";

        if (currentLocation == null) {
            testoErrore = getString(R.string.error_location_not_found);
        } else if (currentLocation.getAccuracy() > 500) {
            // accuracy bassa
            testoErrore = getString(R.string.error_location_accuracy);
        } else if (new Date().getTime() - currentLocation.getTime() > 10 * 60 * 1000) {
            // passati piu' di 10 minuti
            testoErrore = getString(R.string.error_location_time);
        }

        // mostro il dialog
        if (!testoErrore.isEmpty()) {
            // location non trovata ancora
            ListenerUtility.showYesNoDialog(this, getString(R.string.error), testoErrore,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                publishElementToWall();
                            } else {
                                dialog.dismiss();
                            }
                        }
                    });
        }

        if (testoErrore.isEmpty())
            return true;
        else
            return false;
    }

    private void showLoading(boolean indeterminate) {
        setContentView(R.layout.loading_layout);
        loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
        loadingBar.setMax(100);
        loadingBar.setIndeterminate(indeterminate);
    }


    public void returnToWallActivity() {
        Intent result = new Intent();
        result.putExtra(WallActivity.EXTRA_ELEMENT, element);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        setLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getBaseContext(), "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Gps is turned off!! ",
                Toast.LENGTH_SHORT).show();
    }

    // verifica se la connessione è disponibile per poter aggiungere un element
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
