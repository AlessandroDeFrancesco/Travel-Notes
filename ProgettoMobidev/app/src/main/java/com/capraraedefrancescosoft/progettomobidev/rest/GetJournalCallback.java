package com.capraraedefrancescosoft.progettomobidev.rest;

import com.capraraedefrancescosoft.progettomobidev.models.Journal;

/**
 * Created by Ianfire on 29/08/2016.
 */
public interface GetJournalCallback {
    /** Chiamata sull'UI thread */
    void onJournalLoaded(Journal journal);
}
