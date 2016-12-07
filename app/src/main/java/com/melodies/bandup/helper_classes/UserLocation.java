package com.melodies.bandup.helper_classes;

import java.io.Serializable;

/**
 * Created by Bergthor on 7.12.2016.
 */

public class UserLocation implements Serializable  {
    public UserLocation() {
        this.lat = 0;
        this.lon = 0;
        this.valid = false;
    }

    public void setLatitude(double lat) {
        this.lat = lat;
    }

    public void setLongitude(double lon) {
        this.lon = lon;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lon;
    }

    public Boolean getValid() {
        return valid;
    }

    double lat;
    double lon;
    Boolean valid;
}
