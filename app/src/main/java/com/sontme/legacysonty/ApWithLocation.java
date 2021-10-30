package com.sontme.legacysonty;

import android.location.Location;

public class ApWithLocation {
    public String getMac() {
        return mac;
    }

    public int getRssi() {
        return rssi;
    }

    public Location getLocation() {
        return location;
    }

    public String mac;
    public int rssi;
    public Location location;

    public ApWithLocation(String mac, int rssi, Location location) {
        this.mac = mac;
        this.rssi = rssi;
        this.location = location;
    }

    @Override
    public String toString() {
        return "ApWithLocation{" +
                "mac='" + mac + '\'' +
                ", rssi=" + rssi +
                ", location=" + location +
                '}';
    }
}
