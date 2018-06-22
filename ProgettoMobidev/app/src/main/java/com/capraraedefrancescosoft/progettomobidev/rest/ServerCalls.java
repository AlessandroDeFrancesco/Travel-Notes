package com.capraraedefrancescosoft.progettomobidev.rest;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.capraraedefrancescosoft.progettomobidev.R;
import com.capraraedefrancescosoft.progettomobidev.models.Element;
import com.capraraedefrancescosoft.progettomobidev.models.Journal;
import com.capraraedefrancescosoft.progettomobidev.utilities.ContentTypesUtility;
import com.capraraedefrancescosoft.progettomobidev.utilities.ListenerUtility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Ianfire on 29/08/2016.
 */
public class ServerCalls {

    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
    private static final String SERVER_ADDRESS = "http://travel-notes.herokuapp.com/";

    @Nullable
    public static void downloadJournal(final GetJournalCallback getJournalCallback, final long journalOwnerId, final String journalName) {
        AsyncTask<Float, Float, Journal> task = new AsyncTask<Float, Float, Journal>() {
            @Override
            protected Journal doInBackground(Float... params) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SERVER_ADDRESS)
                        .addConverterFactory(GsonConverterFactory.create(GSON))
                        .build();
                ServerApi serverApi = retrofit.create(ServerApi.class);

                //synchronous call
                Call<Journal> call = serverApi.loadJournal(journalOwnerId + "", journalName);
                Journal downloadedJournal = null;
                try {
                    Response<Journal> response = call.execute();
                    if (response.isSuccessful())
                        downloadedJournal = response.body();
                    else
                        System.out.println("Error downloading journal: " + journalName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return downloadedJournal;
            }

            @Override
            protected void onPostExecute(Journal journal) {
                getJournalCallback.onJournalLoaded(journal);
            }
        };

        task.execute();
    }

    @Nullable
    public static void downloadAllJournals(final Activity activity, final GetAllJournalsCallback getAllJournalsCallback, final long journalOwnerId) {
        AsyncTask<Float, Float, ArrayList<Journal>> task = new AsyncTask<Float, Float, ArrayList<Journal>>() {
            @Override
            protected ArrayList<Journal> doInBackground(Float... params) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SERVER_ADDRESS)
                        .addConverterFactory(GsonConverterFactory.create(GSON))
                        .build();
                ServerApi serverApi = retrofit.create(ServerApi.class);

                //synchronous call
                Call<ArrayList<Journal>> call = serverApi.loadAllJournals(journalOwnerId + "");
                ArrayList<Journal> downloadedJournals = new ArrayList<>();
                try {
                    Response<ArrayList<Journal>> response = call.execute();
                    if (response.isSuccessful())
                        downloadedJournals = response.body();
                    else {
                        System.out.println("Error downloading all journals: " + journalOwnerId);
                        downloadedJournals = null;
                    }
                } catch (IOException e) {
                    downloadedJournals = null;
                }
                return downloadedJournals;
            }

            @Override
            protected void onPostExecute(ArrayList<Journal> journals) {
                if (journals != null) {
                    getAllJournalsCallback.onJournalsLoaded(journals);
                } else {
                    ListenerUtility.showYesNoDialog(activity, activity.getResources().getString(R.string.error), activity.getResources().getString(R.string.error_server), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                downloadAllJournals(activity, getAllJournalsCallback, journalOwnerId);
                            } else {
                                System.exit(0);
                            }
                        }
                    });
                }
            }
        };
        task.execute();
    }

    @Nullable
    public static void downloadElement(final GetElementCallback getElementCallback, final long journalOwnerId, final String journalName, final int element_position) {
        AsyncTask<Float, Float, Element> task = new AsyncTask<Float, Float, Element>() {
            @Override
            protected Element doInBackground(Float... params) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SERVER_ADDRESS)
                        .addConverterFactory(GsonConverterFactory.create(GSON))
                        .build();
                ServerApi serverApi = retrofit.create(ServerApi.class);

                //synchronous call
                Call<Element> call = serverApi.loadElement(journalOwnerId + "", journalName, element_position + "");
                Element downloadedElement = null;
                try {
                    Response<Element> response = call.execute();
                    if (response.isSuccessful())
                        downloadedElement = response.body();
                    else
                        System.out.println("Error downloading element: " + journalName + " " + element_position);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return downloadedElement;
            }

            @Override
            protected void onPostExecute(Element element) {
                getElementCallback.onElementLoaded(element);
            }
        };

        task.execute();
    }

    public static void uploadElementToJournal(final AddElementCallback getJournalCallback, Element newElement, ContentResolver contentResolver, final long journalOwnerId, final String journalName, final String elementOwnerId) {
        // converto l'Immagine o Video da uri a stringa codificata
        final Element element = ContentTypesUtility.convertElementContentToBase64(newElement, contentResolver);

        AsyncTask<Float, Float, Boolean> task = new AsyncTask<Float, Float, Boolean>() {
            @Override
            protected Boolean doInBackground(Float... params) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SERVER_ADDRESS)
                        .addConverterFactory(GsonConverterFactory.create(GSON))
                        .build();
                ServerApi serverApi = retrofit.create(ServerApi.class);

                //synchronous call
                Call<Element> call = serverApi.addElementToJournal(element, journalOwnerId + "", journalName, elementOwnerId);
                boolean uploaded = false;
                try {
                    Response<Element> response = call.execute();
                    if (response.isSuccessful()) {
                        uploaded = true;
                    } else {
                        System.out.println("Error uploading: " + element.getType());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return uploaded;
            }

            @Override
            protected void onPostExecute(Boolean uploaded) {
                getJournalCallback.elementUploaded(uploaded);
            }
        };

        task.execute();
    }


    public static void uploadNewJournal(final NewJournalCallback newJournalCallback, final Journal new_Journal) {
        AsyncTask<Float, Float, Boolean> task = new AsyncTask<Float, Float, Boolean>() {
            @Override
            protected Boolean doInBackground(Float... params) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SERVER_ADDRESS)
                        .addConverterFactory(GsonConverterFactory.create(GSON))
                        .build();
                ServerApi serverApi = retrofit.create(ServerApi.class);

                //synchronous call
                Call<Journal> call = serverApi.createJournal(new_Journal);
                boolean uploaded = false;
                try {
                    Response<Journal> response = call.execute();
                    if (response.isSuccessful())
                        uploaded = true;
                    else
                        System.out.println("Error downloading: " + new_Journal.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return uploaded;
            }

            @Override
            protected void onPostExecute(Boolean uploaded) {
                newJournalCallback.elementUploaded(uploaded);
            }
        };

        task.execute();
    }

    public static void updateJournal(final UpdateJournalCallback updateJournalCallback, final Journal updateJournal) {
        AsyncTask<Float, Float, Boolean> task = new AsyncTask<Float, Float, Boolean>() {
            @Override
            protected Boolean doInBackground(Float... params) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SERVER_ADDRESS)
                        .addConverterFactory(GsonConverterFactory.create(GSON))
                        .build();
                ServerApi serverApi = retrofit.create(ServerApi.class);

                //synchronous call
                Call<Journal> call = serverApi.updateJournal(updateJournal);
                boolean uploaded = false;
                try {
                    Response<Journal> response = call.execute();
                    if (response.isSuccessful())
                        uploaded = true;
                    else
                        System.out.println("Error updating: " + updateJournal.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return uploaded;
            }

            @Override
            protected void onPostExecute(Boolean uploaded) {
                updateJournalCallback.journalUploaded(uploaded);
            }
        };

        task.execute();
    }

    public static void addToken(final AddTokenCallback newTokenCallback, final String id_facebook, final String id_token) {
        AsyncTask<Float, Float, Boolean> task = new AsyncTask<Float, Float, Boolean>() {
            @Override
            protected Boolean doInBackground(Float... params) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SERVER_ADDRESS)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();
                ServerApi serverApi = retrofit.create(ServerApi.class);

                //synchronous call
                Call<String> call = serverApi.addToken(id_facebook, id_token);
                boolean uploaded = false;
                try {
                    Response<String> response = call.execute();
                    if (response.isSuccessful())
                        uploaded = true;
                    else
                        System.out.println("Error add token: " + id_facebook + "," + id_token);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return uploaded;
            }

            @Override
            protected void onPostExecute(Boolean uploaded) {
                newTokenCallback.tokenUploaded(uploaded);
            }
        };

        task.execute();
    }

}
