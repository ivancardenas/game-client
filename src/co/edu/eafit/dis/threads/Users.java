
package co.edu.eafit.dis.threads;

import co.edu.eafit.dis.api.API;
import co.edu.eafit.dis.json.JSON;
import co.edu.eafit.dis.ui.GameListUI;

import java.util.HashMap;

public class Users extends Thread {
    
    public static HashMap<String, String> 
            oldUsers = new HashMap<>();
    public static HashMap<String, String> 
            newUsers = new HashMap<>();
    
    private String user[], name[];
    
    @Override
    public void run() {
        
        try {
            
            while(true) {
                
                oldUsers = new HashMap<>(); 
                fillUsers(oldUsers);
                Thread.sleep(60000);
                newUsers = new HashMap<>(); 
                fillUsers(newUsers);
                
                if (newUser()) {
                    
                    oldUsers = new HashMap<>(newUsers);
                    GameListUI.listOnlineUsers();
                }
            }
            
        } catch(InterruptedException e) {}
    }
    
    private void fillUsers(HashMap<String, String> users) {
        
        String request = API.doGET("users?status=1");
        
        user = JSON.getParameter(request, "user_id");
        name = JSON.getParameter(request, "name");
        
        for (int i = 0; i < user.length; i++) 
            users.put(user[i], name[i]);
    }
    
    /* public static String[] getUsers() {
        
        Iterator<HashMap.Entry<String, String>> entries;
        HashMap.Entry<String, String> entry;
        
        entries = newUsers.entrySet().iterator();
        
        String response[] = new String[newUsers.size() * 2];
        
        for (int i = 0; i < response.length; i++) {
            
            entry = entries.next();
            
            response[i] = entry.getKey();
            response[++i] = entry.getValue();
        }
        
        return response;
    } */
    
    private boolean newUser() {
        
        int oldCount = 0, newCount = 0;
        
        oldCount = oldUsers.keySet().stream().map((item) -> 1)
                .reduce(oldCount, Integer::sum);
        
        newCount = newUsers.keySet().stream().map((item) -> 1)
                .reduce(newCount, Integer::sum);
        
        return oldCount != newCount;
    }
}