package com.capraraedefrancescosoft.progettomobidev.rest;


import com.capraraedefrancescosoft.progettomobidev.models.Element;

/**
 * Created by Ianfire on 13/09/2016.
 */
public interface GetElementCallback {
    /** Chiamata sull'UI thread */
    void onElementLoaded(Element element);
}
