package com.capraraedefrancescosoft.progettomobidev.utilities;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONException;

import java.util.HashMap;

/**
 * Created by Ianfire on 26/09/2016.
 */
public class FacebookUtility {

    public interface GetFacebookNameCallback{
        void nameRetrieved(String name);
    }

    private static FacebookUtility instance = new FacebookUtility();
    private HashMap<Long, String> facebookNamesMap;

    private FacebookUtility(){
        facebookNamesMap = new HashMap<Long, String>();
    }

    public static FacebookUtility getInstance(){
        return instance;
    }

    public void getNameFromId(final Long id, final GetFacebookNameCallback callback){
        String name = facebookNamesMap.get(id);
        if(name == null){
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(), "/" + id, null, HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            try {
                                String name = response.getJSONObject().getString("name");
                                facebookNamesMap.put(id, name);
                                callback.nameRetrieved(name);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            ).executeAsync();
        } else {
            callback.nameRetrieved(name);
        }
    }
}
