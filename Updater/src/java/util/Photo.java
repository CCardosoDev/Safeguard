package util;

/**
 *
 * @author claudia
 */
public class Photo {
    
    private String photoID;
    private String link;

    public Photo(String photoID, String link) {
        this.photoID = photoID;
        this.link = link;
    }

    public String getPhotoID() {
        return photoID;
    }

    public String getLink() {
        return link;
    }
    
    
    
}
