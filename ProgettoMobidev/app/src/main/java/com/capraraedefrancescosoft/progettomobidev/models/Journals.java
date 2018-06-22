package com.capraraedefrancescosoft.progettomobidev.models;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ianfire on 03/07/2016.
 */
public class Journals {

    private static Journals instance = new Journals();

    private Map<JournalID, Journal> journals;
    private JournalID currentJournalID;

    private Journals() {
        this.journals = new HashMap<>();
        this.currentJournalID = null;
    }

    public static Journals getInstance() {
        return instance;
    }

    public synchronized void setJournals(List<Journal> journals) {
        this.journals = new HashMap<>();
        // per ogni journal parte la sottoiscrizione al topic del journal
        // e viene aggiunto all'hashmap
        for (Journal journal : journals){
            this.journals.put(new JournalID(journal.getOwnerID(), journal.getName()), journal);

            String topic = journal.getOwnerID() + "_" + journal.getName().replace(" ", "_");
            FirebaseMessaging.getInstance().subscribeToTopic(topic);
            System.out.println("Sottoscritto al topic " + topic);
        }
    }

    public synchronized JournalID addJournal(Journal journal){
        JournalID id = new JournalID(journal.getOwnerID(), journal.getName());
        journals.put(id, journal);

        return id;
    }

    /** Ritorna il journal cercato */
    public synchronized Journal getJournal(JournalID id){
        return journals.get(id);
    }

    public List<Journal> getJournals(){
        return new ArrayList<Journal>(journals.values());
    }

    public synchronized void setCurrentJournal(JournalID id) {
        this.currentJournalID = id;
    }

    public synchronized Journal getCurrentJournal() {
        return journals.get(currentJournalID);
    }

    /** Rimpiazza il journal con quello nuovo fornito */
    public JournalID updateJournal(Journal newJournal) {
        JournalID newID = new JournalID(newJournal.getOwnerID(), newJournal.getName());
        Journal precedente = journals.put(newID, newJournal);

        if(precedente == null){
            Log.e("TravelNotes", "Errore: updateJournal non ha trovato il journal che doveva rimpiazzare");
        }

        return newID;
    }

}
