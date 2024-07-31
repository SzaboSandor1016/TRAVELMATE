package com.example.gtk_maps;

import android.util.Log;

import org.osmdroid.util.GeoPoint;

import java.util.Arrays;
import java.util.Locale;

// ----------------------------------------------
// | ManipulateUrl                              |
// | Is for replacing substrings in categories  |
// | That's all                                 |
// ----------------------------------------------

public class ManipulateUrl{

    private String centerUrl,nearbyUrl,addrNameOfPlace, upperAddrNameOfPlace;
    private static final String EMPTY = "";
    public ManipulateUrl(String categories, String lat, String lon, double dist){
        String baseurl = "[out:json];" +
                "(" + categories + ");" +
                "out center;";

        baseurl = baseurl.replace("dist", String.valueOf(dist));
        baseurl = baseurl.replace("startLat", lat);
        baseurl = baseurl.replace("startLong", lon);

        nearbyUrl = "https://www.overpass-api.de/api/interpreter?data=" + baseurl;
    }

    public ManipulateUrl(String nameOfPlace,String city,String street,String houseNumber){
        String baseurl = "[out:json];";
        String upper="nwr";
        String lower="nwr";
        String rest="";
        Log.d("nameOfPlace", nameOfPlace);

        if (!city.equals(EMPTY)){
            rest= rest +"[\'addr:city\'~\"" + city + "\"]";
        }
        if (!street.equals(EMPTY)){
            rest = rest + "[\'addr:street\'~\"" + street + "\"]";
        }
        if (!houseNumber.equals(EMPTY)) {
            rest = rest + "[\'addr:housenumber\'~\"" + houseNumber + "\"]";
        }

        if (!nameOfPlace.equals(EMPTY)){
            String[] splitNameOfPlace = nameOfPlace.split(" ");
            String concat="";
            for (int i=0; i<splitNameOfPlace.length; i++){
                if(splitNameOfPlace[i].length()>4) {
                    splitNameOfPlace[i] = splitNameOfPlace[i].replaceFirst(splitNameOfPlace[i].substring(0, 1), "");
                    concat = concat + " " + splitNameOfPlace[i];
                }else {
                    concat = concat+ " "+ splitNameOfPlace[i];
                }
            }
            concat=concat.trim();
            upperAddrNameOfPlace= concat.replace(" ",".*");
            /*String[] splitNameOfPlace = nameOfPlace.split(" ");
            String concat="";
            for (int i=0; i<splitNameOfPlace.length; i++){
                splitNameOfPlace[i]=splitNameOfPlace[i].replaceFirst(splitNameOfPlace[i].substring(0,1),splitNameOfPlace[i].substring(0,1).toUpperCase());
                concat = concat+" "+ splitNameOfPlace[i];

            }
            concat=concat.trim();
            upperAddrNameOfPlace= concat.replace(" ",".*");*/
            //addrNameOfPlace = nameOfPlace.replace(" ", ".*");

            upper = upper + "[\'name\'~\"" + upperAddrNameOfPlace + "\"]" + rest;
            //lower = lower + "[\'name\'~\"" + addrNameOfPlace + "\"]" + rest;

            baseurl = baseurl + upper + ";" /*+ lower+";"*/;
        }else {
            baseurl = baseurl + "nwr" +rest + ";";
        }
        baseurl= baseurl + "out center;";
        centerUrl = "https://www.overpass-api.de/api/interpreter?data=" + baseurl;
        Log.d("centerUrl", centerUrl);
    }

    public String getCenterUrl() {
        return centerUrl;
    }
    public String getNearbyUrl(){
        return nearbyUrl;
    }
}
