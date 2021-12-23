package com.sontme.legacysonty;

import android.location.Location;

public class ApWithLocation {
    public String getMac() {
        if (mac != null) {
            return mac;
        } else {
            return "null";
        }
    }

    public int getRssi() {
        return rssi;
    }

    public Location getLocation() {
        return location;
    }

    public String getSsid() {
        return ssid;
    }

    public double getFrequency() {
        return frequency;
    }

    private String ssid;
    private double frequency;
    private String mac;
    private int rssi;
    private Location location;

    public ApWithLocation(String ssid, double frequency, String mac, int rssi, Location location) {
        this.mac = mac;
        this.rssi = rssi;
        this.location = location;
        this.ssid = ssid;
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "ApWithLocation{" +
                "mac='" + mac + '\'' +
                ", rssi=" + rssi +
                '}';
    }
}
