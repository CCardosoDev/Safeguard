package com.es.georeferencingservice;

import java.util.List;

/**
 *
 * @author claudia cardoso
 */
public class Fence_ID {

    private int id;
    private List <Coordinate> coordinates;
    private String created;
    private String modified;

    public Fence_ID() {
    }

    public Fence_ID(int id, List <Coordinate> coordinates, String created, String modified) {
        this.coordinates = coordinates;
        this.id = id;
        this.created = created;
        this.modified = modified;
    }

    public List <Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List <Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
    

}
