package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author claudia
 */
public class Authentication {

    public static String authenticate(HttpServletResponse response, String token) throws IOException {
        String userID = null;

        try {
            URL url = new URL("https://192.168.215.216:5000/getData?token=" + token);

            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setHostnameVerifier(new NullHostnameVerifier());
            String userPass = "ADSLKDJSCD:ASECSGJKMJHJHJH";
            String userPassB64 = new String(Base64.encodeBase64(userPass.getBytes()));
            con.setRequestProperty("Authorization", "Basic " + userPassB64);
            con.setUseCaches(false); // ignore caches
            con.setDoInput(true); // defaults to true, but doesn't hurt
            con.connect(); // connect to the server

            InputStream is;
            if (con.getResponseCode() == 200) {
                is = con.getInputStream();
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not valid token");
                return null;
            }

            JsonParser parser = Json.createParser(is);
            while (parser.hasNext()) {
                JsonParser.Event e = parser.next();
                if (e == JsonParser.Event.KEY_NAME) {
                    switch (parser.getString()) {
                        case "id":
                            parser.next();
                            userID = parser.getString();
                            break;

                        default:
                            parser.next();
                    }
                }
            }

            if (userID == null || userID.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "UserID not Valid");
                return null;
            }



        } catch (MalformedURLException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Malformed URL");
            return null;
        } catch (IOException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Token not Valid");
            return null;
        }

        return userID;

    }

    public static String getServerAccess(HttpServletResponse response, String token, String service) throws IOException {

        String serviceToken;

        try {
            URL url = new URL("https://192.168.215.216:5000/getServerAccess?token=" + token + "&service=" + service);

            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setHostnameVerifier(new NullHostnameVerifier());
            String userPass = "ADSLKDJSCD:ASECSGJKMJHJHJH";
            String userPassB64 = new String(Base64.encodeBase64(userPass.getBytes()));
            con.setRequestProperty("Authorization", "Basic " + userPassB64);
            con.setUseCaches(false); // ignore caches
            con.setDoInput(true); // defaults to true, but doesn't hurt
            con.connect(); // connect to the server

            InputStream is;
            BufferedReader bufferedReader;
            if (con.getResponseCode() == 200) {
                is = con.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(is));
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not valid token");
                return null;
            }

            String sentJson = new String();
            JSONObject jsonObject;

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sentJson += line;
            }
            try {
                jsonObject = new JSONObject(sentJson);
                serviceToken = jsonObject.getString("token");
            } catch (JSONException ex) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Problems with sent JSON (token)");
                return null;
            }

            if (serviceToken == null || serviceToken.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "token not valid");
                return null;
            }


        } catch (MalformedURLException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Malformed URL");
            return null;
        } catch (IOException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Token not Valid");
            return null;
        }

        return serviceToken;
    }
}
