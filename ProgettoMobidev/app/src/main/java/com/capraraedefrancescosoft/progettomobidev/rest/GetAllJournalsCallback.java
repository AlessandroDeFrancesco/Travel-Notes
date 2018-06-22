package com.capraraedefrancescosoft.progettomobidev.rest;

import com.capraraedefrancescosoft.progettomobidev.models.Journal;

import java.util.ArrayList;

/**
 * Created by Ianfire on 13/09/2016.
 */
public interface GetAllJournalsCallback {
    /** Chiamata sull'UI thread */
    void onJournalsLoaded(ArrayList<Journal> journals);
}
