package com.capraraedefrancescosoft.progettomobidev.models;

/**
 * Created by Gianpaolo Caprara on 9/13/2016.
 */
public class FacebookFriend {
    private String name;
    private String id;
    private boolean checked;

    public FacebookFriend(String name, String id){
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
