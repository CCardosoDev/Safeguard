package util;

import api.NullHostnameVerifier;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author claudia
 */
public class Authentication {

    public static IDEmail authenticate(HttpServletResponse response, String token) throws IOException {
        String userID;
        String email;
        IDEmail iDEmail;

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
            BufferedReader bufferedReader;
            if (con.getResponseCode() == 200) {
                is = con.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(is));
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not valid token");
                return null;
            }

            String sentJson = new String();
            JSONObject jsonObject, jsonUserData;

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sentJson += line;
            }
            try {
                jsonObject = new JSONObject(sentJson);
                jsonUserData = jsonObject.getJSONObject("userData");
                userID = jsonUserData.getString("id");
            } catch (JSONException ex) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Problems with sent JSON (id)");
                return null;
            }

            if (userID == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "UserID not Valid");
                return null;
            }

            try {
                email = jsonUserData.getString("email");
            } catch (JSONException ex) {
                email = null;

            }

            iDEmail = new IDEmail(userID, email);


        } catch (MalformedURLException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Malformed URL");
            return null;
        } catch (IOException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Token not Valid");
            return null;
        }

        return iDEmail;

    }

    public static String authenticateID(HttpServletResponse response, String token) throws IOException {
        String userID;
        String email;
        IDEmail iDEmail;

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
            BufferedReader bufferedReader;
            if (con.getResponseCode() == 200) {
                is = con.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(is));
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not valid token");
                return null;
            }

            String sentJson = new String();
            JSONObject jsonObject, jsonUserData;

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sentJson += line;
            }
            try {
                jsonObject = new JSONObject(sentJson);
                jsonUserData = jsonObject.getJSONObject("userData");
                userID = jsonUserData.getString("id");
            } catch (JSONException ex) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Problems with sent JSON (id)");
                return null;
            }

            if (userID == null) {
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

    public static String getFullName(HttpServletResponse response, String token, int profileID) throws IOException {

        String fullName = null;

        try {
            URL url = new URL("https://127.0.0.1:8181/PofilesService/GetProfiles?token=" + token);

            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setHostnameVerifier(new NullHostnameVerifier());
            con.setUseCaches(false); // ignore caches
            con.setDoInput(true); // defaults to true, but doesn't hurt
            con.connect(); // connect to the server

            InputStream is;
            BufferedReader bufferedReader;
            if (con.getResponseCode() == 200) {
                is = con.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(is));
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not valid token to get full name");
                return null;
            }

            String sentJson = new String();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sentJson += line;
            }
            try {

                JSONArray jsonArray = new JSONArray(sentJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (jsonArray.getJSONObject(i).getInt("profileID") == profileID) {
                        fullName = jsonArray.getJSONObject(i).getString("fullName");
                        break;
                    }
                }

            } catch (JSONException ex) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Problems with sent JSON (token) to get full name");
                return null;
            }

        } catch (MalformedURLException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Malformed URL to get full name");
            return null;
        } catch (IOException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Token not Valid to get full name");
            return null;
        }

        return fullName;

    }
}
