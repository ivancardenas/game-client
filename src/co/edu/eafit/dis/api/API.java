
package co.edu.eafit.dis.api;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;

public class API {

    final private static String URL = setURL();

    private static HttpResponse<String> responseGET;
    private static HttpResponse<String> responsePOST;
    private static HttpResponse<String> responsePUT;

    public static String doGET(String path) {

        try {

            responseGET = Unirest.get(URL + "/" + path).asString();

            return responseGET.getBody();

        } catch (UnirestException e) {
            System.out.println("Unirest exception: " + e);
        }

        return "";
    }

    public static int doPOST(String path, String json) {

        try {

            responsePOST = Unirest.post(URL + "/" + path)
                    .header("content-type", "application/json")
                    .body(json).asString();

        } catch (UnirestException e) {
            System.out.println("Unirest exception: " + e);
        }

        return responsePOST.getStatus();
    }

    public static int doPUT(String path, String json) {

        try {

            responsePUT = Unirest.put(URL + "/" + path)
                    .header("content-type", "application/json")
                    .body(json).asString();
            
        } catch (UnirestException e) {
            System.out.println("Unirest exception: " + e);
        }

        return responsePUT.getStatus();
    }
    
    public static String doPOST(String path, String json, int i) {

        try {

            responsePOST = Unirest.post(URL + "/" + path)
                    .header("content-type", "application/json")
                    .body(json).asString();

        } catch (UnirestException e) {
            System.out.println("Unirest exception: " + e);
        }

        return responsePOST.getBody();
    }

    private static String setURL() {
        return "http://localhost:3000";
    }
    
    
    public static HttpResponse<String> 
        getResponseGET() {
            
        return responseGET;
    }
    
    public static HttpResponse<String> 
        getResponsePOST() {
            
        return responsePOST;
    }
    
    public static HttpResponse<String> 
        getResponsePUT() {
            
        return responsePUT;
    }
}