package com.example.gtk_maps;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class VolleyRequests extends AppCompatActivity {

    private Context context;
    private final Resources resources;
    private final RequestQueue requestQueue;
    private final ArrayList<String> matchCoordinates, matchLabels;
    private static String[] usefulTags= {"name","cuisine","opening_hours","charge"};
    private static String[] toReturn= {"placeNames","cuisines","openingHours","charges"};

    private final CategoryManager categoryManager = new CategoryManager();
    public VolleyRequests(RequestQueue requestQueue, Context context) {
            this.requestQueue = requestQueue;
            resources = context.getResources();
            this.context= context;
            matchCoordinates = new ArrayList<>();
            matchLabels = new ArrayList<>();
        }
    // if the searchByCurrentPositionSW is not switched a search is made with the name of the starting point
    // provided through the placeET EditText field
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF searching by the name of the starting point
    //----------------------------------------------------------------------------------------------------------------
    public void makeCenterRequest(String url, final CenterVolleyCallback callback) {

        JsonObjectRequest jsonObjectRequestPlaceName = new JsonObjectRequest(
            Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("onResponse", String.valueOf(response));
                    try {

                        JSONArray elements = response.getJSONArray("elements");
                        for (int i = 0; i < elements.length(); i++) {
                            JSONObject element = elements.getJSONObject(i);
                            String type= element.getString("type");
                            JSONObject tags = element.getJSONObject("tags");
                            if (type.equals("way") || type.equals("relation")){
                                JSONObject center = element.getJSONObject("center");
                                double lat = center.getDouble("lat");
                                double lon = center.getDouble("lon");
                                String latlon = String.valueOf(lat) + "," + String.valueOf(lon);
                                matchCoordinates.add(latlon);
                                dataManipulation(tags);
                            }else {
                                double lat = element.getDouble("lat");
                                double lon = element.getDouble("lon");
                                String latlon = String.valueOf(lat) + "," + String.valueOf(lon);
                                matchCoordinates.add(latlon);
                                dataManipulation(tags);
                            }

                        }

                        callback.onSuccess(matchCoordinates, matchLabels);
                        matchCoordinates.clear();
                        matchLabels.clear();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onError(error.toString());
                }
            });
            // You need to set a timeout to avoid problems
            jsonObjectRequestPlaceName.setRetryPolicy(new DefaultRetryPolicy(
                    300000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(jsonObjectRequestPlaceName);

    }
    //----------------------------------------------------------------------------------------------------------------
    //END OF searching by the name of the starting point
    //----------------------------------------------------------------------------------------------------------------

    private void dataManipulation(JSONObject tags) throws JSONException {
        String name;
        String address= "";
        if (tags.has("name")){
            name= tags.getString("name")+ ", ";
        }else{
            name = resources.getString(R.string.unknown) + ", ";
        }
        if (tags.has("addr:city")){
            address +=  tags.getString("addr:city") + " ";
        }
        if (tags.has("addr:street")){
            address += tags.getString("addr:street")+" ";
        }
        if (tags.has("addr:housenumber")){
            address += tags.getString("addr:housenumber");
        }
        matchLabels.add(name + address);
    }

    private Map<String, ArrayList<String>> extractUsefulData(JSONArray elements) {

        Map<String, ArrayList<String>> usefulMap = null;
        try {
            // Iterate through the array to get each set of coordinates
            // If the type is "way" or "relation", that part of the response is handled in a different way
            // because it means that that is a set of values stored in the OSM's database
            // but we need only the coordinates of the center
            //----------------------------------------------------------------------------------------------------------------
            //BEGIN
            //----------------------------------------------------------------------------------------------------------------
            usefulMap = new HashMap<>();

            for (int j=0; j< usefulTags.length; j++) {
                ArrayList<String> tagArray = new ArrayList<>();
                for (int i = 0; i < elements.length(); i++) {
                    JSONObject element = elements.getJSONObject(i);
                    JSONObject tags = element.getJSONObject("tags");

                    if (tags.has(usefulTags[j])) {
                        tagArray.add(tags.getString(usefulTags[j]));
                    } else {
                        if (usefulTags[j].equals("name"))
                            tagArray.add(resources.getString(R.string.unknown));
                        else tagArray.add("unknown");
                    }
                }
                usefulMap.put(toReturn[j], tagArray);
            }
            //----------------------------------------------------------------------------------------------------------------
            //END
            //----------------------------------------------------------------------------------------------------------------
        } catch (JSONException ignored) {

        }
        return usefulMap;
    }

    // Send a request to the Overpass API
    // the parameter is a url accepted by Overpass
    // returns a response in json that handled and forwarded to the main activity as a String array
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF findNearbyPlacesRequest
    //----------------------------------------------------------------------------------------------------------------
    public void findNearbyPlacesRequest(String start,String url, final NearbyVolleyCallback callback) {

        JsonObjectRequest jsonObjectRequestNearby = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Map<String, ArrayList<String>> extractedMap;
                try {
                    ArrayList<String> coordinates = new ArrayList<>();
                    coordinates.add(start);
                    ArrayList<String> coordinateTags = new ArrayList<>();
                    extractedMap = new HashMap<>();

                    JSONArray elements = response.getJSONArray("elements");
                    for (int i = 0; i < elements.length(); i++) {
                        JSONObject element = elements.getJSONObject(i);
                        String type = element.getString("type");
                        JSONObject tags = element.getJSONObject("tags");
                        if (type.equals("way") || type.equals("relation")) {
                            JSONObject center = element.getJSONObject("center");

                            double lat = center.getDouble("lat");
                            double lon = center.getDouble("lon");
                            coordinates.add(String.valueOf(lat) + "," + String.valueOf(lon));
                        } else {
                            double lat = element.getDouble("lat");
                            double lon = element.getDouble("lon");
                            coordinates.add(String.valueOf(lat) + "," + String.valueOf(lon));


                        }
                        String category = categoryManager.getCategoryFromTags(tags);
                        coordinateTags.add(category);
                    }

                    Map<String, ArrayList<String>> tagsMap = extractUsefulData(elements);
                    extractedMap.put("coordinates", coordinates);
                    extractedMap.put("categories", coordinateTags);
                    extractedMap.putAll(tagsMap);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                callback.onSuccess(extractedMap);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error.toString());
                    }
                }

        );

        jsonObjectRequestNearby.setRetryPolicy(new DefaultRetryPolicy(
                300000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequestNearby);

    }
    //----------------------------------------------------------------------------------------------------------------
    //END OF findNearbyPlacesRequest
    //----------------------------------------------------------------------------------------------------------------

    //Interface for returning the response
    // to the calling class
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF callbacks
    //----------------------------------------------------------------------------------------------------------------
    public interface CenterVolleyCallback {
        void onSuccess(ArrayList<String> matchCoordinatesResult, ArrayList<String> matchLabelsResult);
        void onError(String error);
        }
    public interface NearbyVolleyCallback {
        void onSuccess(Map<String,ArrayList<String>> extractedMap);
        void onError(String error);
    }
    //----------------------------------------------------------------------------------------------------------------
    //END OF callbacks
    //----------------------------------------------------------------------------------------------------------------

    public void clearAllVolleyRequest(){
        matchCoordinates.clear();
        matchLabels.clear();
        resources.flushLayoutCache();
    }
}

