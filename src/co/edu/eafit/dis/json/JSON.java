
package co.edu.eafit.dis.json;

import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import org.json.*;

public class JSON {

    public static String[] getParameter(String json, String parameter) {

        HashMap<String, List<String>> request = decode(json);
        Iterator<HashMap.Entry<String, List<String>>> entries;
        
        entries = request.entrySet().iterator();
        
        String response[] = new String[0];
        
        while (entries.hasNext()) {
            
            HashMap.Entry<String, List<String>> 
                    entry = entries.next();
            
            if (entry.getKey().equals(parameter)) {
                
                response = new String[entry.getValue().size()];
                
                for (int i = 0; i < entry.getValue().size(); i++) 
                    response[i] = entry.getValue().get(i);
            }
            
        }
        
        return response;
    }

    private static HashMap<String, List<String>> decode(String json) {

        HashMap<String, List<String>> response = new HashMap<>();

        if (isJSONArray(json) && json.length() > 2) {

            JSONArray jsonArray = new JSONArray(json);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            
            Iterator<?> iterator = jsonObject.keys();

            for (int i = 0; i < jsonObject.length(); i++) {

                ArrayList<String> values = new ArrayList<>();
                String key = (String) iterator.next();
                
                for (int j = 0; j < jsonArray.length(); j++)
                    values.add(jsonArray.getJSONObject(j)
                          .get(key).toString());
                
                response.put(key, values);
            }

        } else if (isJSONObject(json) && json.length() > 2) {

            JSONObject jsonObject = new JSONObject(json);

            Iterator<?> iterator = jsonObject.keys();

            for (int i = 0; i < jsonObject.length(); i++) {
                
                ArrayList<String> values = new ArrayList<>();
                
                String key = (String) iterator.next();
                String value = jsonObject.get(key).toString();
                
                values.add(value); response.put(key, values);
            }
        }

        return response;
    }

    private static boolean isJSONArray(String json) {

        char contentType[] = { json.charAt(0),
            json.charAt(json.length() - 1) };

        return String.valueOf(contentType).equals("[]");
    }

    private static boolean isJSONObject(String json) {

        char contentType[] = { json.charAt(0),
            json.charAt(json.length() - 1) };

        return String.valueOf(contentType).equals("{}");
    }
}