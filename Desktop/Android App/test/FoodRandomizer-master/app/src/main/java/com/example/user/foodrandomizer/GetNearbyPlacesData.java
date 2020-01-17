package com.example.user.foodrandomizer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Priyanka
 */

class GetNearbyPlacesData extends AsyncTask<Object, String, String> {
    public PlacesClient placesClient;
    public Activity activityRef;
    List<HashMap<String, String>> nearbyPlaceList;
    private String googlePlacesData;
    private GoogleMap mMap;
    String url;
    LatLng currentLocation;
    Activity activity;

    //private OnTaskCompleted listener;
    //public void addListener(OnTaskCompleted listener){
    //    this.listener=listener;

    //}

    public GetNearbyPlacesData(Activity activity)
    {
        this.activity = activity;
    }


    @Override
    protected String doInBackground(Object... objects){
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];

        DownloadURL downloadURL = new DownloadURL();
        try {
            googlePlacesData = downloadURL.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s){

        if(MainActivity.data.equals("Favourite"))
        {
            SharedPreferences pref = activity.getSharedPreferences("MyPref", 0); // 0 - for private mode

            final Set<String> setList = new HashSet<String>(pref.getStringSet("favourite", new HashSet<String>()));
            List<String> setList2 = new ArrayList<>(setList);

            for (int i = 0; i < setList2.size(); i++)
            {
                MarkerOptions markerOptions = new MarkerOptions();
                String[] parts = setList2.get(i).split("\\|");
                Toast.makeText(activity,setList2.get(i) , Toast.LENGTH_LONG).show();
                Toast.makeText(activity,parts[0] , Toast.LENGTH_LONG).show();


                double latitude = Double.parseDouble(parts[0]);
                double longitude = Double.parseDouble(parts[1]);
                String place = parts[2];
                String placeid = parts[3];

                markerOptions.position(new LatLng(latitude,longitude));
                markerOptions.title(place);
                markerOptions.snippet(placeid);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude,longitude)));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            }
        }
        else if(MainActivity.data.equals("Group"))
        {
            SharedPreferences pref = activity.getSharedPreferences("MyPref", 0); // 0 - for private mode

            final Set<String> folderList = new HashSet<String>(pref.getStringSet("group", new HashSet<String>()));
            List<String> folderList2 = new ArrayList<>(folderList);

            for (int i = 0; i < folderList2.size(); i++)
            {
                Log.d("666666", "onPostExecute: " + folderList2.get(i));
                MarkerOptions markerOptions = new MarkerOptions();
                String[] parts = folderList2.get(i).split("\\|");
                Toast.makeText(activity,folderList2.get(i) , Toast.LENGTH_LONG).show();
                Log.d("666666", "onPostExecute: " + parts[0]);


                double latitude = Double.parseDouble(parts[0]);
                double longitude = Double.parseDouble(parts[1]);
                String place = parts[2];
                String placeid = parts[3];
                String folder = parts[4];



                Log.d("55555", "onPostExecute: " + folderList2.get(i));
                if(folder.equals(MainActivity.folderName))
                {
                    markerOptions.position(new LatLng(latitude,longitude));
                    markerOptions.title(place);
                    markerOptions.snippet(placeid);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude,longitude)));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                }
            }
        }
        else{
            DataParser parser = new DataParser();
            parser.currentLocation = currentLocation;
            if (parser.placesClient == null) parser.placesClient = placesClient;
            nearbyPlaceList = parser.parse(s);
            Log.d("nearbyplacesdata","called parse method");
            showNearbyPlaces(nearbyPlaceList);
            //this.listener.onTaskCompleted();
        }


    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList)
    {
        if (nearbyPlaceList == null) {
            return;
        }

        for (int i = 0; i < nearbyPlaceList.size(); i++)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

            String placeName = googlePlace.get("placeName");
            String placeId = googlePlace.get("placeId");
            double lat = Double.parseDouble( googlePlace.get("lat"));
            double lng = Double.parseDouble( googlePlace.get("lng"));
            LatLng latLng = new LatLng( lat, lng);
            markerOptions.position(latLng);
            markerOptions.snippet(placeId);
            markerOptions.title(placeName);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }

    public interface OnTaskCompleted{
        void onTaskCompleted();
    }

    public int getSize()
    {
        return nearbyPlaceList.size();

    }

    public HashMap<String, String> getRandomPlace(int index)
    {
        return nearbyPlaceList.get(index);

    }

    public boolean getNull() {
        return nearbyPlaceList == null;
    }


}