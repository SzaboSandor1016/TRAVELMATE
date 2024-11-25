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
    private static final String EMPTY = "";

    public ManipulateUrl(){}
    public String getNearbyUrl(String categories, String lat, String lon, double dist){
        String baseurl = "[out:json];" +
                "(" + categories + ");" +
                "out center;";

        baseurl = baseurl.replace("dist", String.valueOf(dist));
        baseurl = baseurl.replace("startLat", lat);
        baseurl = baseurl.replace("startLong", lon);

        String  nearbyUrl = "https://www.overpass-api.de/api/interpreter?data=" + baseurl;

        return  nearbyUrl;
    }

}
