package com.capraraedefrancescosoft.progettomobidev.rest;

import com.capraraedefrancescosoft.progettomobidev.models.Element;
import com.capraraedefrancescosoft.progettomobidev.models.Journal;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServerApi {
    @GET("/resources/journal/{owner_id}/{journal_name}")
    Call<Journal> loadJournal(@Path("owner_id") String ownerID, @Path("journal_name") String journalName);
    @GET("/resources/journal/{owner_id}")
    Call<ArrayList<Journal>> loadAllJournals(@Path("owner_id") String ownerID);
    @GET("/resources/journal/{owner_id}/{journal_name}/{element_position}")
    Call<Element> loadElement(@Path("owner_id") String ownerID, @Path("journal_name") String journalName, @Path("element_position") String element_position);
    @POST("/resources/journal")
    Call<Journal> createJournal(@Body Journal newJournal);
    @POST("/resources/update_journal")
    Call<Journal> updateJournal(@Body Journal journalUpdate);
    @POST("/resources/addtoken/{id_facebook}/{id_token}")
    Call<String> addToken(@Path("id_facebook") String id_facebook, @Path("id_token") String id_token);
    @POST("/resources/journal/element/{owner_id}/{journal_name}/{owner_id_element}")
    Call<Element> addElementToJournal(@Body Element newElement, @Path("owner_id") String ownerID, @Path("journal_name") String journalName, @Path("owner_id_element") String owner_id_element);
}
