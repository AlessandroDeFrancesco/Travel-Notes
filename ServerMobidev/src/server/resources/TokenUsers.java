package server.resources;

import java.util.HashMap;

public class TokenUsers {

	private HashMap<Long,String> tokenUsers;
	private static TokenUsers instance;
	
	private TokenUsers(){
		tokenUsers = new HashMap<>();
	}
	
	public synchronized static TokenUsers getInstance(){
		if(instance == null){
			instance = new TokenUsers();
		}
		return instance;
	}
	
	public synchronized void addTokenUser(Long id_user, String tokenString){
		tokenUsers.put(id_user, tokenString);
	}
	
	public synchronized String getTokenUser(Long id_user){
		return tokenUsers.get(id_user);
	}
	
}