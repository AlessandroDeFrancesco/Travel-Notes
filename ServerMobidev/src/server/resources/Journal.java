package server.resources;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Journal {

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

    public Journal(Journal journal) {
    	this.name = journal.getName();
        this.description = journal.getDescription();
        this.type= journal.getType();
        this.city = journal.city;
        this.departureDate = journal.departureDate;
        this.returnDate = journal.returnDate;
        this.elements = journal.getElements();
        this.participants = journal.getParticipants();
        this.owner_id = journal.getOwnerId();
	}
    
    // svuota gli elements del contenuto se non sono di tipo NOTE
    public void clearElements(){
    	ArrayList<Element> newElements = new ArrayList<Element>();
    	for(Element element:elements){
    		Element newElement = new Element(element);
    		if(element.getType() != ElementType.NOTE)
    			newElement.clearContent();
    		newElements.add(newElement);
    	}
    	
    	this.elements = newElements;
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

    public Long getOwnerId() {
        return this.owner_id;
    }

    public void setOwnerId(Long own_id) {
        this.owner_id = own_id;
    }

    public void printAll() {
        System.out.println(getName() + "\n" +
                getType() + "\n" +
                getCity() + "\n" +
                getDescription() + "\n" +
                getDepartureDate() + "\n" +
                getReturnDate() + "\n" +
                "num elements: " + getElements().size() + "\n" +
                "num participants: " + getParticipants().size());
    }
}


