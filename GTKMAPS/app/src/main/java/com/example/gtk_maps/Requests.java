package com.example.gtk_maps;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class Requests extends AppCompatActivity {

    private static final String API_KEY = "5b3ce3597851110001cf624822732185f95b41bbadd3ad38afd95ef0"; // Cseréld le a saját API kulcsodra
    private static final String API_URL = "https://api.openrouteservice.org/v2/directions/";

    private final Context context;
    private final Resources resources;
    private final RequestQueue requestQueue;
    private final PhotonApi photonApi;
    private static final String[] usefulTags= {"name","cuisine","opening_hours","charge"};
    private static final String[] toReturn= {"placeNames","cuisines","openingHours","charges"};

    private final CategoryManager categoryManager = new CategoryManager();
    public Requests(RequestQueue requestQueue, Context context) {
            this.requestQueue = requestQueue;
            resources = context.getResources();
            this.context= context;
            photonApi = retrofit.create(PhotonApi.class);
        }
    public interface PhotonApi {
        @GET("api/")
        Call<PhotonResponse> getAutocomplete(@Query("q") String query, @Query("limit") String limit);
    }
    public interface NearbyVolleyCallback {
        void onResponse(ArrayList<Place> results);
        void onFailure(String error);
    }
    public interface GetAutocompleteCallback {
        void onResponse(retrofit2.Response<PhotonResponse> response);
        void onFailure(Throwable t);
    }
    public interface RouteCallback {
        void onRouteReceived(String result);
        void onRouteFailure();
    }
    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                okhttp3.Request original = chain.request();
                okhttp3.Request request = original.newBuilder()
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0")
                        .header("Accept-Language", "hu-HU,hu;q=0.8,en-US;q=0.5,en;q=0.3")
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            })
            .build();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://photon.komoot.io/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    /*private Map<String, ArrayList<String>> extractUsefulData(JSONArray elements) {

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
    }*/

    public static void getRoute(double startLat, double startLon, double endLat, double endLon, String mode, RouteCallback routeCallback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String result = null;
                try {
                    // A hálózati kérés elvégzése
                    URL url = new URL(API_URL + mode + "?api_key=" + API_KEY +
                            "&start=" + startLon + "," + startLat +
                            "&end=" + endLon + "," + endLat);

                    Log.d("openUrl", String.valueOf(url));
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        InputStream in = urlConnection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder resultStringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            resultStringBuilder.append(line).append("\n");
                        }
                        result = resultStringBuilder.toString();
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                // Ellenőrizd, hogy a result nem null és van-e hossza
                if (result != null && result.length() > 0) {
                    // További feldolgozás
                    if (routeCallback != null) {
                        routeCallback.onRouteReceived(result);
                    } else {
                        // Hibakezelés: RouteCallback null esetén
                        Log.e("OpenRouteServiceAPI", "RouteCallback is null");
                    }
                } else {
                    // Hibakezelés, pl. üres vagy null válasz esetén
                    if (routeCallback != null) {
                        routeCallback.onRouteFailure();
                    } else {
                        // Hibakezelés: RouteCallback null esetén
                        Log.e("OpenRouteServiceAPI", "RouteCallback is null");
                    }
                }
            }
        }.execute();
    }

    // Send a request to the Overpass API
    // the parameter is a url accepted by Overpass
    // returns a response in json that handled and forwarded to the main activity as a String array
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF findNearbyPlacesRequest
    //----------------------------------------------------------------------------------------------------------------
    public void findNearbyPlacesRequest(String url, final NearbyVolleyCallback callback) {

        JsonObjectRequest jsonObjectRequestNearby = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Map<String, ArrayList<String>> extractedMap;
                ArrayList<Place> places = new ArrayList<>();
                try {
                    /*ArrayList<String> coordinates = new ArrayList<>();
                    coordinates.add(start);*/
                    /*ArrayList<String> coordinateTags = new ArrayList<>();
                    extractedMap = new HashMap<>();*/

                    JSONArray elements = response.getJSONArray("elements");
                    for (int i = 0; i < elements.length(); i++) {
                        Place place = new Place();
                        Address address = new Address();
                        JSONObject element = elements.getJSONObject(i);
                        String type = element.getString("type");
                        JSONObject tags = element.getJSONObject("tags");
                        if (type.equals("way") || type.equals("relation")) {
                            JSONObject center = element.getJSONObject("center");

                            double lat = center.getDouble("lat");
                            double lon = center.getDouble("lon");
                            //coordinates.add(String.valueOf(lat) + "," + String.valueOf(lon));

                            place.setCoordinatesWithLatLon(lat,lon);
                        } else {
                            double lat = element.getDouble("lat");
                            double lon = element.getDouble("lon");
                            //coordinates.add(String.valueOf(lat) + "," + String.valueOf(lon));
                            place.setCoordinatesWithLatLon(lat,lon);
                        }
                        String category = categoryManager.getCategoryFromTags(tags);
                        place.setCategory(category);

                        if (tags.has("name:hu")) {
                            place.setName(tags.getString("name:hu"));
                        }else if(tags.has("name:en")){
                            place.setName(tags.getString("name:en"));
                        }else if(tags.has("name")){
                            place.setName(tags.getString("name"));
                        }else {
                            place.setName(resources.getString(R.string.unknown));
                        }

                        if (tags.has("cuisine")) {
                            place.setCuisine((String) tags.get("cuisine"));
                        }else {
                            place.setCuisine("unknown");
                        }
                        if (tags.has("opening_hours")) {
                            place.setOpeningHours((String) tags.get("opening_hours"));
                        }else {
                            place.setOpeningHours("unknown");
                        }

                        if (tags.has("charge")) {
                            place.setCharge((String) tags.get("charge"));
                        }else {
                            place.setCharge("unknown");
                        }
                        if (tags.has("addr:city")){
                            address.setCity(tags.getString("addr:city"));
                        }
                        if (tags.has("addr:street")){
                            address.setStreet(tags.getString("addr:street"));
                        }
                        if (tags.has("addr:housenumber")){
                            address.setHouseNumber(tags.getString("addr:housenumber"));
                        }

                        Log.d("place",place.getOpeningHours());
                        Log.d("place",place.getCuisine());
                        Log.d("place",place.getCharge());
                        place.setAddress(address);
                        places.add(place);
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                callback.onResponse(places);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onFailure(error.toString());
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


    public void getAutocompleteRequest(String s, GetAutocompleteCallback getAutocompleteCallback){

        photonApi.getAutocomplete(s, "5").enqueue(new Callback<PhotonResponse>() {
            @Override
            public void onResponse(@NonNull Call<PhotonResponse> call, @NonNull retrofit2.Response<PhotonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    getAutocompleteCallback.onResponse(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PhotonResponse> call, @NonNull Throwable t) {
                // Handle the error
                getAutocompleteCallback.onFailure(t);
            }
        });
    }

    //Interface for returning the response
    // to the calling class
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF callbacks
    //----------------------------------------------------------------------------------------------------------------
    /*public interface CenterVolleyCallback {
        void onSuccess(ArrayList<String> matchCoordinatesResult, ArrayList<String> matchLabelsResult);
        void onError(String error);
        }*/

    //----------------------------------------------------------------------------------------------------------------
    //END OF callbacks
    //----------------------------------------------------------------------------------------------------------------

    public void clearAllVolleyRequest(){
        resources.flushLayoutCache();
    }
}

