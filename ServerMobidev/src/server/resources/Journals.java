package server.resources;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Journals {
	
	public List<Journal> journals;
	private static Journals instance;
	
	protected Journals(){
		journals = new ArrayList<Journal>();
		Journal journal = new Journal ("ciao", "journey test", "car", "talsano", 10209327457973903l);
		
		Element element = new Element(ElementType.NOTE);
		element.setContent("Come va?");
		element.setDate(new Date(2016,05,01));
		journal.addElement(element);
		
		element = new Element(ElementType.IMAGE);
		element.setContent("iVBORw0KGgoAAAANSUhEUgAAAB0AAAAfCAYAAAAbW8YEAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAADYSURBVEhL7Zc7DoMwEEQ3uQJtytz/RClpOQPJSkYCf2ZnHD4NrwHEMs/2Wgge0+s928k80/FUbumhFBtpGD/pzOx3L51pRBmbma6LnfyagckIl1cRs7VUT5kwZXAbKeohCkX3wp46qlgVOtXlZcU9Qge+e1EoAgkduJGih2swz4S7VxGztaHUYcKUwVHS3t62CKWsUBkYlKozZOubUhSA+seIq1JG+I+4kCoz7BWHG2mhJegRU1IU7KjiQpoHRMIFts7Z/WO7NrN8QHRPWZiVun8rDuUCqdkX3o1m6QvveDUAAAAASUVORK5CYII=");
		element.setDate(new Date(2016,05,01));
		journal.addElement(element);
		
		element = new Element(ElementType.NOTE);
		element.setContent("Un nuovo giorno!");
		element.setDate(new Date(2016,05,02));
		journal.addElement(element);
		
		element = new Element(ElementType.NOTE);
		element.setContent("WoW!");
		element.setDate(new Date(2016,05,02));
		journal.addElement(element);
		
		this.addJournal(journal);
		journal = new Journal ("secondo", "secondo journey test", "car", "talsano", 10209327457973903l);
		this.addJournal(journal);
	}
	
	public synchronized static Journals getInstance(){
		if (instance == null){
			instance = new Journals();
		}
		return instance;
	}
	
	// trova il journal con un dato owner id e nome journal, null se non lo trova
	public synchronized Journal findJournal(long own_id, String name){
		for (int i = 0; i < journals.size(); i++){
			if (journals.get(i).getOwnerId() == own_id && journals.get(i).getName().equals(name)){
				return journals.get(i);
			}
		}
		return null;
	}
	
	// ritorna una copia del journal richiesto, svuotato del contenuto degli elementi(ad eccezione delle note)
	public synchronized Journal getJournalSvuotato(long own_id, String name){
		Journal journal = new Journal(findJournal(own_id, name));
		if(journal != null)
			journal.clearElements();
		
		return journal;
	}
	
	public synchronized void addJournal(Journal j){
		this.journals.add(j);
	}

	public void addElement(Element new_element, long own_id, String name) {
		Journal journal = findJournal(own_id, name);
		journal.addElement(new_element);
	}

	// restituisce tutti i journal, svuotati del content degli elements
	public ArrayList<Journal> getJournals(long user_id) {
		ArrayList<Journal> ret = new ArrayList<Journal>();
		for (int i = 0; i < journals.size(); i++){
			Journal journal = journals.get(i);
			if (journal.getOwnerId() == user_id || journal.getParticipants().contains(user_id)){
				Journal newJournal = new Journal(journal);
				newJournal.clearElements();
				ret.add(newJournal);
			}
		}
		return ret;
	}

	public Element getElement(long own_id, String name, int elementPosition) {
		for (int i = 0; i < journals.size(); i++){
			if (journals.get(i).getOwnerId() == own_id && journals.get(i).getName().equals(name)){
				return journals.get(i).getElement(elementPosition);
			}
		}
		return null;
	}
}
