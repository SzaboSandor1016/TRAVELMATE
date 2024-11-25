package com.example.gtk_maps;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.example.gtk_maps.MainActivity;
import com.example.gtk_maps.OpenRouteServiceAPI;
import com.example.gtk_maps.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Navigation {

    private final Requests requests;

    public Navigation(RequestQueue requestQueue, Context context){
        this.requests = new Requests(requestQueue,context);
    }
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private interface RowsExecuted{
        void Success(ArrayList<ArrayList<Double>> distancesResult,
                     ArrayList<ArrayList<ArrayList<GeoPoint>>> geoPointsResult);
        void Failure();
    }
    private interface RowExecuted{
        void Success(ArrayList<Double> rowDistancesResult,
                     ArrayList<ArrayList<GeoPoint>> rowGeoPointsResult);
        void Failure();
    }
    public interface TspCallback{
        void onComplete(ArrayList<Polyline> polylines,ArrayList<Integer> indexes);

        void onFailure();
    }

    private BoundingBox addPaddingToBoundingBox(BoundingBox boundingBox, double padding) {
        double latMin = boundingBox.getLatSouth() - padding;
        double latMax = boundingBox.getLatNorth() + padding;
        double lonMin = boundingBox.getLonWest() - padding;
        double lonMax = boundingBox.getLonEast() + padding;

        return new BoundingBox(latMax, lonMax, latMin, lonMin);
    }

    public void tspSolver(String transportMode, ArrayList<Place> selectedPlaces, TspCallback tspCallback){

        executorService.execute(() -> {

        ArrayList<ArrayList<Double>> distancesResult = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<GeoPoint>>> coordinatesResult = new ArrayList<>();
        ArrayList<Polyline> polylines = new ArrayList<>();

        executeRouteRequests(transportMode, selectedPlaces, distancesResult, coordinatesResult, 0, new RowsExecuted() {

            @Override
            public void Success(ArrayList<ArrayList<Double>> distancesResult, ArrayList<ArrayList<ArrayList<GeoPoint>>> geoPointsResult) {

                Log.d("routeRequest", distancesResult.toString());

                ArrayList<ArrayList<Double>> distances = new ArrayList<>(distancesResult);
                Log.d("indexesDistances", distances.toString());
                ArrayList<ArrayList<ArrayList<GeoPoint>>> geoPointsArray = new ArrayList<>(geoPointsResult);
                ArrayList<ArrayList<Double>> finalDistances = new ArrayList<>();
                ArrayList<ArrayList<GeoPoint>> finalGeoPoints = new ArrayList<>();
                ArrayList<Integer> indexes = new ArrayList<>();

                indexes.add(0);

                do {

                    int index = selectedPlaces.size()-1;
                    double minDistance = Double.MAX_VALUE;

                    for (int i= 0; i < distances.size(); i++){

                        for (int j = i+1; j< distances.get(i).size()-1; j++){

                            if ( distances.get(i).get(j)< minDistance && !indexes.contains(j)){
                                index = j;
                                minDistance = distances.get(i).get(j);
                            }

                        }
                    }
                    indexes.add(index);
                    Log.d("mittomen_indexes", indexes.toString());
                    finalGeoPoints.add(geoPointsArray.get(indexes.get(indexes.size()-2)).get(index));

                }while (finalGeoPoints.size()<selectedPlaces.size()-1);

                finalGeoPoints.add(geoPointsArray.get(0).get(indexes.get(indexes.size()-1)));

                Log.d("indexes", indexes.toString());


                for (int i = 0; i< finalGeoPoints.size(); i++) {
                    Polyline polyline = new Polyline();
                    polyline.setPoints(finalGeoPoints.get(i));
                    polyline.setColor(Color.argb(200, 42, 63, 117));
                    polyline.getOutlinePaint().setStrokeWidth(15);
                    polylines.add(polyline);
                    //map.getOverlayManager().add(polyline);
                    //map.zoomToBoundingBox(newBoundingBox, true);
                }
                new Handler(Looper.getMainLooper()).post(() -> tspCallback.onComplete(polylines,indexes));
            }

            @Override
            public void Failure() {
                //Toast.makeText(MainActivity.this, R.string.route_planning_error,Toast.LENGTH_LONG).show();
                Log.e("nem_megyen", "nem megyen");
            }
        });
        });

    }

    private void executeRouteRequests(String transportMode,ArrayList<Place> selectedPlaces,ArrayList<ArrayList<Double>> distancesResult,
                                      ArrayList<ArrayList<ArrayList<GeoPoint>>> geoPointsResult,int index, RowsExecuted rowsExecuted){

        ArrayList<Double> rowCoordinates = new ArrayList<>();

        for(int i=0; i<index; i++){
            rowCoordinates.add(selectedPlaces.get(i).getCoordinates().getLat());
            rowCoordinates.add(selectedPlaces.get(i).getCoordinates().getLon());
        }

        for (int i = index; i< selectedPlaces.size(); i++){
            rowCoordinates.add(selectedPlaces.get(i).getCoordinates().getLat());
            rowCoordinates.add(selectedPlaces.get(i).getCoordinates().getLon());
        }
        ArrayList<Double> rowDistances = new ArrayList<>();
        ArrayList<ArrayList<GeoPoint>> rowGeoPoints = new ArrayList<>();

        Log.d("rowCoordinates", rowCoordinates.toString());

        executeRow(transportMode, rowCoordinates,rowDistances,rowGeoPoints,index*2, 0,  new RowExecuted() {
            @Override
            public void Success(ArrayList<Double> rowDistancesResult, ArrayList<ArrayList<GeoPoint>> rowGeoPointsResult) {

                for (int i=0; i< selectedPlaces.size()-rowDistancesResult.size(); i++){
                    rowDistances.add(Double.MAX_VALUE);
                    rowGeoPoints.add(new ArrayList<>());
                }
                ArrayList<Double> rowDistances = new ArrayList<>(rowDistancesResult);
                ArrayList<ArrayList<GeoPoint>> rowGeoPoints = new ArrayList<>(rowGeoPointsResult);

                distancesResult.add(rowDistances);
                geoPointsResult.add(rowGeoPoints);
                if (index== selectedPlaces.size()-1){
                    rowsExecuted.Success(distancesResult,geoPointsResult);
                    Log.d("rowGEO", geoPointsResult.toString());
                }else {
                    Log.d("rowGEO", geoPointsResult.toString());
                    executeRouteRequests(transportMode, selectedPlaces,distancesResult,geoPointsResult, index+1, rowsExecuted);
                }
            }

            @Override
            public void Failure() {
                rowsExecuted.Failure();
            }
        });

    }

    private void executeRow(String transportMode, ArrayList<Double> coordinatesOrig,
                            ArrayList<Double> rowDistances,ArrayList<ArrayList<GeoPoint>> rowGeoPoints,int startIndex,int index,  RowExecuted rowExecuted){

        Requests.getRoute(coordinatesOrig.get(startIndex), coordinatesOrig.get(startIndex+1), coordinatesOrig.get(index), coordinatesOrig.get(index+1), transportMode, new Requests.RouteCallback() {
            @Override
            public void onRouteReceived(String result) {
                //rowDistances.add()

                try {
                    JSONObject jsonResponse = new JSONObject(result);

                    Log.d("routeResponse", jsonResponse.toString());

                    JSONArray features = jsonResponse.getJSONArray("features");

                    JSONObject firstFeature = features.getJSONObject(0);

                    JSONObject geometry = firstFeature.getJSONObject("geometry");

                    JSONObject properties = firstFeature.getJSONObject("properties");

                    JSONObject summary = properties.getJSONObject("summary");

                    double distance = Double.MAX_VALUE;

                    if (summary.has("distance")) {

                        distance = summary.getDouble("distance");

                    }

                    JSONArray coordinates = null;

                    if ( geometry.has("coordinates")){

                        coordinates = geometry.getJSONArray("coordinates");

                    }

                    ArrayList<GeoPoint> routePoints = new ArrayList<>();

                    for (int i = 0; i < coordinates.length(); i++) {
                        JSONArray point = coordinates.getJSONArray(i);
                        double lat = point.getDouble(1);
                        double lon = point.getDouble(0);

                        routePoints.add(new GeoPoint(lat, lon));
                    }
                    rowGeoPoints.add(routePoints);
                    rowDistances.add(distance);

                    if (index == coordinatesOrig.size() - 2) {
                        rowExecuted.Success(rowDistances,rowGeoPoints);
                    }else {
                        executeRow(transportMode,coordinatesOrig,rowDistances,rowGeoPoints,startIndex, index+2,rowExecuted);
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }

            @Override
            public void onRouteFailure() {
                rowExecuted.Failure();
            }
        });

    }

}
