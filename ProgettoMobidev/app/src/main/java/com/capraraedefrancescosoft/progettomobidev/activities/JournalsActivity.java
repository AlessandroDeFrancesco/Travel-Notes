package com.capraraedefrancescosoft.progettomobidev.activities;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.capraraedefrancescosoft.progettomobidev.models.Journals;
import com.capraraedefrancescosoft.progettomobidev.R;
import com.capraraedefrancescosoft.progettomobidev.adapters.JournalsAdapter;
import com.capraraedefrancescosoft.progettomobidev.models.Journal;
import com.capraraedefrancescosoft.progettomobidev.models.JournalID;
import com.capraraedefrancescosoft.progettomobidev.rest.GetAllJournalsCallback;
import com.capraraedefrancescosoft.progettomobidev.rest.ServerCalls;
import com.facebook.Profile;

import java.util.ArrayList;

public class JournalsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView elementsList;
    private JournalsAdapter journalsAdapter;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journals);

        journalsAdapter = new JournalsAdapter(this);
        elementsList = (ListView) findViewById(R.id.journals_container);
        elementsList.setAdapter(journalsAdapter);
        elementsList.setOnItemClickListener(this);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayoutJournals);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadAllJournals();
                refreshLayout.setRefreshing(false);
            }
        });

        downloadAllJournals();
    }

    private void downloadAllJournals() {
        // scarico tutti i journal
        ServerCalls.downloadAllJournals(JournalsActivity.this, new GetAllJournalsCallback() {
            @Override
            public void onJournalsLoaded(ArrayList<Journal> journals) {
                // ha scaricato tutti i journals
                Journals.getInstance().setJournals(journals);
                journalsAdapter.notifyDataSetChanged();
                System.out.println("scaricati tutti i journal");
            }
        }, Long.valueOf(Profile.getCurrentProfile().getId()));
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Journal journal = journalsAdapter.getItem(position);
        Log.d("Journal clicked", "Journal name: " + journal.getName());
        if (journal != null) {
            Intent intent = new Intent();
            intent.putExtra(WallActivity.EXTRA_JOURNAL, new JournalID(journal.getOwnerID(), journal.getName()));
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
