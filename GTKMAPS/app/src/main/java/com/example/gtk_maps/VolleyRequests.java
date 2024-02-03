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

public class VolleyRequests extends AppCompatActivity {

    private Context context;
    private final Resources resources;
    private final RequestQueue requestQueue;
    private final ArrayList<String> nearbyCoords, nearbyCoordsTags, nearbyCoordsNames, matchCoordinates, matchLabels;

    private final CategoryManager categoryManager = new CategoryManager();
    public VolleyRequests(RequestQueue requestQueue, Context context) {
            this.requestQueue = requestQueue;
            resources = context.getResources();
            this.context= context;
            nearbyCoords = new ArrayList<>();
            nearbyCoordsNames =new ArrayList<>();
            nearbyCoordsTags = new ArrayList<>();
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
            name= tags.getString("name");
        }else{
            name = resources.getString(R.string.unknown);
        }
        if (tags.has("addr:city")){
            address += ", "+ tags.getString("addr:city") + " ";
        }
        if (tags.has("addr:street")){
            address += tags.getString("addr:street")+" ";
        }
        if (tags.has("addr:housenumber")){
            address += tags.getString("addr:housenumber");
        }
        matchLabels.add(name + address);
    }

    // Send a request to the Overpass API
    // the parameter is a url accepted by Overpass
    // returns a response in json that handled and forwarded to the main activity as a String array
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF findNearbyPlacesRequest
    //----------------------------------------------------------------------------------------------------------------
    public void findNearbyPlacesRequest(String start,String url, final NearbyVolleyCallback callback) {
        if (nearbyCoords.size()<1) {
            nearbyCoords.add(start);
        }
        JsonObjectRequest jsonObjectRequestNearby = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray elements = response.getJSONArray("elements");

                    // Iterate through the array to get each set of coordinates
                    // If the type is "way" or "relation", that part of the response is handled in a different way
                    // because it means that that is a set of values stored in the OSM's database
                    // but we need only the coordinates of the center
                    //----------------------------------------------------------------------------------------------------------------
                    //BEGIN
                    //----------------------------------------------------------------------------------------------------------------
                    for (int i = 0; i < elements.length(); i++) {
                        JSONObject element = elements.getJSONObject(i);
                        String type= element.getString("type");
                        JSONObject tags = element.getJSONObject("tags");
                        if (type.equals("way") || type.equals("relation")){
                            JSONObject center = element.getJSONObject("center");

                            double lat = center.getDouble("lat");
                            double lon = center.getDouble("lon");
                            nearbyCoords.add(String.valueOf(lat) + "," + String.valueOf(lon));
                        }else {
                            double lat = element.getDouble("lat");
                            double lon = element.getDouble("lon");
                            nearbyCoords.add(String.valueOf(lat)+"," + String.valueOf(lon));


                        }

                        if (tags.has("name")){
                            nearbyCoordsNames.add(tags.getString("name"));
                        }else{
                            nearbyCoordsNames.add(resources.getString(R.string.unknown));
                        }
                        String category = categoryManager.getCategoryFromTags(tags);
                        nearbyCoordsTags.add(category);
                        Log.d("coords", (String) nearbyCoords.toString());

                    }
                    //----------------------------------------------------------------------------------------------------------------
                    //END
                    //----------------------------------------------------------------------------------------------------------------
                        // convert exportcoords to string array

                        /*String[] stringArray = new String[exportcoords.length];
                        //String[] stringNamesArray = new String[exportCoordsNames.length];
                        String[] stringTagsArray = new String[exportCoordsTags.length];
                        for (int j = 0; j < exportcoords.length; j++) {
                            stringArray[j] = exportcoords[j];
                            //stringNamesArray[j] = exportCoordsNames[j];
                            stringTagsArray[j] = exportCoordsTags[j];
                        }*/
                        Log.d("responsejson", String.valueOf(nearbyCoords));
                        callback.onSuccess(nearbyCoords,nearbyCoordsNames, nearbyCoordsTags);
                } catch (JSONException ignored) {

                }
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
        void onSuccess(ArrayList<String> result, ArrayList<String> namesResult, ArrayList<String> tagsResults);
        void onError(String error);
    }
    //----------------------------------------------------------------------------------------------------------------
    //END OF callbacks
    //----------------------------------------------------------------------------------------------------------------

    public void clearAllVolleyrequest(){
        nearbyCoords.clear();
        nearbyCoordsNames.clear();
        nearbyCoordsTags.clear();
        matchCoordinates.clear();
        matchLabels.clear();
    }
}

