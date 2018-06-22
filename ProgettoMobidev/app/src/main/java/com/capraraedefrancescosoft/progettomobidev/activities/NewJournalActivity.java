package com.capraraedefrancescosoft.progettomobidev.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.capraraedefrancescosoft.progettomobidev.models.Journals;
import com.capraraedefrancescosoft.progettomobidev.R;
import com.capraraedefrancescosoft.progettomobidev.adapters.FacebookFriendsAdapter;
import com.capraraedefrancescosoft.progettomobidev.adapters.PlaceAutocompleteAdapter;
import com.capraraedefrancescosoft.progettomobidev.models.FacebookFriend;
import com.capraraedefrancescosoft.progettomobidev.models.Journal;
import com.capraraedefrancescosoft.progettomobidev.models.JournalID;
import com.capraraedefrancescosoft.progettomobidev.utilities.ListenerUtility;
import com.capraraedefrancescosoft.progettomobidev.widgets.CalendarView;
import com.capraraedefrancescosoft.progettomobidev.widgets.TextWithIcon;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewJournalActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String[] TRAVEL_TYPES = new String[]{"Car", "Train", "Plane", "Foot", "Boat", "Cruise"};

    private TextWithIcon journalName, journalDescription;
    private AutoCompleteTextView journalCity;
    private CalendarView journalDeparture, journalReturn;
    private ListView listaAmiciScelti;
    private Spinner journalType;
    private FacebookFriendsAdapter facebookFriendsAdapter;

    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mPlaceAutoCompleteAdapter;
    private AutocompleteFilter autocompleteFilter;
    private static final LatLngBounds BOUNDS_WORLD = new LatLngBounds(
            new LatLng(-180, 180), new LatLng(-180, 180));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_journal);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        findViews();
    }

    private void findViews() {
        this.journalName = (TextWithIcon) findViewById(R.id.newJournalName);
        this.journalDescription = (TextWithIcon) findViewById(R.id.newJournalDescription);
        this.journalType = (Spinner) findViewById(R.id.newJournalType);
        this.journalDeparture = (CalendarView) findViewById(R.id.newJournalDepartureDate);
        this.journalReturn = (CalendarView) findViewById(R.id.newJournalReturnDate);
        this.journalCity = (AutoCompleteTextView) findViewById(R.id.newJournalCity);

        this.journalName.requestFocus();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TRAVEL_TYPES);
        adapter.setDropDownViewResource(R.layout.spinner_text_item);
        journalType.setAdapter(adapter);

        // applico il filtro a solo le citta'
        this.autocompleteFilter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).build();
        this.journalCity.setOnItemClickListener(mAutocompleteClickListener);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API
        mPlaceAutoCompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_WORLD, autocompleteFilter);
        journalCity.setAdapter(mPlaceAutoCompleteAdapter);

        // prendo da facebook gli amici che puo' aggiungere
        this.listaAmiciScelti = (ListView) findViewById(R.id.listaAmiciScelti);
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        ArrayList<FacebookFriend> friends = getFriendsList(response);
                        facebookFriendsAdapter = new FacebookFriendsAdapter(getApplicationContext(), friends);
                        listaAmiciScelti.setAdapter(facebookFriendsAdapter);
                    }
                }
        ).executeAsync();
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
    public void onBackPressed() {
        if (!Journals.getInstance().getJournals().isEmpty()) {
            super.onBackPressed();
        } else {
            // chiusura applicazione
            ListenerUtility.showYesNoDialog(this, getString(R.string.exit), getString(R.string.exit_confirmation),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                // ritorno alla wall passandogli il nome del nuovo journal
                                Intent intent = new Intent();
                                setResult(RESULT_CANCELED, intent);
                                finish();
                            } else {
                                dialog.dismiss();
                            }
                        }
                    });
        }
    }

    // chiamato quando si preme sul pulsante Crea per finalizzare la creazione del journal
    public void createNewJournal(View view) throws ParseException {
        if (isNetworkAvailable()) {
            if (checkEmptyTextEdit(journalName, getResources().getString(R.string.error_journal_name))
                    && checkEmptyTextEdit(journalDescription, getResources().getString(R.string.error_journal_description))
                    && checkTextContainsSpecialChar(journalName, getResources().getString(R.string.error_special_char))
                    && checkEmptyCityJournal(journalCity, getResources().getString(R.string.error_journal_city))
                    && checkEmptyDate(journalDeparture, getResources().getString(R.string.error_journal_departure))
                    && checkEmptyDate(journalReturn, getResources().getString(R.string.error_journal_return))
                    && checkIfDateIsCorrect(journalDeparture.getDate(), journalReturn.getDate(), getResources().getString(R.string.error_date))
                    && checkIfNameJournalAlreadyExist(journalName, Profile.getCurrentProfile().getId(), getResources().getString(R.string.error_journal_already_exist))) {
                Journal newJournal = new Journal(journalName.getText().toString(), Long.valueOf(Profile.getCurrentProfile().getId()));
                newJournal.setDescription(journalDescription.getText().toString());
                newJournal.setDepartureDate(journalDeparture.getDate());
                newJournal.setReturnDate(journalReturn.getDate());
                newJournal.setCity(journalCity.getText().toString());
                newJournal.setType(journalType.getSelectedItem().toString());

                // aggiungo i partecipanti
                if (facebookFriendsAdapter != null)
                    newJournal.setParticipants(facebookFriendsAdapter.getListaSceltiID());

                newJournal.printAll();
                // aggiungo il nuovo journal nella lista dei journal
                JournalID newJournalID = Journals.getInstance().addJournal(newJournal);
                //sottoiscrizione al topic
                String topic = Profile.getCurrentProfile().getId() + "_" + journalName.getText().toString().replace(" ", "_");
                FirebaseMessaging.getInstance().subscribeToTopic(topic);
                System.out.println("Sottoscritto al topic " + topic);

                // ritorno alla wall passandogli l'id del nuovo journal
                Intent intent = new Intent();
                intent.putExtra(WallActivity.EXTRA_JOURNAL, newJournalID);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                System.out.println("Form error");
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_journal_form),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_connection),
                    Toast.LENGTH_LONG).show();
        }


    }

    protected ArrayList<FacebookFriend> getFriendsList(GraphResponse response) {
        ArrayList<FacebookFriend> listaAmici = new ArrayList<FacebookFriend>();
        try {
            JSONArray arrayJson = response.getJSONObject().getJSONArray("data");
            for (int i = 0; i < arrayJson.length(); i++) {
                JSONObject data = arrayJson.getJSONObject(i);
                String name = data.getString("name");
                String id = data.getString("id");
                listaAmici.add(new FacebookFriend(name, id));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listaAmici;
    }

    // controllo sui campi
    private boolean checkEmptyTextEdit(TextWithIcon textWithIcon, String error) {
        if (TextUtils.isEmpty(textWithIcon.getText().toString())) {
            textWithIcon.setError(error);
            return false;
        }
        return true;
    }

    // controllo sui campi
    private boolean checkTextContainsSpecialChar(TextWithIcon textWithIcon, String error) {
        Pattern p = Pattern.compile("[^A-Za-z0-9]");
        Matcher m = p.matcher(textWithIcon.getText().toString());
        boolean b = m.find();
        if (b) {
            textWithIcon.setError(error);
            return false;
        }
        return true;
    }

    // controllo sul luogo
    private boolean checkEmptyCityJournal(AutoCompleteTextView text, String error) {
        if (TextUtils.isEmpty(text.getText().toString())) {
            text.setError(error);
            return false;
        }
        return true;
    }

    // controllo sulle date
    private boolean checkEmptyDate(CalendarView calendarView, String error) {
        if (calendarView.getDate() == null) {
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    // controllo se la data di partenza è anteriore a quella di ritorno
    private boolean checkIfDateIsCorrect(Date depDate, Date retDate, String error) {
        if (depDate.after(retDate)) {
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean checkIfNameJournalAlreadyExist(TextWithIcon textWithIcon, String id, String error) {
        Journal journalTrovato = Journals.getInstance().getJournal(new JournalID(Long.parseLong(id), textWithIcon.getText()));

        if (journalTrovato != null) {
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            textWithIcon.setError(error);
            return false;
        }
        return true;
    }

    // verifica se la connessione è disponibile per poter aggiungere un journal
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void openAppInvite(View view) {

        String appLinkUrl, previewImageUrl;

        appLinkUrl = "https://www.travelnotes.com/travelnotes";
        previewImageUrl = "https://www.travelnotes.com/travel_notes.jpg";

        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();
            AppInviteDialog.show(this, content);
        }

    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mPlaceAutoCompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                System.out.println("Place query did not complete. Error: "
                        + places.getStatus().toString());
                places.release();
                return;
            }
            places.release();
        }
    };

    // connessione fallita a Google API Place
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("Impossibile connettersi.");
    }

}
