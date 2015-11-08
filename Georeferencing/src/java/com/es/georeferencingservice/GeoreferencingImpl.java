package com.es.georeferencingservice;

import exceptions.GeoreferencingAlreadyExistsException;
import exceptions.GeoreferencingAuthenticationException;
import exceptions.GeoreferencingCoordinatesException;
import exceptions.GeoreferencingEmptyException;
import exceptions.GeoreferencingNotExistsException;
import exceptions.GeoreferencingSQLException;
import exceptions.GeoreferencingUserNotExistsException;
import exceptions.GeorefererencingException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.net.ssl.HttpsURLConnection;
import javax.sql.DataSource;
import org.apache.commons.codec.binary.Base64;
import util.NullHostnameVerifier;
import util.Util;

/**
 *
 * @author claudia cardoso - 45772
 */
@WebService(serviceName = "Georeferencing")
public class GeoreferencingImpl {

    @Resource(name = "gisRef")
    private DataSource gisRef;

    private String authenticateUser(String token) throws GeoreferencingAuthenticationException {

        if (token == null || token.isEmpty()) {
            throw new GeoreferencingAuthenticationException("Empty token.");
        }

        String userID = "";

        try {
            URL url = new URL("https://192.168.215.216:5000/getData?token=" + token);

            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setHostnameVerifier(new NullHostnameVerifier());
            String userPass = "LFGKDLFKGJ:RETWERETYTUYGHN";
            String userPassB64 = new String(Base64.encodeBase64(userPass.getBytes()));
            con.setRequestProperty("Authorization", "Basic " + userPassB64);
            con.setUseCaches(false); // ignore caches
            con.setDoInput(true); // defaults to true, but doesn't hurt
            con.connect(); // connect to the server

            InputStream is;
            if (con.getResponseCode() == 200) {
                is = con.getInputStream();
            } else {
                throw new GeoreferencingAuthenticationException("It was not possible to get the id");
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

            if (userID.isEmpty()) {
                throw new GeoreferencingAuthenticationException("It was not possible to get the id");
            }



        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
            throw new GeoreferencingAuthenticationException("It was not possible to get the id");
        }

        return userID;

    }

    @WebMethod
    @RolesAllowed("USER")
    public int insertFence(@WebParam(name = "token") String token,
            @WebParam(name = "childUserID") String childUserID,
            @WebParam(name = "coordinates") List<Coordinate> coordinates)
            throws GeoreferencingEmptyException, GeoreferencingCoordinatesException, GeoreferencingAlreadyExistsException, GeoreferencingSQLException, GeoreferencingAuthenticationException, GeoreferencingUserNotExistsException, IOException {
        if (coordinates == null || coordinates.isEmpty()) {
            throw new GeoreferencingEmptyException("It cant be an empty fence");
        }


        String parentUserID = this.authenticateUser(token);

        int finalResult = -1;
        Connection connection = null;
        ResultSet result = null;
        PreparedStatement statement = null;
        try {
            connection = gisRef.getConnection();
            String wkt = Util.coordinatesToWKT(coordinates);
            String query = "SELECT insertFence(?, ?, ?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, parentUserID);
            statement.setString(2, childUserID);
            statement.setString(3, wkt);
            result = statement.executeQuery();
            result.next();
            finalResult = result.getInt(1);
            if (finalResult == 0) {
                throw new GeoreferencingAlreadyExistsException("The fence already exists");
            } else if (finalResult == -1) {
                throw new GeoreferencingUserNotExistsException("Parante or child user does not exist");
            }
        } catch (SQLException ex) {
            System.out.println("It was not possible insert the fence");
            throw new GeoreferencingSQLException("It was not possible insert the fence");
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                }
            }
        }

        return finalResult;
    }

    @WebMethod
    public void editFence(@WebParam(name = "token") String token,
            @WebParam(name = "childUserID") String childUserID,
            @WebParam(name = "fenceID") int fenceID,
            @WebParam(name = "coordinates") List<Coordinate> coordinates)
            throws GeoreferencingEmptyException, GeoreferencingCoordinatesException, GeoreferencingNotExistsException, GeoreferencingSQLException, GeoreferencingAuthenticationException, GeoreferencingUserNotExistsException {
        if (coordinates == null || coordinates.isEmpty()) {
            throw new GeoreferencingEmptyException("It cant be an empty fence");
        }

        String parentUserID = this.authenticateUser(token);
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            String wkt = Util.coordinatesToWKT(coordinates);
            connection = gisRef.getConnection();
            String query = "SELECT editFence(?, ?, ?, ?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, parentUserID);
            statement.setString(2, childUserID);
            statement.setInt(3, fenceID);
            statement.setString(4, wkt);
            result = statement.executeQuery();
            result.next();
            int finalResult = result.getInt(1);
            if (finalResult == 0) {
                throw new GeoreferencingNotExistsException("There is no fance with id " + fenceID + ".");
            } else if (finalResult == -1) {
                throw new GeoreferencingUserNotExistsException("Parante or child user does not exist");
            }
        } catch (SQLException ex) {
            throw new GeoreferencingSQLException("It was not possible edit fence");
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                }
            }
        }


    }

    @WebMethod
    public void removeFence(@WebParam(name = "token") String token,
            @WebParam(name = "childUserID") String childUserID,
            @WebParam(name = "fenceID") int fenceID)
            throws GeoreferencingNotExistsException, GeoreferencingSQLException, GeoreferencingAuthenticationException, GeoreferencingUserNotExistsException {
        Connection connection = null;
        String parentUserID = this.authenticateUser(token);
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            connection = gisRef.getConnection();
            String query = "SELECT deleteFence(?, ?, ?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, parentUserID);
            statement.setString(2, childUserID);
            statement.setInt(3, fenceID);
            result = statement.executeQuery();
            result.next();
            int finalResult = result.getInt(1);
            if (finalResult == 0) {
                throw new GeoreferencingNotExistsException("There is no fance with id " + fenceID + ".");
            } else if (finalResult == -1) {
                throw new GeoreferencingUserNotExistsException("Parante or child user does not exist");
            }
        } catch (SQLException ex) {
            throw new GeoreferencingSQLException("It was not possible delete the " + fenceID + " fence.");
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    @WebMethod
    public List<Fence_ID> getFences(@WebParam(name = "token") String token,
            @WebParam(name = "childUserID") String childUserID)
            throws GeoreferencingSQLException, GeoreferencingAuthenticationException {
        Connection connection = null;
        List<Fence_ID> fences = new ArrayList<>();
        String parentUserID = this.authenticateUser(token);
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            connection = gisRef.getConnection();
            String query = "SELECT * FROM getFences(?, ?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, parentUserID);
            statement.setString(2, childUserID);
            result = statement.executeQuery();
            while (result.next()) {
                int id_fence = result.getInt("id_fence");
                String geometry = result.getString("geometry");
                Timestamp created = result.getTimestamp("created");
                Timestamp modified = result.getTimestamp("modified");
                List<Coordinate> coordinates = Util.WKTToCoordinates(geometry);
                fences.add(new Fence_ID(id_fence, coordinates, created.toString(), modified.toString()));
            }
            if (fences.size() > 0) {
                fences.add(new Fence_ID(-1 , null, null,null));
            }
        } catch (SQLException | GeoreferencingCoordinatesException ex) {
            throw new GeoreferencingSQLException("Can't query DB.");
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                }
            }
        }
        return fences;
    }

    @WebMethod
    public List<Fence_ID> fencesContainingCoordinate(@WebParam(name = "token") String token,
            @WebParam(name = "childUserID") String childUserID,
            @WebParam(name = "coordinate") Coordinate coordinate)
            throws GeoreferencingCoordinatesException, GeorefererencingException {
        Connection connection = null;
        List<Fence_ID> fences = new ArrayList<>();
        String parentUserID = this.authenticateUser(token);
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            String wkt = Util.coordinateToWKT(coordinate);
            connection = gisRef.getConnection();
            String query = "SELECT * FROM fencesContainingCoordinate(?, ?, ?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, parentUserID);
            statement.setString(2, childUserID);
            statement.setString(3, wkt);
            result = statement.executeQuery();
            while (result.next()) {
                int id_fence = result.getInt("id_fence");
                String geometry = result.getString("geometry");
                Timestamp created = result.getTimestamp("created");
                Timestamp modified = result.getTimestamp("modified");
                List<Coordinate> coordinates = Util.WKTToCoordinates(geometry);
                fences.add(new Fence_ID(id_fence, coordinates, created.toString(), modified.toString()));
            }
        } catch (SQLException ex) {
            throw new GeorefererencingException("Can't query DB.");
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                }
            }
        }
        return fences;
    }

    @WebMethod
    public int insertCoordinate(@WebParam(name = "token") String token,
            @WebParam(name = "childUserID") String childUserID,
            @WebParam(name = "coordinate") Coordinate coordinate)
            throws GeoreferencingEmptyException, GeoreferencingCoordinatesException, GeoreferencingSQLException, GeoreferencingAuthenticationException, GeoreferencingUserNotExistsException {
        System.out.println("Insert Coordinate");
        if (coordinate == null) {
            throw new GeoreferencingEmptyException("It cant be an empty point");
        }
        String parentUserID = this.authenticateUser(token);
        int finalResult = -1;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            String wkt = Util.coordinateToWKT(coordinate);
            connection = gisRef.getConnection();
            String query = "SELECT insertCoordinate(?, ?, ?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, parentUserID);
            statement.setString(2, childUserID);
            statement.setString(3, wkt);
            result = statement.executeQuery();
            result.next();
            finalResult = result.getInt(1);
            if (finalResult == -1) {
                throw new GeoreferencingUserNotExistsException("Parante or child user does not exist");
            }
        } catch (SQLException ex) {
            throw new GeoreferencingSQLException("It was not possible insert the coordinate");
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                }
            }
        }
        return finalResult;
    }

    @WebMethod
    public void editCoordinate(@WebParam(name = "token") String token,
            @WebParam(name = "childUserID") String childUserID,
            @WebParam(name = "coordinateID") int coordinateID,
            @WebParam(name = "coordinate") Coordinate coordinate)
            throws GeoreferencingEmptyException, GeoreferencingCoordinatesException, GeoreferencingNotExistsException, GeoreferencingSQLException, GeoreferencingAuthenticationException, GeoreferencingUserNotExistsException {
        if (coordinate == null) {
            throw new GeoreferencingEmptyException("It cant be an empty coordinate");
        }
        String parentUserID = this.authenticateUser(token);
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            String wkt = Util.coordinateToWKT(coordinate);
            connection = gisRef.getConnection();
            String query = "SELECT editCoordinate(?, ?, ?, ?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, parentUserID);
            statement.setString(2, childUserID);
            statement.setInt(3, coordinateID);
            statement.setString(4, wkt);
            result = statement.executeQuery();
            result.next();
            int finalResult = result.getInt(1);
            if (finalResult == 0) {
                throw new GeoreferencingNotExistsException("There is no coordinate with id " + coordinateID + ".");
            } else if (finalResult == -1) {
                throw new GeoreferencingUserNotExistsException("Parante or child user does not exist");
            }
        } catch (SQLException ex) {
            throw new GeoreferencingSQLException("It was not possible edit coordinate");
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    @WebMethod
    public void removeCoordinate(@WebParam(name = "token") String token,
            @WebParam(name = "childUserID") String childUserID,
            @WebParam(name = "coordinate") int coordinateID)
            throws GeoreferencingNotExistsException, GeoreferencingSQLException, GeoreferencingAuthenticationException, GeoreferencingUserNotExistsException {
        Connection connection = null;
        String parentUserID = this.authenticateUser(token);
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            connection = gisRef.getConnection();
            String query = "SELECT deleteCoordinate(?, ?, ?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, parentUserID);
            statement.setString(2, childUserID);
            statement.setInt(3, coordinateID);
            result = statement.executeQuery();
            result.next();
            int finalResult = result.getInt(1);
            if (finalResult == 0) {
                throw new GeoreferencingNotExistsException("There is no coordinate with id " + coordinateID + ".");
            } else if (finalResult == -1) {
                throw new GeoreferencingUserNotExistsException("Parante or child user does not exist");
            }
        } catch (SQLException ex) {
            throw new GeoreferencingSQLException("It was not possible delete the " + coordinateID + " coordinate.");
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                }
            }
        }

    }

    @WebMethod
    public List<Coordinate_ID> getCompleteCoordinateHistory(@WebParam(name = "token") String token,
            @WebParam(name = "childUserID") String childUserID)
            throws GeoreferencingSQLException, GeoreferencingAuthenticationException {
        Connection connection = null;
        List<Coordinate_ID> coordinates = new ArrayList<>();
        String parentUserID = this.authenticateUser(token);
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            connection = gisRef.getConnection();
            String query = "SELECT * FROM getCoordinates(?, ?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, parentUserID);
            statement.setString(2, childUserID);
            result = statement.executeQuery();
            while (result.next()) {
                int id_coordinate = result.getInt("id_coordinate");
                String geometry = result.getString("geometry");
                Timestamp created = result.getTimestamp("created");
                Timestamp modified = result.getTimestamp("modified");
                Coordinate coordinate = Util.WKTToCoordinate(geometry);
                coordinates.add(new Coordinate_ID(id_coordinate, coordinate, created.toString(), modified.toString()));
            }
        } catch (SQLException ex) {
            throw new GeoreferencingSQLException("Can't query DB.");
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                }
            }
        }
        return coordinates;
    }

    @WebMethod
    public List<Coordinate_ID> getCoordinateHistory(@WebParam(name = "token") String token,
            @WebParam(name = "childUserID") String childUserID,
            @WebParam(name = "startTime") String startTime,
            @WebParam(name = "endTime") String endTime)
            throws GeoreferencingSQLException, GeoreferencingAuthenticationException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        String parentUserID = this.authenticateUser(token);
        List<Coordinate_ID> coordinates = new ArrayList<>();
        try {
            connection = gisRef.getConnection();
            String query = "SELECT * FROM getCoordinatesDate(?, ?, ?, ?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, parentUserID);
            statement.setString(2, childUserID);
            statement.setTimestamp(3, Timestamp.valueOf(startTime));
            statement.setTimestamp(4, Timestamp.valueOf(endTime));
            result = statement.executeQuery();
            while (result.next()) {
                int id_coordinate = result.getInt("id_coordinate");
                String geometry = result.getString("geometry");
                Timestamp created = result.getTimestamp("created");
                Timestamp modified = result.getTimestamp("modified");
                Coordinate coordinate = Util.WKTToCoordinate(geometry);
                coordinates.add(new Coordinate_ID(id_coordinate, coordinate, created.toString(), modified.toString()));
            }
        } catch (SQLException ex) {
            throw new GeoreferencingSQLException("Can't query DB.");
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                }
            }
        }
        return coordinates;
    }

    @WebMethod
    public void createUser(@WebParam(name = "token") String token,
            @WebParam(name = "childUserID") String childUserID)
            throws GeoreferencingAuthenticationException, GeoreferencingSQLException, GeoreferencingAlreadyExistsException {
        Connection connection = null;
        String parentUserID = this.authenticateUser(token);
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            connection = gisRef.getConnection();
            String query = "SELECT createUser(?, ?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, parentUserID);
            statement.setString(2, childUserID);
            result = statement.executeQuery();
            result.next();
            if (!result.getBoolean(1)) {
                throw new GeoreferencingAlreadyExistsException("This parent and child user already exist");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new GeoreferencingSQLException("It was not possible to create the user");
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    @WebMethod
    public void removeUser(@WebParam(name = "token") String token,
            @WebParam(name = "childUserID") String childUserID)
            throws GeoreferencingAuthenticationException, GeoreferencingSQLException, GeoreferencingUserNotExistsException {
        Connection connection = null;
        this.authenticateUser(token);
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            connection = gisRef.getConnection();
            String query = "SELECT removeUser(?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, childUserID);
            result = statement.executeQuery();
            result.next();
            if (!result.getBoolean(1)) {
                throw new GeoreferencingUserNotExistsException("This user does not exist");
            }
        } catch (SQLException ex) {
            throw new GeoreferencingSQLException("It was not possible to delete the parent user");
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                }
            }
        }
    }
}
