package com.example.user.foodrandomizer;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Priyanka
 */

public class DataParser {

    static double earthRadius = 3958.75;
    LatLng currentLocation;
    public PlacesClient placesClient;

    private HashMap<String, String> getPlace(JSONObject googlePlaceJson)
    {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String placeName = "--NA--";
        String latitude= "";
        String longitude="";
        String rating="";
        String numRating="";
        String priceLevel="";
        String distance="";
        String opensAt="";
        String placeId="";

        Log.d("DataParser","jsonobject ="+googlePlaceJson.toString());


        try {
            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name");
            }
//            if (!googlePlaceJson.isNull("vicinity")) {
//                vicinity = googlePlaceJson.getString("vicinity");
//            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            placeId = googlePlaceJson.getString("place_id");
            if (!googlePlaceJson.isNull("rating")) rating = googlePlaceJson.getString("rating");
            if (!googlePlaceJson.isNull("price_level")) priceLevel = googlePlaceJson.getString("price_level");
            if (!googlePlaceJson.isNull("user_ratings_total")) numRating = googlePlaceJson.getString("user_ratings_total");
            if (!googlePlaceJson.isNull("open_now")) opensAt = googlePlaceJson.getJSONObject("opening_hours").getString("open_now");
            distance = distFrom(currentLocation.latitude, currentLocation.longitude, Double.parseDouble(latitude), Double.parseDouble(longitude));
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("placeId", placeId);

            googlePlaceMap.put("placeName", placeName);
            googlePlaceMap.put("rating", rating);
            googlePlaceMap.put("numRating", numRating);
            googlePlaceMap.put("priceLevel", priceLevel);
            googlePlaceMap.put("distance", distance);
            googlePlaceMap.put("opensAt", opensAt);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;

    }
    private List<HashMap<String, String>>getPlaces(JSONArray jsonArray)
    {
        int count = jsonArray.length();
        List<HashMap<String, String>> placelist = new ArrayList<>();
        HashMap<String, String> placeMap = null;

        for (int i = 0; i<count; i++)
        {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                System.out.println(placeMap.get("lat") + "latt");

                placelist.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placelist;
    }

    public List<HashMap<String, String>> parse(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        Log.d("json data", jsonData);

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonArray == null) {
            return null;
        }
        return getPlaces(jsonArray);
    }

    public static String distFrom(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        System.out.println(dist);
        return String.valueOf(dist);
    }


}
