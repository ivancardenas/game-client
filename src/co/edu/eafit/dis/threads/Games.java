
// Status = 0: ended, 1: online, 2: invited, 3: waiting.

package co.edu.eafit.dis.threads;

import co.edu.eafit.dis.api.API;
import co.edu.eafit.dis.entities.User;
import co.edu.eafit.dis.json.JSON;
import co.edu.eafit.dis.ui.GameListUI;
import co.edu.eafit.dis.ui.GameUI;

import java.util.HashMap;
import javax.swing.JOptionPane;

public class Games extends Thread {
    
    private HashMap<String, String> waitingGames = new HashMap<>();
    
    private final String scope;
    
    public Games(String scope) { this.scope = scope; }
    
    @Override
    public void run() {
        
        try {
            
            while (true) {
                
                waitingForGame(scope);
                Thread.sleep(5000);
            }
            
        } catch(InterruptedException e) {}
        
    }
    
    private boolean waitingForGame(String scope) {
        
        String response = "", opponent[] = new String[0], 
                gameID[] = new String[0];
        
        if (scope.equals("player")) { // Invitations.
        
            response = API.doGET("games?user="
                    .concat(User.getUser()).concat("&status=3"));
            
            opponent = JSON.getParameter(response, "player");
        
        } else if (scope.equals("user")) { // Online.
            
            response = API.doGET("games?player="
                    .concat(User.getUser()).concat("&status=4"));
            
            opponent = JSON.getParameter(response, "user_id");
        }
        
        if (response.length() > 2) gameID = JSON
                .getParameter(response, "game_id");
        
        
        for (int i = 0; i < gameID.length && response.length() > 2; i++) {
            
            if (!waitingGames.containsKey(gameID[i])) {
                
                waitingGames.put(gameID[i], opponent[i]);
                
                int answer = JOptionPane.showConfirmDialog(null, "Play with '"
                        + opponent[i] + "' now?", "", JOptionPane.YES_NO_OPTION);
                
                if (answer == JOptionPane.YES_OPTION) {
                    
                    API.doPUT("games/".concat(gameID[i]), 
                        "{ \"game\": { \"status\": 1 } }");
                    
                    // Start new game (interface - UI).
                    GameUI newGame = new GameUI(Integer
                            .parseInt(gameID[i]), opponent[i]);
                        newGame.setVisible(true);

                    GameListUI.currentGames.put(Integer
                            .parseInt(gameID[i]), newGame);
                    GameListUI.listCurrentGames(); // List current games.
                
                } else API.doPUT("games/".concat(gameID[i]), 
                        "{ \"game\": { \"status\": 0 } }");
            }
        }
        
        return false;
    }
}