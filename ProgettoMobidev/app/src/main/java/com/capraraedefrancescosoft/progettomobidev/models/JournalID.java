package com.capraraedefrancescosoft.progettomobidev.models;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Ianfire on 30/09/2016.
 */

public class JournalID implements Serializable {
    private long ownerID;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JournalID journalID = (JournalID) o;

        if (ownerID != journalID.ownerID) return false;
        return name.equals(journalID.name);

    }

    @Override
    public int hashCode() {
        int result = (int) (ownerID ^ (ownerID >>> 32));
        result = 31 * result + name.hashCode();
        return result;
    }

    public JournalID(long ownerID,@NonNull String name) {
        this.name = name;
        this.ownerID = ownerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(long ownerID) {
        this.ownerID = ownerID;
    }

    @Override
    public String toString() {
        return "ID(" + getName() + ", " + getOwnerID() + ")";
    }
}
