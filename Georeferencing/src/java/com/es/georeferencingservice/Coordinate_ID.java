/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.es.georeferencingservice;

/**
 *
 * @author claudia
 */
public class Coordinate_ID {

    private int id;
    private Coordinate coordinate;
    private String created;
    private String modified;

    public Coordinate_ID() {
    }

    public Coordinate_ID(int id, Coordinate coordinate, String created, String modified) {
        this.id = id;
        this.coordinate = coordinate;
        this.created = created;
        this.modified = modified;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
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
