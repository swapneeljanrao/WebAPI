package com.mrcoder.webapi;

import java.io.Serializable;

public class City implements Serializable {

    private String RestoName, City, Address, Latitude, Longitude;

    public City(String restoName, String city) {
        RestoName = restoName;
        City = city;
    }

    public City(String restoName, String city, String address, String latitude, String longitude) {
        RestoName = restoName;
        City = city;
        Address = address;
        Latitude = latitude;
        Longitude = longitude;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getRestoName() {
        return RestoName;
    }

    public void setRestoName(String restoName) {
        RestoName = restoName;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }
}
