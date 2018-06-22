package com.capraraedefrancescosoft.progettomobidev.models;


import java.io.Serializable;
import java.util.Date;

/**
 * Created by Ianfire on 24/06/2016.
 */
public class Element implements Serializable{

    /** 'content' e' una stringa di testo oppure un path alla risorsa relativa, a seconda di 'type' */
    private String content;
    /** e' l'hash del content se l'element non e' di tipo note */
    private int id;
    private float longitude;
    private float latitude;
    private Date date;
    private ElementType type;
    private String ownerName;


    public Element(ElementType type){
        this.content = null;
        this.type = type;
        this.longitude = 0;
        this.latitude = 0;
        this.date = new Date();
        this.id = 0;
    }

    public Element(Element element) {
        setContent(element.getContent());
        setLatitude(element.getLatitude());
        setDate(element.getDate());
        setOwnerName(element.getOwnerName());
        setLongitude(element.getLongitude());
        setType(element.getType());
        setId(element.getId());
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ElementType getType() {
        return type;
    }

    public void setType(ElementType type) {
        this.type = type;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
