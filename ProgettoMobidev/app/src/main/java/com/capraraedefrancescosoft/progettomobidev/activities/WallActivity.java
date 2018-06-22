package com.capraraedefrancescosoft.progettomobidev.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bartoszlipinski.flippablestackview.FlippableStackView;
import com.bartoszlipinski.flippablestackview.StackPageTransformer;
import com.capraraedefrancescosoft.progettomobidev.models.Journals;
import com.capraraedefrancescosoft.progettomobidev.R;
import com.capraraedefrancescosoft.progettomobidev.adapters.PagerAdapterDays;
import com.capraraedefrancescosoft.progettomobidev.adapters.PagerAdapterElements;
import com.capraraedefrancescosoft.progettomobidev.models.Element;
import com.capraraedefrancescosoft.progettomobidev.models.ElementType;
import com.capraraedefrancescosoft.progettomobidev.models.Journal;
import com.capraraedefrancescosoft.progettomobidev.models.JournalID;
import com.capraraedefrancescosoft.progettomobidev.rest.AddElementCallback;
import com.capraraedefrancescosoft.progettomobidev.rest.GetElementCallback;
import com.capraraedefrancescosoft.progettomobidev.rest.GetJournalCallback;
import com.capraraedefrancescosoft.progettomobidev.rest.NewJournalCallback;
import com.capraraedefrancescosoft.progettomobidev.rest.ServerCalls;
import com.capraraedefrancescosoft.progettomobidev.rest.UpdateJournalCallback;
import com.capraraedefrancescosoft.progettomobidev.utilities.ListenerUtility;
import com.capraraedefrancescosoft.progettomobidev.utilities.MemoryCacheUtility;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;

import java.util.Date;
import java.util.List;

public class WallActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    private static final float DEFAULT_CURRENT_PAGE_SCALE = 0.9f;
    private static final float DEFAULT_TOP_STACKED_SCALE = 0.7f;
    private static final float DEFAULT_OVERLAP_FACTOR = 0.4f;

    public static final String EXTRA_TYPE_KEY = "ELEMENT_TYPE";
    public static final String EXTRA_ACTION = "TAKE PHOTO/VIDEO";
    public static final String EXTRA_ELEMENT = "ELEMENT";
    public static final String EXTRA_JOURNAL = "JOURNAL";
    public static final String FIREBASE_MESSAGE_RECEIVED = "FIREBASE_MESSAGE_RECEIVED";

    private static final int ADD_ELEMENT = 4656;
    private static final int NEW_JOURNAL = 4816;
    private static final int JOURNALS = 48421;
    private static final int SETTINGS = 35981;
    private static final int INFORMATION_UPDATE_JOURNAL = 8979;

    private PopupWindow popupWindow;
    private PagerAdapterElements adapterElements;
    private PagerAdapterDays adapterDays;
    private ViewPager pagerDays;
    private FlippableStackView pagerElements;

    private SharedPreferences sharedPreferences;
    private ImageButton refreshButton;
    private ViewGroup layoutNoElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //caricamento shared preferences
        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // aggiunta dell'header
        View headerView = navigationView.inflateHeaderView(R.layout.activity_wall_nav_header);
        // view pager degli elementi e dei giorni
        pagerElements = (FlippableStackView) findViewById(R.id.pagerElements);
        pagerElements.initStack(3, StackPageTransformer.Orientation.HORIZONTAL, DEFAULT_CURRENT_PAGE_SCALE, DEFAULT_TOP_STACKED_SCALE, DEFAULT_OVERLAP_FACTOR, StackPageTransformer.Gravity.BOTTOM);
        pagerDays = (ViewPager) findViewById(R.id.pagerDays);

        layoutNoElement = (ViewGroup) findViewById(R.id.layoutNoElement);

        // pulsante che compare quando ci sono nuovi elementi da scaricare
        refreshButton = (ImageButton) findViewById(R.id.buttonRefreshJournal);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ruota quando premuto
                refreshButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation));
                // scarica i nuovi elementi
                ServerCalls.downloadJournal(new GetJournalCallback() {
                    @Override
                    public void onJournalLoaded(final Journal journal) {
                        Journals.getInstance().updateJournal(journal);
                        refreshCurrentJournal();
                        refreshButton.setAnimation(null);
                    }
                }, getCurrentJournal().getOwnerID(), getCurrentJournal().getName());
                System.out.println("Refresh del journal dal server");
            }
        });

        // setto l'immagine del profilo e il nome
        ProfilePictureView profilePictureView = (ProfilePictureView) headerView.findViewById(R.id.imageViewProfile);
        profilePictureView.setProfileId(Profile.getCurrentProfile().getId());

        TextView profileName = (TextView) headerView.findViewById(R.id.textViewProfile);
        profileName.setText(Profile.getCurrentProfile().getName());

    }

    private Journal getCurrentJournal() {
        return Journals.getInstance().getCurrentJournal();
    }

    private void refreshCurrentJournal() {
        // scarico gli elementi dal server se non sono in cache
        downloadJournalContents(getCurrentJournal().getElements());

        // ricarico l'UI con i nuovi dati
        reloadUI();

        // setto il nome del journal come titolo
        setTitle(getCurrentJournal().getName());
        // mostro o no l'immagine per il journal vuoto
        if (getCurrentJournal().getElements().isEmpty())
            layoutNoElement.setVisibility(View.VISIBLE);
        else
            layoutNoElement.setVisibility(View.INVISIBLE);
    }


    private void reloadUI() {
        // adapter per gli elementi e per i giorni del journal
        adapterDays = new PagerAdapterDays(this, this.getSupportFragmentManager(), getCurrentJournal());
        adapterElements = new PagerAdapterElements(this, this.getSupportFragmentManager(), getCurrentJournal(), getCurrentJournal().getLastDate());
        // riadatto l'adapter considerando i nuovi elementi
        pagerElements.setAdapter(adapterElements);
        pagerElements.setCurrentItem(adapterElements.getCount() - 1);

        /* riadatto l'adapter considerando il giorno attuale (se è cambiato cambia il giorno in quello nuovo */
        pagerDays.setAdapter(adapterDays);
        pagerDays.addOnPageChangeListener(this);
        pagerDays.setCurrentItem(adapterDays.getCount() - 1); // setto all'ultimo giorno
        adapterDays.notifyDataSetChanged();

        // mostro o no l'immagine per il journal vuoto
        if (getCurrentJournal().getElements().isEmpty())
            layoutNoElement.setVisibility(View.VISIBLE);
        else
            layoutNoElement.setVisibility(View.INVISIBLE);
    }

    private void downloadJournalContents(List<Element> elements) {
        for (int i = 0; i < elements.size(); i++) {
            final Element element = elements.get(i);
            String content = MemoryCacheUtility.getElementContentFromCache(element);
            element.setContent(content);
            if (content == null) {
                // il contenuto e' null, lo scarico dal server
                ServerCalls.downloadElement(new GetElementCallback() {
                    @Override
                    public void onElementLoaded(Element elem) {
                        // ha scaricato l'elemento, salvo il content in cache
                        String cachedContent = MemoryCacheUtility.cacheElementContent(elem);
                        element.setContent(cachedContent);
                        element.setId(elem.getId());
                        // dico all'adapter di aggiornare le view con i nuovi dati
                        adapterElements.refreshFragments();
                    }
                }, getCurrentJournal().getOwnerID(), getCurrentJournal().getName(), i);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // chiusura applicazione
            ListenerUtility.showYesNoDialog(this, getString(R.string.exit), getString(R.string.exit_confirmation),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                finish();
                            } else {
                                dialog.dismiss();
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.wall, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    //register your activity onResume()
    @Override
    public void onResume() {
        super.onResume();
        Log.d("wall", "onResume with intent: " + getIntent());
        // rimane in ascolto per il messaggio di refreshare il journal
        getApplicationContext().registerReceiver(mMessageReceiver, new IntentFilter(FIREBASE_MESSAGE_RECEIVED));
        // se riceve una notifica push, apre il journal desiderato
        if (getIntent() != null && getIntent().getSerializableExtra(EXTRA_JOURNAL) != null) {
            final JournalID journalID = (JournalID) getIntent().getSerializableExtra(EXTRA_JOURNAL);
            // scarico il journal
            ServerCalls.downloadJournal(new GetJournalCallback() {
                @Override
                public void onJournalLoaded(final Journal journal) {
                    Journals.getInstance().updateJournal(journal);
                    Journals.getInstance().setCurrentJournal(journalID);

                    refreshCurrentJournal();
                }
            }, journalID.getOwnerID(), journalID.getName());
            setIntent(null);
        } else {
            if (getCurrentJournal() == null) { // riapre l'ultimo journal aperto o la lista dei journals se non ce n'e' uno
                String lastJournalName = sharedPreferences.getString(getString(R.string.preference_last_journal_name), null);
                long lastJournalOwner = sharedPreferences.getLong(getString(R.string.preference_last_journal_owner), -1);
                Log.d("wall", "last preferences " + lastJournalName + " " + lastJournalOwner);
                if (lastJournalName != null && lastJournalOwner != -1) {
                    JournalID lastJournalID = new JournalID(lastJournalOwner, lastJournalName);
                    Journal lastJournal = Journals.getInstance().getJournal(lastJournalID);
                    if (lastJournal != null) {
                        Journals.getInstance().setCurrentJournal(lastJournalID);
                        refreshCurrentJournal();
                        Log.d("wall", "last journal loaded from the save " + lastJournalID);
                    } else if (!Journals.getInstance().getJournals().isEmpty()) {
                        Intent intent = new Intent(this, JournalsActivity.class);
                        startActivityForResult(intent, JOURNALS);
                    } else {
                        Intent intent = new Intent(this, NewJournalActivity.class);
                        startActivityForResult(intent, NEW_JOURNAL);
                    }
                } else if (!Journals.getInstance().getJournals().isEmpty()) {
                    Intent intent = new Intent(this, JournalsActivity.class);
                    startActivityForResult(intent, JOURNALS);
                } else {
                    Intent intent = new Intent(this, NewJournalActivity.class);
                    startActivityForResult(intent, NEW_JOURNAL);
                }
            } else {
                getCurrentJournal().printAll();
                refreshCurrentJournal();
            }
        }
    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        getApplicationContext().unregisterReceiver(mMessageReceiver);
        if (getCurrentJournal() != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.preference_last_journal_name), getCurrentJournal().getName());
            editor.putLong(getString(R.string.preference_last_journal_owner), getCurrentJournal().getOwnerID());
            editor.commit();
            System.out.println("LAST JOURNAL: " + sharedPreferences.getString(getString(R.string.preference_last_journal_name), "default") + sharedPreferences.getLong(getString(R.string.preference_last_journal_owner), -1));
        }
    }


    // Questo riceve i broadcaster, se e' il messaggio di refresh allora fa animare il pulsante per il refresh
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // anima il pulsante del refresh
            if (getCurrentJournal().getJournalID().equals(intent.getSerializableExtra(EXTRA_JOURNAL))) {
                refreshButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pulse));
            }
        }
    };

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.new_journal) {
            Intent intent = new Intent(this, NewJournalActivity.class);
            startActivityForResult(intent, NEW_JOURNAL);
        } else if (id == R.id.journals) {
            Intent intent = new Intent(this, JournalsActivity.class);
            startActivityForResult(intent, JOURNALS);
        } else if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, SETTINGS);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openPopUpAddElement(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.pop_up_add_element, null);

        popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.FadeAnimation);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    public void onAddPopUpClicked(View view) {
        Intent intent = new Intent(this, AddElementActivity.class);

        switch (view.getId()) {
            case R.id.add_image:
                intent.putExtra(EXTRA_TYPE_KEY, ElementType.IMAGE);
                break;
            case R.id.add_video:
                intent.putExtra(EXTRA_TYPE_KEY, ElementType.VIDEO);
                break;
            case R.id.add_note:
                intent.putExtra(EXTRA_TYPE_KEY, ElementType.NOTE);
                break;
            case R.id.take_image:
                intent.putExtra(EXTRA_TYPE_KEY, ElementType.IMAGE);
                intent.putExtra(EXTRA_ACTION, ElementType.IMAGE);
                break;
            case R.id.take_video:
                intent.putExtra(EXTRA_TYPE_KEY, ElementType.VIDEO);
                intent.putExtra(EXTRA_ACTION, ElementType.VIDEO);
                break;
        }

        popupWindow.dismiss();
        this.startActivityForResult(intent, ADD_ELEMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            if (requestCode == ADD_ELEMENT)
                Toast.makeText(this, "Add element aborted", Toast.LENGTH_SHORT).show();
            else if (requestCode == NEW_JOURNAL)
                Toast.makeText(this, "New journal aborted", Toast.LENGTH_SHORT).show();
        } else if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ADD_ELEMENT:
                    final Element element = (Element) data.getSerializableExtra(EXTRA_ELEMENT);
                    getCurrentJournal().addElement(element);
                    // notifico la presenza che il dataset è cambiato
                    reloadUI();
                    Log.d("wall", "added new element");

                    // Faccio l'upload al server
                    ServerCalls.uploadElementToJournal(new AddElementCallback() {
                        @Override
                        public void elementUploaded(boolean uploaded) {
                            if (uploaded)
                                System.out.println("Elemento Uppato sul server " + element.getType());
                        }
                    }, element, getContentResolver(), getCurrentJournal().getOwnerID(), getCurrentJournal().getName(), Profile.getCurrentProfile().getId());
                    break;
                case NEW_JOURNAL:
                    JournalID newJournalID = (JournalID) data.getSerializableExtra(EXTRA_JOURNAL);
                    Journals.getInstance().setCurrentJournal(newJournalID);

                    refreshCurrentJournal();

                    Log.d("wall", "showing journal " + getCurrentJournal().getName());
                    // Faccio l'upload al server
                    ServerCalls.uploadNewJournal(new NewJournalCallback() {
                        @Override
                        public void elementUploaded(boolean uploaded) {
                            if (uploaded)
                                System.out.println("Nuovo Journal Uppato sul server");
                        }
                    }, getCurrentJournal());
                    break;
                case JOURNALS:
                    JournalID newJournalID2 = (JournalID) data.getSerializableExtra(EXTRA_JOURNAL);
                    Journals.getInstance().setCurrentJournal(newJournalID2);

                    refreshCurrentJournal();

                    Log.d("wall", "showing journal: " + getCurrentJournal().getName());
                    break;
                case SETTINGS:
                    System.out.println("Return to wall activity");
                    break;
                case INFORMATION_UPDATE_JOURNAL:
                    final JournalID newJournalID3 = (JournalID) data.getSerializableExtra(EXTRA_JOURNAL);
                    Journals.getInstance().setCurrentJournal(newJournalID3);

                    refreshCurrentJournal();

                    // Faccio l'upload al server
                    ServerCalls.updateJournal(new UpdateJournalCallback() {
                        @Override
                        public void journalUploaded(boolean uploaded) {
                            if (uploaded) {
                                System.out.println("Update Journal " + newJournalID3 + " effettuato sul server");
                            }
                        }
                    }, getCurrentJournal());
                    System.out.println("Return to wall activity");
                    break;
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
    }

    public void openInfoJournal(View v) {
        //avvio dell'activity wall e chiusura di questa activity
        Intent result = new Intent(WallActivity.this, InformationJournalActivity.class);
        result.putExtra(WallActivity.EXTRA_JOURNAL, getCurrentJournal().getJournalID());
        startActivityForResult(result, INFORMATION_UPDATE_JOURNAL);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    // quando si cambia il giorno da visualizzare del journal
    @Override
    public void onPageSelected(int position) {
        Date date = adapterDays.getDay(position);
        adapterElements = new PagerAdapterElements(this, getSupportFragmentManager(), getCurrentJournal(), date);
        pagerElements.setAdapter(adapterElements);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void onArrowClick(View v) {
        int position = 0;
        if (v.getId() == R.id.daysLeftArrow)
            position = pagerDays.getCurrentItem() - 1;
        else if (v.getId() == R.id.daysRightArrow)
            position = pagerDays.getCurrentItem() + 1;

        Date date = adapterDays.getDay(position);
        adapterElements = new PagerAdapterElements(this, getSupportFragmentManager(), getCurrentJournal(), date);
        pagerDays.setCurrentItem(position);
        pagerElements.setAdapter(adapterElements);
    }

}
