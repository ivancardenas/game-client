
package co.edu.eafit.dis.entities;

import co.edu.eafit.dis.api.API;
import co.edu.eafit.dis.json.JSON;
import co.edu.eafit.dis.checksum.SHA1;

public class User {
    
    private static String user = "", name = "";
    private static boolean turn = false;

    // Login validation.
    public static boolean validate(String user, String pass) {

        boolean checkStatus = false;

        String query = API.doGET("users/".concat(user));

        if (API.getResponseGET().getStatus() == 200)
            checkStatus = JSON.getParameter(query, "pass")[0]
                    .equals(SHA1.checksum(pass));

        return checkStatus;
    }

    // Change user status.
    public static boolean status(String path, int status) {

        String json = "{ \"user\": { \"status\": " + status + " } }";
        
        return API.doPUT(path, json) == 204;
    }
    
    // Does the user exist?
    public static boolean exist(String user) {
        
        API.doGET("users/".concat(user));
        
        return API.getResponseGET().getStatus() == 200;
    }
    
    // Create new user.
    public static boolean insert(String name, String user, String pass) {
        
        String json = 
                  "{ \"user\": { \"user_id\": \"" + user + "\", \"name\": \"" + name 
                + "\", \"pass\": \"" + SHA1.checksum(pass) + "\", \"status\": 0 } }";
        
        return API.doPOST("users", json) == 201;
    }
    
    // Users online (users).
    public static String[] onlineUser() {
        
        String response = API.doGET("users?status=1");
        
        // String request[] = new String[0]; // Returning request values.
        
        // if (API.getResponseGET().getStatus() == 200)
            // request = JSON.getParameter(response, "user_id");
        
        return JSON.getParameter(response, "user_id");
    }
    
    // Users online (names).
    public static String[] onlineName() {
        
        String response = API.doGET("users?status=1");
        
        // String request[] = new String[0]; // Returning request values.
        
        // if (API.getResponseGET().getStatus() == 200)
            // request = JSON.getParameter(response, "user_id");
        
        return JSON.getParameter(response, "name");
    }
    
    public static int[] games(String scope, int status) {
        
        String request = API.doGET("games?"
                .concat(scope).concat("=").concat(User.getUser())
                .concat("&status=".concat(Integer.toString(status))));
        
        String getGames[] = JSON.getParameter(request, "game_id");
        
        int games[] = new int[getGames.length];
        
        for (int i = 0; i < getGames.length; i++)
            games[i] = Integer.parseInt(getGames[i]);
        
        return games;
    }
    
    public static String[] games(String scope, String player, int status) {
        
        String request = API.doGET("games?"
                .concat(scope).concat("=").concat(User.getUser())
                .concat("&status=".concat(Integer.toString(status))));
        
        String getPlayers[] = JSON.getParameter(request, player);
        
        String players[] = new String[getPlayers.length];
        
        System.arraycopy(getPlayers, 0, players, 0, getPlayers.length);
        
        return players;
    }
    
    public static boolean online(String user) {
        
        String response = API.doGET("users?user="
                .concat(user).concat("&status=1"));
        
        return response.length() > 2 && 
                API.getResponseGET().getStatus() == 200;
    }
    
    public static String getUser() {
        return user;
    }
    
    public static void setUser(String user) {
        User.user = user;
    }
    
    public static String getName() {
        return name;
    }
    
    public static void setName(String name) {
        User.name = name;
    }
    
    public static boolean getTurn() {
        return turn;
    }
    
    public static void setTurn(boolean turn) {
        User.turn = turn;
    }
}