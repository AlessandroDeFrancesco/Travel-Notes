package com.capraraedefrancescosoft.progettomobidev.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.capraraedefrancescosoft.progettomobidev.models.Journals;
import com.capraraedefrancescosoft.progettomobidev.R;
import com.capraraedefrancescosoft.progettomobidev.adapters.FacebookFriendAlreadyAddedAdapter;
import com.capraraedefrancescosoft.progettomobidev.adapters.FacebookFriendsAdapter;
import com.capraraedefrancescosoft.progettomobidev.models.FacebookFriend;
import com.capraraedefrancescosoft.progettomobidev.models.Journal;
import com.capraraedefrancescosoft.progettomobidev.models.JournalID;
import com.capraraedefrancescosoft.progettomobidev.utilities.FacebookUtility;
import com.capraraedefrancescosoft.progettomobidev.widgets.CalendarView;
import com.capraraedefrancescosoft.progettomobidev.widgets.TextWithIcon;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InformationJournalActivity extends AppCompatActivity {

    private TextWithIcon journalName;
    private TextWithIcon journalDescription;
    private TextWithIcon journalCity;
    private TextWithIcon journalType;

    private CalendarView journalDeparture;
    private CalendarView journalReturn;

    private Journal journal;
    private CardView cardView;
    private Button buttonUpdate;
    private ListView listViewAmiciViaggioAttuali;
    private ListView listViewNuoviAmici;

    private FacebookFriendAlreadyAddedAdapter facebookJournalFriendsAdapter;
    private FacebookFriendsAdapter facebookNewFriendsAdapter;

    private List<Long> journal_participants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_journal);

        this.journalName = (TextWithIcon) findViewById(R.id.infoJournalName);
        this.journalDescription = (TextWithIcon) findViewById(R.id.infoJournalDescription);
        this.journalDeparture = (CalendarView) findViewById(R.id.infoJournalDeparture);
        this.journalReturn = (CalendarView) findViewById(R.id.infoJournalReturn);
        this.journalCity = (TextWithIcon) findViewById(R.id.infoJournalCity);
        this.journalType = (TextWithIcon) findViewById(R.id.infoJournalType);
        this.cardView = (CardView) findViewById(R.id.cardViewNewFriends);
        this.buttonUpdate = (Button) findViewById(R.id.updateJournalButton);
        this.listViewAmiciViaggioAttuali = (ListView) findViewById(R.id.listaAmiciScelti);
        this.listViewNuoviAmici = (ListView) findViewById(R.id.listaNuoviAmici);

        journal = Journals.getInstance().getCurrentJournal();

        setJournalInformationOnViews();
    }

    public void setJournalInformationOnViews() {
        this.journalName.setText(journal.getName());
        this.journalDescription.setText(journal.getDescription());
        this.journalDeparture.setDate(journal.getDepartureDate());
        this.journalReturn.setDate(journal.getReturnDate());
        this.journalCity.setText(journal.getCity());
        this.journalType.setText(journal.getType());

        this.journal_participants = new ArrayList<>(journal.getParticipants());
        this.journal_participants.add(journal.getOwnerID());

        // ottiene la lista dei partecipanti del journal
        getFriendsListAlreadyAdded();

        // verifica se è stato l'owner id ad aprire il journal; nel caso il sistema da la possibita
        // di aggiungere partecipanti
        if (!journal.getOwnerID().toString().equals(Profile.getCurrentProfile().getId())) {
            this.cardView.setVisibility(View.GONE);
            this.buttonUpdate.setVisibility(View.GONE);
        } else {
            getNewFriendsList();
        }

    }

    // riempie la lista degli utenti già presenti al journal
    protected void getFriendsListAlreadyAdded() {
        facebookJournalFriendsAdapter = new FacebookFriendAlreadyAddedAdapter(this.getApplicationContext(), new ArrayList<FacebookFriend>());
        listViewAmiciViaggioAttuali.setAdapter(facebookJournalFriendsAdapter);

        for (int i = 0; i < journal_participants.size(); i++) {
            final long id = journal_participants.get(i);
            FacebookUtility.getInstance().getNameFromId(id, new FacebookUtility.GetFacebookNameCallback() {
                @Override
                public void nameRetrieved(String name) {
                    facebookJournalFriendsAdapter.addFriend(new FacebookFriend(name, "" + id));
                }
            });
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

    // restituisce la lista degli utenti che possono essere aggiunti al journal
    protected void getNewFriendsList() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(), "/me/friends", null, HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        ArrayList<FacebookFriend> PossibiliPartecipanti = new ArrayList<FacebookFriend>();
                        try {
                            JSONArray arrayJson = response.getJSONObject().getJSONArray("data");
                            for (int i = 0; i < arrayJson.length(); i++) {
                                JSONObject data = arrayJson.getJSONObject(i);
                                String name = data.getString("name");
                                String id = data.getString("id");
                                if (!journal_participants.contains(Long.valueOf(id))) {
                                    PossibiliPartecipanti.add(new FacebookFriend(name, id));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        facebookNewFriendsAdapter = new FacebookFriendsAdapter(getApplicationContext(), PossibiliPartecipanti);
                        listViewNuoviAmici.setAdapter(facebookNewFriendsAdapter);
                    }
                }
        ).executeAsync();
    }

    public void updateJournal(View v) {
        if (isNetworkAvailable()) {
            System.out.println("Aggiornamento journal " + journal.getName());

            // aggiungo i partecipanti
            if (facebookNewFriendsAdapter != null) {
                for (Long new_participants : facebookNewFriendsAdapter.getListaSceltiID())
                    journal.addParticipant(new_participants);
            }

            JournalID updatedJournalID = Journals.getInstance().updateJournal(journal);

            // ritorno alla wall passandogli il nome del journal aggiornato
            Intent intent = new Intent();
            intent.putExtra(WallActivity.EXTRA_JOURNAL, updatedJournalID);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_connection), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // click su "Invite Facebook Friends"
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

    // verifica se la connessione è disponibile per poter aggiornare il journal
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
