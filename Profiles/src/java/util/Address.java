package util;

/**
 *
 * @author claudia
 */
public class Address {

    private String streetAddress;
    private String locality;
    private String postCode;
    private String countryName;

    public Address(String streetAddress, String locality, String postCode, String countryName) {
        this.streetAddress = streetAddress;
        this.locality = locality;
        this.postCode = postCode;
        this.countryName = countryName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getLocality() {
        return locality;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getCountryName() {
        return countryName;
    }
}
