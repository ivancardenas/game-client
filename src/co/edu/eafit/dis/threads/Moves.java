
package co.edu.eafit.dis.threads;

import co.edu.eafit.dis.api.API;
import co.edu.eafit.dis.json.JSON;
import co.edu.eafit.dis.ui.GameListUI;
import co.edu.eafit.dis.ui.GameUI;
import java.awt.Color;

import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

public class Moves extends Thread {

    private static HashMap<Integer, List<Integer>> 
            oldPoints = new HashMap<>();
    private static HashMap<Integer, List<Integer>> 
            newPoints = new HashMap<>();

    private String moves, countX0[], 
            countY0[], countX1[], countY1[];

    private int gameID[] = fillGameArray();
    
    private GameUI game;

    @Override
    public void run() {
        
        try {
            
            while(true) { 
                
                if (gameID.length > 0) {
                    fillPoints(oldPoints);

                    // System.out.println("Pintar puntos 1");
                    Thread.sleep(1000);

                    fillPoints(newPoints);
                    
                    for (int i = 0; i < gameID.length; i++) {

                        if (newMove(gameID[i])) {
                            
                            oldPoints = new HashMap<>(newPoints);
                            
                            game = GameListUI.currentGames
                                    .get(gameID[i]);
                            game.repaint();
                        }
                    }
                } else { Thread.sleep(1000); gameID = fillGameArray(); }
            }
            
        } catch(InterruptedException e) {}
    }
    
    private void fillPoints(HashMap<Integer, List<Integer>> points) {
        
        for (int i = 0; i < gameID.length; i++) {
                
            ArrayList<Integer> movesPerGame = new ArrayList<>();

            moves = API.doGET("points?game_id=".
                    concat(String.valueOf(gameID[i])));

            countX0 = JSON.getParameter(moves, "x0");
            countY0 = JSON.getParameter(moves, "y0");
            countX1 = JSON.getParameter(moves, "x1");
            countY1 = JSON.getParameter(moves, "y1");

            for (int j = 0; j < countX0.length; j++) {

                movesPerGame.add(Integer.parseInt(countX0[j]));
                movesPerGame.add(Integer.parseInt(countY0[j]));
                movesPerGame.add(Integer.parseInt(countX1[j]));
                movesPerGame.add(Integer.parseInt(countY1[j]));
            }

            points.put(gameID[i], movesPerGame);
        }
    }
    
    public int[] getMoves(int gameID) {
        
        Iterator<HashMap.Entry<Integer, List<Integer>>> entries;
        HashMap.Entry<Integer, List<Integer>> entry;

        entries = newPoints.entrySet().iterator();
        
        int response[] = new int[0];

        while (entries.hasNext()) {

            entry = entries.next();

            if (entry.getKey().equals(gameID)) {
                
                response = new int[entry.getValue().size()];

                for (int i = 0; i < entry.getValue().size(); i++) 
                    response[i] = entry.getValue().get(i);
            }
        }
            
        return response;
    }
    
    private boolean newMove(int gameID) {
        
        Iterator<HashMap.Entry<Integer, List<Integer>>> entries;
        HashMap.Entry<Integer, List<Integer>> entry;
        
        entries = oldPoints.entrySet().iterator();
        
        int oldCount = 0, newCount = 0;
        
        while (entries.hasNext()) {
            
            entry = entries.next();
            
            if (entry.getKey().equals(gameID)) 
                oldCount = entry.getValue().size();
        }
        
        entries = newPoints.entrySet().iterator();

        while (entries.hasNext()) {
            
            entry = entries.next();

            if (entry.getKey().equals(gameID)) 
                newCount = entry.getValue().size();
        }
        
        return oldCount < newCount;
    }
    
    private int[] fillGameArray() {
        
        Iterator entries = GameListUI.currentGames.entrySet().iterator();
        HashMap.Entry<Integer, GameUI> entry;
        
        int games[] = new int[GameListUI.currentGames.size()];
        
        for (int i = 0; i < GameListUI.currentGames.size(); i++) {
            
            entry = (HashMap.Entry) entries.next();
            games[i] = entry.getKey();
        }
        
        return games;
    }
}