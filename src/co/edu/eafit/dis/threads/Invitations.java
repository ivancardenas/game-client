
package co.edu.eafit.dis.threads;

import co.edu.eafit.dis.api.API;
import co.edu.eafit.dis.json.JSON;
import co.edu.eafit.dis.entities.User;
import co.edu.eafit.dis.ui.GameListUI;

import java.util.HashMap;

public class Invitations extends Thread {

    private HashMap<Integer, String> oldInvitations = new HashMap<>();
    private HashMap<Integer, String> newInvitations = new HashMap<>();

    private String game[], oppt[];

    private final String scope;

    public Invitations(String scope) { this.scope = scope; }

    @Override
    public void run() {

        try {

            while (true) {

                oldInvitations = new HashMap<>();
                fillInvitations(oldInvitations, scope);
                
                Thread.sleep(60000);
                
                newInvitations = new HashMap<>();
                fillInvitations(newInvitations, scope);

                if (newInvitation()) {

                    oldInvitations = new HashMap<>(newInvitations);
                    
                    if (scope.equals("user")) GameListUI
                            .listSentInvitations();
                    if (scope.equals("player")) GameListUI
                            .listReceivedInvitations();
                }
            }

        } catch (InterruptedException e) {}
    }

    private void fillInvitations(HashMap<Integer, String> 
            invitations, String scope) {

        switch (scope) {
            
            case "user":
                
                String user = API.doGET("games?user="
                        .concat(User.getUser()).concat("&status=2"));

                game = JSON.getParameter(user, "game_id");
                oppt = JSON.getParameter(user, "player");

                for (int i = 0; i < game.length; i++)
                    invitations.put(Integer.parseInt(game[i]), oppt[i]);
                
                break;
            
            case "player":
                
                String player = API.doGET("games?player="
                        .concat(User.getUser()).concat("&status=2"));

                game = JSON.getParameter(player, "game_id");
                oppt = JSON.getParameter(player, "user_id");

                for (int i = 0; i < game.length; i++)
                    invitations.put(Integer.parseInt(game[i]), oppt[i]);
                
                break;
        }
    }

    /* public static String[] getInvitations() {
        
        Iterator<HashMap.Entry<Integer, String>> entries;
        HashMap.Entry<Integer, String> entry;
        
        entries = newInvitations.entrySet().iterator();
        
        String response[] = new String[newInvitations.size() * 2];
        
        for (int i = 0; i < response.length; i++) {
            
            entry = entries.next();
            
            response[i] = entry.getKey().toString();
            response[++i] = entry.getValue();
        }
        
        return response;
    } */
    
    private boolean newInvitation() {

        int oldCount = 0, newCount = 0;

        oldCount = oldInvitations.keySet().stream().map((item) -> 1)
                .reduce(oldCount, Integer::sum);

        newCount = newInvitations.keySet().stream().map((item) -> 1)
                .reduce(newCount, Integer::sum);

        return oldCount != newCount;
    }
}