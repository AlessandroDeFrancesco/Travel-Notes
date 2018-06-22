package com.capraraedefrancescosoft.progettomobidev.utilities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Gianpaolo Caprara on 9/14/2016.
 */
public class GeoLocationUtility {

    private static GeoLocationUtility instance;
    // mappa per tenere traccia delle coordinate degli elementi del journal
    HashMap<String,String> geoLocation;

    private GeoLocationUtility(){
        geoLocation = new HashMap<>();
    }

    public static GeoLocationUtility getInstance(){
        if (instance == null)
            instance = new GeoLocationUtility();
        return instance;
    }

    public void setGeoLocation(final Context context, final float newLat, final float newLon, final LocationFoundCallback locationFoundCallback){
        AsyncTask<Float, Float, String> task = new AsyncTask<Float, Float, String>() {
            @Override
            protected String doInBackground(Float... params) {
                String location = "";
                Geocoder gcd = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(newLat, newLon, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addresses != null && addresses.size() > 0) {
                    location = "";
                    if (addresses.get(0).getThoroughfare() != null)
                        location += addresses.get(0).getThoroughfare() + ", ";
                    if (addresses.get(0).getLocality() != null)
                        location += addresses.get(0).getLocality() + ", ";
                    if (addresses.get(0).getCountryName() != null)
                        location += addresses.get(0).getCountryName();

                    geoLocation.put(newLat + "," + newLon, location);
                }
                return location;
            }

            @Override
            protected void onPostExecute(String location) {
                if(!location.isEmpty())
                    locationFoundCallback.onLocationFound(location);
            }
        };

        task.execute();
    }

    /** il callback verra' chiamato sul thread dell'UI*/
    public void getLocation (Context context, float newLat, float newLon, LocationFoundCallback locationFoundCallback){
        if (geoLocation.get(newLat + "," + newLon) == null){
            setGeoLocation(context, newLat,newLon, locationFoundCallback);
        } else {
            locationFoundCallback.onLocationFound(geoLocation.get(newLat + "," + newLon));
        }
    }

    public interface LocationFoundCallback {
        void onLocationFound(String location);
    }
}
