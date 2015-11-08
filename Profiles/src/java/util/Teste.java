package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.json.JsonArray;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author claudia
 */
public class Teste {

    public static void main(String args[]) throws JSONException {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("token", "token value");
        ArrayList<String> phones = new ArrayList<>();
        phones.add("phone1");
        phones.add("phone2");
        jsonMap.put("phones", phones);
        ArrayList<Address> addresses = new ArrayList<>();
        Address address = new Address("street", "locality", "postCOde", "country");
        Address address2 = new Address("street2", "locality2", "postCOde2", "country2");
        addresses.add(address);
        addresses.add(address2);
        jsonMap.put("adresses", addresses);
        JSONObject jSONObject = new JSONObject(jsonMap);

        Map<String, Object> jsonMap2 = new HashMap<>();
        jsonMap.put("token", "token value");
        ArrayList<String> phones2 = new ArrayList<>();
        phones2.add("phone1");
        phones2.add("phone2");
        jsonMap2.put("phones", phones2);
        ArrayList<Address> addresses2 = new ArrayList<>();
        Address address3 = new Address("street", "locality", "postCOde", "country");
        Address address4 = new Address("street2", "locality2", "postCOde2", "country2");
        addresses2.add(address3);
        addresses2.add(address4);
        jsonMap2.put("adresses", addresses2);
        JSONObject jSONObject2 = new JSONObject(jsonMap);
        
        ArrayList<JSONObject> jsonArray= new ArrayList<>();
        
        jsonArray.add(jSONObject);
        jsonArray.add(jSONObject2);
        
        JSONArray jSONArray2 = new JSONArray(jsonArray);
        
        System.out.println(jSONArray2.toString(4));
    }
}
