package com.example.trackme.model;

import android.location.Location;

import com.google.protobuf.DescriptorProtos;

public class LiveLocation {
double lat;
double lon;

    public LiveLocation() {
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public LiveLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
