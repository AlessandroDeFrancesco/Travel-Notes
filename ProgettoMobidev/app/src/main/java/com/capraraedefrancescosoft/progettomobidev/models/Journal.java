package com.capraraedefrancescosoft.progettomobidev.models;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

public class Journal {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private String name;
    private String description;
    private String type;
    private String city;
    private Long owner_id;
    private Date departureDate;
    private Date returnDate;
    private List<Long> participants;
    private List<Element> elements;

    public Journal(String name, Long own_id){
        this.name = name;
        this.description = type = city = "";
        this.departureDate = returnDate = new Date();
        this.elements = new ArrayList<Element>();
        this.participants = new ArrayList<Long>();
        this.owner_id = own_id;
    }

    public Journal(String name, String desc, String type, String city, Long own_id){
        this.name = name;
        this.description = desc;
        this.type= type;
        this.city = city;
        this.departureDate = returnDate = new Date();
        this.elements = new ArrayList<Element>();
        this.participants = new ArrayList<Long>();
        this.owner_id = own_id;
    }

    public Journal(String name, String desc, String type, String city, Long own_id, Date depDate, Date retDate){
        this.name = name;
        this.description = desc;
        this.type= type;
        this.city = city;
        this.departureDate = depDate;
        this.returnDate = retDate;
        this.elements = new ArrayList<Element>();
        this.participants = new ArrayList<Long>();
        this.owner_id = own_id;
    }

    public synchronized void addElement(Element element){
        elements.add(element);
    }

    public synchronized  Element getElement(int i){
        if(i >= 0 && i < getSize())
            return elements.get(i);
        else
            return null;
    }

    public synchronized List<Element> getElements() {
        return new ArrayList<Element>(elements);
    }

    public synchronized int getSize() {
        return elements.size();
    }

    public List<Long> getParticipants() {
        return participants;
    }

    public void addParticipant(Long participant) {
        participants.add(participant);
    }

    public void setParticipants(ArrayList<Long> participants) {
        this.participants = participants;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getOwnerID() {
        return this.owner_id;
    }

    public void setOwnerId(Long own_id) {
        this.owner_id = own_id;
    }

    public void printAll() {
        Log.d("Journal", getName() + "\n" +
                getOwnerID() + "\n" +
                getType() + "\n" +
                getCity() + "\n" +
                getDescription() + "\n" +
                getDepartureDate() + "\n" +
                getReturnDate() + "\n" +
                "num elements: " + getElements().size() + "\n" +
                "num participants: " + getParticipants().size());
    }

    public ArrayList<Date> getAllJournalDays(){

        HashSet<Integer> datesInDays = new HashSet<Integer>();
        Calendar calDate = Calendar.getInstance();
        for(Element element : elements){
            calDate.setTime(element.getDate());
            long year = calDate.get(Calendar.YEAR);
            long month = calDate.get(Calendar.MONTH);
            long day = calDate.get(Calendar.DAY_OF_MONTH);
            long calcDate = year * 100 + month;
            calcDate = calcDate * 100 + day;
            datesInDays.add((int)calcDate);
        }

        // riconverte in Date
        ArrayList<Date> dates = new ArrayList<>();
        for(Integer d : datesInDays.toArray(new Integer[datesInDays.size()])){
            int year = d / 10000;
            int month = (d % 10000) / 100;
            int day = d % 100;
            Date date = new GregorianCalendar(year, month, day).getTime();
            dates.add(date);
        }

        Collections.sort(dates);

        return dates;
    }

    /** Ritorna tutti gli elementi di un dato giorno */
    public ArrayList<Element> getElementsOfDay(Date date){
        ArrayList<Element> list = new ArrayList<Element>();
        Calendar calDate = Calendar.getInstance();
        Calendar calElement = Calendar.getInstance();
        calDate.setTime(date);

        for(Element element : elements){
            calElement.setTime(element.getDate());
            boolean isSameDay = calDate.get(Calendar.YEAR) == calElement.get(Calendar.YEAR) && calDate.get(Calendar.DAY_OF_YEAR) == calElement.get(Calendar.DAY_OF_YEAR);
            if(isSameDay) {
                list.add(element);
                System.out.println(element.getDate() + " " + element.getOwnerName());
            }
        }

        return list;
    }

    // ritorna l'ultima data tra gli elementi
    public Date getLastDate() {
        if(elements.isEmpty())
            return departureDate;

        Date lastDate = elements.get(0).getDate();
        for(Element element : elements){
            if(lastDate.before(element.getDate()))
                lastDate = element.getDate();
        }
        return lastDate;
    }

    public JournalID getJournalID() {
        return new JournalID(getOwnerID(), getName());
    }
}


