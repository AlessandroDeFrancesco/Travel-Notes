package server.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;

import com.google.gson.*;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import server.resources.TokenUsers;

@Path("/resources")
public class Resources {

	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
	private static final String key_auth = "AIzaSyDt-2qdxKKgZLJf9ejCl49hRDou5k9kMSc";

	@GET
	@Path("/journal/{owner_id}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getJournal(@PathParam("owner_id") String own_id, @PathParam("name") String name){
		Journal journal = Journals.getInstance().getJournalSvuotato(Long.parseLong(own_id), name);
		String journal_string = "null";
		if (journal != null){
			journal_string = gson.toJson(journal);
			if(journal_string.length() >= 500)
			System.out.println("Returning journal: " + journal_string.substring(0, 500));
		} else {
			System.out.println("Returning no journal");
		}
		return Response.ok(journal_string).build();
	}

	/** restituisce il contenuto di un elemento */
	@GET
	@Path("/journal/{owner_id}/{name}/{element_position}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getElementContent(@PathParam("owner_id") String own_id, @PathParam("name") String name, @PathParam("element_position") String element_position){
		Element element = Journals.getInstance().getElement(Long.parseLong(own_id), name, Integer.parseInt(element_position));
		String element_string = "null";
		if (element != null){
			element_string = gson.toJson(element);
			System.out.println("Returning element: " + element_string.substring(0,50));
		} else {
			System.out.println("Returning no element");
		}
		return Response.ok(element_string).build();
	}

	@GET
	@Path("/journal/{user_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllJournals(@PathParam("user_id") String user_id){
		ArrayList<Journal> journals = Journals.getInstance().getJournals(Long.parseLong(user_id));
		String journals_string = "null";
		if (journals != null){
			journals_string = gson.toJson(journals);
			if(journals_string.length() >= 500)
				System.out.println("Returning journals: " + journals_string.substring(0, 500));
		} else {
			System.out.println("Returning no journal");
		}
		return Response.ok(journals_string).build();
	}

	@POST
	@Path("/journal")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response createJournal(String journal){
		Journal new_journal = gson.fromJson(journal,Journal.class);
		System.out.println("Inserimento journal.");
		Journals.getInstance().addJournal(new_journal);
		sendNotifyNewJournal(new_journal.getParticipants(), new_journal.getName(), new_journal.getOwnerId());
		//chiamata notifica push
		return Response.ok(journal).build();
	}
	
	@POST
	@Path("/update_journal")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateJournal(String journal){
		Journal update_journal = gson.fromJson(journal,Journal.class);
		ArrayList<Long> participantsJournalBeforeUpdate = (ArrayList<Long>) Journals.getInstance().findJournal(update_journal.getOwnerId(), update_journal.getName()).getParticipants();
		ArrayList<Long> participantsUpdateJournal = (ArrayList<Long>) update_journal.getParticipants();
		List<Long> newPartipantsToSendNotify = new ArrayList<Long>();
		System.out.println("Modifica journal.");
		Journals.getInstance().findJournal(update_journal.getOwnerId(), update_journal.getName()).setParticipants(participantsUpdateJournal);
		
		// verifico quali sono i nuovi utenti a cui mandare la notifica
		for (Long member : participantsUpdateJournal){
			if ((!participantsJournalBeforeUpdate.contains(member)) && (update_journal.getOwnerId() != member)){
				newPartipantsToSendNotify.add(member);
			}
		}
		// manda notifiche
		sendNotifyNewJournal(newPartipantsToSendNotify, update_journal.getName(), update_journal.getOwnerId());
		return Response.ok(journal).build();
	}

	@POST
	@Path("/journal/element/{owner_id}/{name}/{owner_id_element}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response addElement(String element, @PathParam("owner_id") String own_id, @PathParam("name") String name, @PathParam("owner_id_element") String owner_id_element){
		Element new_element = gson.fromJson(element,Element.class);
		System.out.println("Inserimento elemento.");
		Journals.getInstance().addElement(new_element,Long.parseLong(own_id), name);
		//chiamata notifica push
		sendNotifyNewElement(own_id, name,owner_id_element);
		return Response.ok(element).build();
	}

	//salva token nel DB
	@POST
	@Path("/addtoken/{id_facebook}/{id_token}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response addToken(@PathParam("id_facebook") String id_facebook, @PathParam("id_token") String id_token){
		System.out.println("Inserimento token utente.");
		TokenUsers.getInstance().addTokenUser(Long.parseLong(id_facebook), id_token);
		System.out.println("Inserimento completato");
		return Response.ok("Ok").build();
	}


	//manda push a tutti i nuovi partecipanti al journal
	public void sendNotifyNewJournal(List<Long> participants , String journal_name, Long owner_id){
		System.out.println("Start to send notify for new Journal");
		//creazione messaggio di push
		for (Long participant : participants){
			String push_notify = "";
			String id_token = TokenUsers.getInstance().getTokenUser(participant);
			//creazione messaggio di push
			String to = "\"to\": \"" + id_token  + "\"";
			String message_data = "{\"topic\": \"" + owner_id + "_" + journal_name.replace(" ", "_") +"\", \"type\" : \"newJournal\" , \"body\": \"Sei stato aggiunto al journal " + journal_name +
					"!\", \"journal_name\" : \"" + journal_name + "\", \"owner_id\": \"" + owner_id + "\",}";
			String data = "\"data\": " + message_data;
			push_notify = "{" + to + "," + data + "}"; 
			URL url;
			try {
				url = new URL("https://fcm.googleapis.com/fcm/send");
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("POST");
				con.addRequestProperty("Content-Type", "application/json");
				con.addRequestProperty("Authorization", "key=" + key_auth);
				con.setDoInput(true);
				con.setDoOutput(true);
				con.getOutputStream().write(push_notify.getBytes());
				InputStream input = con.getInputStream();
				System.out.println("Response " + input.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Finish to send notify for new Journal");
	}

	//manda messaggi push di nuovo elemento ai partecipanti al journal
	public void sendNotifyNewElement(String owner_id, String journal_name, String owner_id_element){
		System.out.println("Start to send notify for new Element for Journal " + journal_name);
		String push_notify = "";
		//creazione messaggio di push
		String to = "\"to\": \"/topics/" + owner_id + "_" + journal_name.replace(" ", "_") +"\"";
		String message_data = "{\"owner_id_element\": \"" + owner_id_element + "\", \"type\" : \"newElement\",\"body\": \"E' stato aggiunto un nuovo elemento al journal " + journal_name +
				"!\", \"journal_name\" : \"" + journal_name + "\", \"owner_id\": \"" + owner_id + "\",}";
		String data = "\"data\": " + message_data;
		push_notify = "{" + to + "," + data + "}"; 
		URL url;
		try {
			url = new URL("https://fcm.googleapis.com/fcm/send");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.addRequestProperty("Content-Type", "application/json");
			con.addRequestProperty("Authorization", "key=" + key_auth);
			con.setDoInput(true);
			con.setDoOutput(true);
			con.getOutputStream().write(push_notify.getBytes());
			InputStream input = con.getInputStream();
			System.out.println("Response: " + input.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Stop to send notify for new Element for Journal " + journal_name);
	}

}
