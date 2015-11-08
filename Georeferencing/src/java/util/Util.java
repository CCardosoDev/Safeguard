package util;

import com.es.georeferencingservice.Coordinate;
import exceptions.GeoreferencingCoordinatesException;
import exceptions.GeorefererencingException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author claudia cardoso - 45772
 */
public class Util {

    public static String coordinatesToWKT(List<Coordinate> coordinates) throws GeoreferencingCoordinatesException {

        String wkt = "POLYGON((";
        
        if(coordinates.get(0).getLatitude() != coordinates.get(coordinates.size() - 1).getLatitude()
                || coordinates.get(0).getLongitude()!= coordinates.get(coordinates.size() - 1).getLongitude()){
            throw new GeoreferencingCoordinatesException("The first and last coordinates should be equal");
        }
        int i = 0;
        for(Coordinate coordinate : coordinates){
            if ((coordinate.getLatitude() >= -90.0 && coordinate.getLatitude() <= 90.0)
                    && (coordinate.getLatitude() >= -180.0 && coordinate.getLatitude() <= 180.0)) {
               if(i < coordinates.size() - 1)
                   wkt += coordinate.getLatitude() + " " + coordinate.getLongitude() + ", ";
               else
                   wkt += coordinate.getLatitude() + " " + coordinate.getLongitude();  
            }
            else throw new GeoreferencingCoordinatesException("Out of bound coordinates");
            i++;
        }
        
        wkt += "))";

        return wkt;
    }
    
    public static List<Coordinate> WKTToCoordinates(String geometry) throws GeoreferencingCoordinatesException {
        //"POLYGON((1 2,1 4,8 4,9 1,1 2))"
        List<Coordinate> coordinates = new ArrayList<Coordinate>();
        
        try{
        String coordinatesStrings[] = geometry.substring(9,geometry.length()-2).split(",");
        for (int i = 0; i < coordinatesStrings.length; i++) {
            String coordinateString[] = coordinatesStrings[i].split(" ");
            double latitude = Double.valueOf(coordinateString[0]);
            double longitude = Double.valueOf(coordinateString[1]);
            Coordinate coordinate = new Coordinate();
            coordinate.setLatitude(latitude);
            coordinate.setLongitude(longitude);
            coordinates.add(coordinate);
        }
        }catch(Exception ex){
            throw  new GeoreferencingCoordinatesException("Cood not parse wkt");
        }
        return coordinates;
    }
    
    public static String coordinateToWKT(Coordinate coordinate) throws GeoreferencingCoordinatesException {
        
        if ((coordinate.getLatitude() < -90.0 || coordinate.getLatitude() > 90.0) 
                || (coordinate.getLongitude() < -180.0 || coordinate.getLongitude() > 180.0)){
            throw new GeoreferencingCoordinatesException("The inserted coordinate is invalide");
        }
        return "POINT(" + coordinate.getLatitude() + " " + coordinate.getLongitude() + ")";
    }
    
    public static Coordinate WKTToCoordinate(String geometry){
        //"POINT(9 1)"
        String values[] = geometry.substring(6, geometry.length() - 1).split(" ");
        Coordinate coordinate = new Coordinate();
        coordinate.setLatitude(Double.valueOf(values[0]));
        coordinate.setLongitude(Double.valueOf(values[1]));
        return coordinate;
    }
    
    public static void main(String [] args){
        List<Coordinate> coordinates = new ArrayList<Coordinate>();
        coordinates.add(new Coordinate(1, 2));
        coordinates.add(new Coordinate(1, 9));
        coordinates.add(new Coordinate(8, 4));
        coordinates.add(new Coordinate(7, 1));
        coordinates.add(new Coordinate(1, 2));
        try {
            System.out.println(Util.coordinatesToWKT(coordinates));
        } catch (GeorefererencingException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
