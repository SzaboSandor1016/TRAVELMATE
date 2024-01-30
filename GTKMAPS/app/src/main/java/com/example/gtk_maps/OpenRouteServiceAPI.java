package com.example.gtk_maps;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenRouteServiceAPI {

    public interface RouteCallback {
        void onRouteReceived(String result);
    }

    private static final String API_KEY = "5b3ce3597851110001cf624822732185f95b41bbadd3ad38afd95ef0"; // Cseréld le a saját API kulcsodra
    private static final String API_URL = "https://api.openrouteservice.org/v2/directions/";

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
                        routeCallback.onRouteReceived(result);
                    } else {
                        // Hibakezelés: RouteCallback null esetén
                        Log.e("OpenRouteServiceAPI", "RouteCallback is null");
                    }
                }
            }
        }.execute();
    }
}
