/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package location;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author luis
 */
@XmlRootElement(name = "locationSample")
public class LocationSample {
    
    private double latitude;
    private double longitude;
    private String externalId;

    public LocationSample() {}
    
    public LocationSample(double latitude, double longitude, String deviceId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.externalId = deviceId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
    
    
    
    
}
