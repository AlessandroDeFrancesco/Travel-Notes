package com.capraraedefrancescosoft.progettomobidev.activities;

import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.capraraedefrancescosoft.progettomobidev.models.Journals;
import com.capraraedefrancescosoft.progettomobidev.R;
import com.capraraedefrancescosoft.progettomobidev.models.Element;
import com.capraraedefrancescosoft.progettomobidev.models.Journal;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

    private GoogleMap mMap;
    private Element element;
    private Journal journal;
    private HashMap<Marker,Element> markerElementHashMap;

    Typeface title_font;
    Typeface extra_font;
    Typeface note_font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        element = (Element) getIntent().getSerializableExtra(WallActivity.EXTRA_ELEMENT);
        journal = Journals.getInstance().getCurrentJournal();
        markerElementHashMap = new HashMap<>();

        // setto i font
        title_font = Typeface.createFromAsset(getAssets(), "fonts/accanthis-adf-std.bold.otf");
        extra_font = Typeface.createFromAsset(getAssets(), "fonts/accanthis-adf-std.regular.otf");
        note_font = Typeface.createFromAsset(getAssets(), "fonts/ammys_handwriting.ttf");
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng di = new LatLng(element.getLatitude(), element.getLongitude());

        // settaggio marker
        Marker marker = mMap.addMarker(new MarkerOptions().position(di));
        // inserimento nella hash map
        markerElementHashMap.put(marker, element);
        // settaggio info adapter
        mMap.setInfoWindowAdapter(this);
        // mostra l'info window per l'elemento selezionato
        marker.showInfoWindow();
        //setta il listener su ogni marker della mappa
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });

        // tutti gli altri pin setta il marker
        for (final Element element_journal: journal.getElements()){
            LatLng newDi = new LatLng((double) element_journal.getLatitude(), (double)element_journal.getLongitude());
            if (!di.equals(newDi)){
                Marker new_marker = mMap.addMarker(new MarkerOptions().position(newDi).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                markerElementHashMap.put(new_marker, element_journal);
            }
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(di, 15));
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);

        ViewGroup viewGroup = (ViewGroup) v.findViewById(R.id.element_container_maps);
        TextView text_owner = (TextView) v.findViewById(R.id.text_element_owner);
        text_owner.setTypeface(title_font);
        TextView text_date = (TextView) v.findViewById(R.id.text_element_date);
        text_date.setTypeface(extra_font);
        TextView text_hour = (TextView) v.findViewById(R.id.text_element_hour);
        text_hour.setTypeface(extra_font);

        Element element = markerElementHashMap.get(marker);

        //setto l'owner dell'elemento
        text_owner.setText(element.getOwnerName());

        // prendo il giorno e l'ora dell'elemento
        Calendar calDate = Calendar.getInstance();
        calDate.setTime(element.getDate());
        int year = calDate.get(Calendar.YEAR);
        int month = calDate.get(Calendar.MONTH) + 1;
        int day = calDate.get(Calendar.DAY_OF_MONTH);
        int min = element.getDate().getMinutes();
        int hours = element.getDate().getHours();

        String date_to_insert = day + "/" + month + "/" + year;
        text_date.setText(date_to_insert);
        String hour_to_insert = (hours < 10 ? "0" + hours : hours) + ":" + (min < 10 ? "0" + min : min);
        text_hour.setText(hour_to_insert);

        // Settaggio content, a seconda del tipo
        if (element.getContent() != null){
            switch (element.getType()) {
                case NOTE:
                    TextView text = ((TextView) (getLayoutInflater().inflate(R.layout.element_note_maps, viewGroup).findViewById(R.id.element)));
                    text.setTypeface(note_font);
                    text.setText(element.getContent());
                    break;
                case IMAGE:
                    ((ImageView)(getLayoutInflater().inflate(R.layout.element_image_maps, viewGroup).findViewById(R.id.element))).setImageURI(Uri.parse(element.getContent()));
                    break;
                case VIDEO:
                    Uri video_uri = Uri.parse(element.getContent());
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    try {
                        retriever.setDataSource(video_uri.getPath());
                        ((ImageView)(getLayoutInflater().inflate(R.layout.element_image_maps, viewGroup)
                                .findViewById(R.id.element)))
                                .setImageBitmap(retriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST));
                    } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            retriever.release();
                        } catch (RuntimeException ex) {
                            ex.printStackTrace();
                        }
                    }
                    break;
            }
        }
        return v;
    }

}
