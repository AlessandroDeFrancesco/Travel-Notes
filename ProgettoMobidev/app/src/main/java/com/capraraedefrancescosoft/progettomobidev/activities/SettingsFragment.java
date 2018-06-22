package com.capraraedefrancescosoft.progettomobidev.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;

import com.capraraedefrancescosoft.progettomobidev.R;
import com.capraraedefrancescosoft.progettomobidev.utilities.ListenerUtility;
import com.facebook.login.LoginManager;

public class SettingsFragment extends PreferenceFragment {

    public static final String KEY_PREF_NOTIFICATIONS = "pref_notifications";
    public static final String KEY_PREF_LOGOUT = "pref_logout";
    SharedPreferences sharedPref;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //ottenere le shared preferences
        Context context = getActivity();
        sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        if(sharedPref.getBoolean(getString(R.string.preference_notifications), true)){
            findPreference(KEY_PREF_NOTIFICATIONS).setDefaultValue(true);
        } else{
            findPreference(KEY_PREF_NOTIFICATIONS).setDefaultValue(false);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference){
        if(preference == findPreference(KEY_PREF_NOTIFICATIONS)) {
            SwitchPreference notifications = (SwitchPreference) findPreference(KEY_PREF_NOTIFICATIONS);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.preference_notifications), notifications.isChecked());
            editor.commit();
            System.out.println("NOTIFICATIONS: " + sharedPref.getBoolean(getString(R.string.preference_notifications), true));
        } else if (preference == findPreference(KEY_PREF_LOGOUT)){
            System.out.println("LOGOUT");
            ListenerUtility.showYesNoDialog(getActivity(), getString(R.string.logout), getString(R.string.logout_confirmation),
            new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == DialogInterface.BUTTON_POSITIVE){
                        System.out.println("YES LOGOUT");
                        dialog.dismiss();
                        LoginManager.getInstance().logOut();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    } else {
                        System.out.println("NO LOGOUT");
                        dialog.dismiss();
                    }
                }
            });
        }
        return true;
    }

}

