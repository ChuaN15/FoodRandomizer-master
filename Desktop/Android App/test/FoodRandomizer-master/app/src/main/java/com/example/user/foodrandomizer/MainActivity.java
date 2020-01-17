package com.example.user.foodrandomizer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GetNearbyPlacesData.OnTaskCompleted {

    RecyclerView recyclerView;

    ArrayList<RestaurantModel> restaurantList;
    public static GoogleMap mMap;
    public static GoogleApiClient client;
    public static LocationRequest locationRequest;
    public static Location lastlocation;
    public static Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    public static int PROXIMITY_RADIUS = 3000;
    public static double latitude, longitude;
    SupportMapFragment mapFragment;
    public static LocationCallback mLocationCallback;
    public static FusedLocationProviderClient mFusedLocationClient;
    public static int randomIndex;
    public static Button buttonRandom;
    public static GetNearbyPlacesData getNearbyPlacesData;
    public static ProgressBar progressBar;
    public static Toolbar toolbar;
    public static boolean toolbarBool = true;
    public static boolean infoWindowBool = true;
    public static AutocompleteSupportFragment autocompleteFragment;
    public static String keyword = null;
    public static PlacesClient placesClient;
    public static Adapter adapter;
    public static int count = 0;
    List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private RelativeLayout infoWindow;
    private boolean optionsBool = false;
    private View childView;
    SharedPreferences pref;
    Set<String> setList;
    public static String selectedData = "",enteredData;
    public static Marker selectedMarker;
    public static MarkerOptions markerOptions;
    public static RecyclerView recyclerView2;
    public static String data = "Nearby",folderName="";
    ImageView imgStar,imgStar2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        act = this;
        pref = this.getSharedPreferences("MyPref", 0); // 0 - for private mode

        Places.initialize(getApplicationContext(), "AIzaSyDZefQdsswMXBFqY5Ai_AMySzBmR4Kc-UQ");

// Create a new Places client instance.
        placesClient = Places.createClient(this);



        Intent intentFromInitial = getIntent();
        Bundle bundle = intentFromInitial.getExtras();

        if (bundle != null && !bundle.isEmpty()) {
            keyword = (String) bundle.get("key");
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        infoWindow = (RelativeLayout) findViewById(R.id.infoWindow);
        imgStar = infoWindow.findViewById(R.id.imgStar);
        imgStar2 = infoWindow.findViewById(R.id.imgStar2);
        SharedPreferences.Editor editor = pref.edit();

        imgStar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> setList = new HashSet<String>(pref.getStringSet("favourite", new HashSet<String>()));

                setList.remove(selectedData);
                editor.putStringSet("favourite",setList).apply();

                imgStar2.setVisibility(View.INVISIBLE);
                imgStar.setVisibility(View.VISIBLE);
            }
        });

        imgStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> setList = new HashSet<String>(pref.getStringSet("favourite", new HashSet<String>()));

                setList.add(selectedData);
                editor.putStringSet("favourite",setList).apply();

                imgStar2.setVisibility(View.VISIBLE);
                imgStar.setVisibility(View.INVISIBLE);
            }
        });

        infoWindow.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //Widgets.
        progressBar = findViewById(R.id.progressBarRand);
        buttonRandom = (Button) findViewById(R.id.randomize);
        buttonRandom.setVisibility(View.VISIBLE);
        buttonRandom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if(MainActivity.data.equals("Favourite"))
                {
                    SharedPreferences pref = MainActivity.this.getSharedPreferences("MyPref", 0); // 0 - for private mode

                    final Set<String> setList = new HashSet<String>(pref.getStringSet("favourite", new HashSet<String>()));
                    List<String> setList2 = new ArrayList<>(setList);

                    if (setList2 != null && setList2.size() != 0) {

                        Random rand = new Random();
                        randomIndex = rand.nextInt(setList2.size());

                        String[] parts = setList2.get(randomIndex).split("\\|");

                        double latitude = Double.parseDouble(parts[0]);
                        double longitude = Double.parseDouble(parts[1]);

                        LatLng latLng = new LatLng(latitude, longitude);
                        showOneLocation(latLng);
                        getPhotoDetails(parts[3]);

                        selectedData = setList2.get(randomIndex);

                        if (infoWindow.getVisibility() == View.GONE) infoWindow.setVisibility(View.VISIBLE);
                        TextView placeName = (TextView) infoWindow.findViewById(R.id.placeName);
                        placeName.setText(parts[2]);

                        if(setList2.contains(selectedData))
                        {
                            imgStar.setVisibility(View.INVISIBLE);
                            imgStar2.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            imgStar.setVisibility(View.VISIBLE);
                            imgStar2.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                else if(MainActivity.data.equals("Group"))
                {
                    SharedPreferences pref = MainActivity.this.getSharedPreferences("MyPref", 0); // 0 - for private mode

                    final Set<String> setList = new HashSet<String>(pref.getStringSet("group", new HashSet<String>()));
                    List<String> setList2 = new ArrayList<>(setList);

                    for (int i = 0;i < setList2.size();i++)
                    {
                        String[] parts = setList2.get(i).split("\\|");
                        if(!parts[4].equals(folderName))
                        {
                            setList2.remove(i);
                        }
                    }

                    if (setList2 != null && setList2.size() != 0) {

                        Random rand = new Random();
                        randomIndex = rand.nextInt(setList2.size());

                        String[] parts = setList2.get(randomIndex).split("\\|");

                        double latitude = Double.parseDouble(parts[0]);
                        double longitude = Double.parseDouble(parts[1]);

                        LatLng latLng = new LatLng(latitude, longitude);
                        showOneLocation(latLng);
                        getPhotoDetails(parts[3]);

                        selectedData = setList2.get(randomIndex);

                        if (infoWindow.getVisibility() == View.GONE) infoWindow.setVisibility(View.VISIBLE);
                        TextView placeName = (TextView) infoWindow.findViewById(R.id.placeName);
                        placeName.setText(parts[2]);

                        if(setList2.contains(selectedData))
                        {
                            imgStar.setVisibility(View.INVISIBLE);
                            imgStar2.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            imgStar.setVisibility(View.VISIBLE);
                            imgStar2.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                else
                {
                    if (getNearbyPlacesData != null && getNearbyPlacesData.getSize() != 0) {
                        Random rand = new Random();
                        randomIndex = rand.nextInt(getNearbyPlacesData.getSize());
                        double lat = Double.parseDouble(getNearbyPlacesData.getRandomPlace(randomIndex).get("lat"));
                        double lng = Double.parseDouble(getNearbyPlacesData.getRandomPlace(randomIndex).get("lng"));
                        String placeId = getNearbyPlacesData.getRandomPlace(randomIndex).get("placeId");
                        getPhotoDetails(placeId);
                        LatLng latLng = new LatLng(lat, lng);
                        showOneLocation(latLng);
                        if (infoWindow.getVisibility() == View.GONE) infoWindow.setVisibility(View.VISIBLE);
                        TextView placeName = (TextView) infoWindow.findViewById(R.id.placeName);
                        placeName.setText(getNearbyPlacesData.getRandomPlace(randomIndex).get("placeName"));

                        setList = new HashSet<String>(pref.getStringSet("favourite", new HashSet<String>()));
                        List<String> setList2 = new ArrayList<>(setList);

                        selectedData = getNearbyPlacesData.getRandomPlace(randomIndex).get("lat") + "|" + getNearbyPlacesData.getRandomPlace(randomIndex).get("lng") + "|" + getNearbyPlacesData.getRandomPlace(randomIndex).get("placeName")+ "|" + placeId;

                        if(setList2.contains(selectedData))
                        {
                            imgStar.setVisibility(View.INVISIBLE);
                            imgStar2.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            imgStar.setVisibility(View.VISIBLE);
                            imgStar2.setVisibility(View.INVISIBLE);
                        }
                    }
                }




                // currentContext.startActivity(activityChangeIntent);

            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        ImageView searchIcon = (ImageView)((LinearLayout)autocompleteFragment.getView()).getChildAt(0);
        searchIcon.setVisibility(View.GONE);

        //autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i("selected", "Place: " + place.getName());
                markerOptions = new MarkerOptions();
                markerOptions.position(place.getLatLng());
                markerOptions.title(place.getName());
                markerOptions.snippet(place.getId());

                setList = new HashSet<String>(pref.getStringSet("favourite", new HashSet<String>()));
                List<String> setList2 = new ArrayList<>(setList);

                if (infoWindow.getVisibility() == View.GONE) infoWindow.setVisibility(View.VISIBLE);
                TextView placeName = (TextView) infoWindow.findViewById(R.id.placeName);
                placeName.setText(place.getName());
                getPhotoDetails(place.getId());


                selectedData = place.getLatLng().latitude + "|" + place.getLatLng().longitude + "|" + place.getName()+ "|" + place.getId();

                if(setList2.contains(selectedData))
                {
                    imgStar.setVisibility(View.INVISIBLE);
                    imgStar2.setVisibility(View.VISIBLE);
                }
                else
                {
                    imgStar.setVisibility(View.VISIBLE);
                    imgStar2.setVisibility(View.INVISIBLE);
                }

                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                selectedMarker = mMap.addMarker(markerOptions);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 25);
                mMap.animateCamera(cameraUpdate);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("selected", "An error occurred: " + status);
            }
        });

        //Maps.
        //amFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        //rlp.addRule(RelativeLayout.ABOVE, R.id.fab);

        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 0, 400);

        //Test
        Button buttonSave = (Button) findViewById(R.id.buttonSaveFolder);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!optionsBool) addOptions();


            }
        });
    }

    public static OptionsAdapter optionsAdapter;
    public static FolderOptionsData[] folderOptionsData;

    public void addOptions() {
        optionsBool = true;
        if (infoWindow.getVisibility() == View.VISIBLE) infoWindow.animate().translationY(infoWindow.getTop()).setInterpolator(new AccelerateInterpolator()).start();

        RelativeLayout main = (RelativeLayout) findViewById(R.id.mainRelative);
        childView = getLayoutInflater().inflate(R.layout.folder_options, main, false);
        main.addView(childView);
        if (childView.getVisibility() == View.GONE) childView.setVisibility(View.VISIBLE);
        childView.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();

        setList = new HashSet<String>(pref.getStringSet("folder", new HashSet<String>()));
        List<String> setList2 = new ArrayList<>(setList);

        folderOptionsData = new FolderOptionsData[setList2.size()+1];
        folderOptionsData[0] = new FolderOptionsData("New Collection", android.R.drawable.ic_input_add);

        for (int i = 0; i < setList2.size(); i++) {
            folderOptionsData[i+1] = new FolderOptionsData(setList2.get(i), 0);
        }

        recyclerView2 = (RecyclerView) childView.findViewById(R.id.recyclerViewOptions);
        optionsAdapter = new OptionsAdapter(folderOptionsData,this);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2.setAdapter(optionsAdapter);
        Button buttonClose = childView.findViewById(R.id.closeOptions);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsBool = false;
                childView.animate().translationY(childView.getTop()).setInterpolator(new AccelerateInterpolator()).start();
            }
        });
    }
/*
    public void onClick(View v)
    {
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();


    }

*/


    public void setProximityRadius(int radius) {
        PROXIMITY_RADIUS = radius;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                        Log.d("TRUE la","TRUE");
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                String placeName2 = marker.getTitle();
                String placeId = marker.getSnippet();
                getPhotoDetails(placeId);
                if (infoWindow.getVisibility() == View.GONE) infoWindow.setVisibility(View.VISIBLE);
                TextView placeName = (TextView) infoWindow.findViewById(R.id.placeName);
                placeName.setText(placeName2);

                selectedData = marker.getPosition().latitude + "|" + marker.getPosition().longitude + "|" + placeName2+ "|" + placeId;

                setList = new HashSet<String>(pref.getStringSet("favourite", new HashSet<String>()));
                List<String> setList2 = new ArrayList<>(setList);

                if(setList2.contains(selectedData))
                {
                    imgStar.setVisibility(View.INVISIBLE);
                    imgStar2.setVisibility(View.VISIBLE);
                }
                else
                {
                    imgStar.setVisibility(View.VISIBLE);
                    imgStar2.setVisibility(View.INVISIBLE);
                }

                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("mapClick", "onMapClick");
                if (toolbarBool == true) {
                    toolbarBool = false;
                    toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                    infoWindow.animate().translationY(infoWindow.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                }
                else {
                    toolbarBool = true;
                    toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                    infoWindow.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();

                }
                if (optionsBool) {
                    optionsBool = false;
                    childView.animate().translationY(childView.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                }
            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            Log.d("Builded","Build");
        }
        Log.d("map",mMap.toString());
    }

    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    public void showOneLocation(LatLng location) {
        Log.d("Location", location.toString());
        latitude = location.latitude;
        longitude = location.longitude;


        Log.d("lat = ",""+latitude);
        Toast.makeText(this,"lat: " + latitude + " lng " + longitude, Toast.LENGTH_LONG).show();
        LatLng latLng = new LatLng(latitude , longitude);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 30);
        mMap.animateCamera(cameraUpdate);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomBy(10));






    }

    public static Activity act;
    //Called when first location of user is retrieved or when user searches in autocomplete fragment
    //Updates the user's location and gets nearby places location.
    public static void onLocationChanged(LatLng location) {
        Log.d("Location", location.toString());
        latitude = location.latitude;
        longitude = location.longitude;
        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();

        }

        Log.d("lat = ",""+latitude);
        Toast.makeText(act,"lat: " + latitude + " lng " + longitude, Toast.LENGTH_LONG).show();
        LatLng latLng = new LatLng(latitude , longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 25);
        mMap.animateCamera(cameraUpdate);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
        Log.e("asd",String.valueOf(client != null));
        if(client != null)
        {
            //mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            //mFusedLocationClient.removeLocationUpdates(client);
            //LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
        Object dataTransfer[] = new Object[2];
        getNearbyPlacesData = new GetNearbyPlacesData(act);
        getNearbyPlacesData.currentLocation = latLng;
        if (getNearbyPlacesData.placesClient == null) getNearbyPlacesData.placesClient = placesClient;
        mMap.clear();
        String resturant = "restaurant";

        String url = getUrl(latitude, longitude, resturant);
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        //buttonRandom.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        getNearbyPlacesData.execute(dataTransfer);
        getNearbyPlacesData.activityRef = act;


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //if (buttonRandom.getVisibility() == View.INVISIBLE) {
          //  buttonRandom.setVisibility(View.VISIBLE);
        //}
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static RestaurantFragment fragmentt;
    FragmentTransaction fragmentTransaction;

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();
        FrameLayout tv = (FrameLayout) findViewById(R.id.content_frames);
        if (id == R.id.nav_camera) {
            //RestaurantFragment fragmentt = new RestaurantFragment();
            //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //getSupportFragmentManager().beginTransaction().hide().commit();
            //tv.setVisibility(View.GONE);
            //fragmentTransaction.replace(R.id.content_frames,(Fragment) fragmentt);
            //fragmentTransaction.addToBackStack(null);
            //fragmentTransaction.commit();
            //fragmentTransaction.show(fragmentt).commit();

            data = "Nearby";
            onConnected(null);

            if(fragmentt!=null)
            {
                fragmentt.getView().setVisibility(View.GONE);
            }

        } else if (id == R.id.fav_camera) {

            data = "Favourite";
            onConnected(null);

            if(fragmentt!=null)
            {
                fragmentt.getView().setVisibility(View.GONE);
            }

        } else if (id == R.id.nav_gallery) {
            fragmentt = new RestaurantFragment();
            fragmentTransaction = fragmentManager.beginTransaction();
            //getSupportFragmentManager().beginTransaction().hide().commit();
            //tv.setVisibility(View.GONE);
            fragmentTransaction.replace(R.id.content_frames,(Fragment) fragmentt);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static String getUrl(double latitude , double longitude , String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&key="+"AIzaSyDZefQdsswMXBFqY5Ai_AMySzBmR4Kc-UQ");
        googlePlaceUrl.append("&sensor=true");
        if (keyword != null) {
            googlePlaceUrl.append("&keyword="+keyword);
        }

        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    public static void onConnected2()
    {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        //locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //locationRequest.setExpirationDuration(30);
        if(ContextCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(act, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                onLocationChanged(new LatLng(location.getLatitude(), location.getLongitude()));
                            }
                        }
                    });


            /*
            //LocationServices.getFusedLocationProviderClient.requestLocationUpdates(client, locationRequest, this));
            //mFusedLocationClient.requestLocationUpdates(locationRequest, this)
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            boolean gpsEnabled=false;
            boolean networkEnabled=false;
            try{
                gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
            try{
                networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}
            Log.d("last known", gpsEnabled + " " + locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            Log.d("last known", networkEnabled  + " " + locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));

            //onLocationChanged(locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER));
            Log.d("locationReq", locationRequest.toString());
            Log.d("Client", client.toString());*/
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        //locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //locationRequest.setExpirationDuration(30);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                onLocationChanged(new LatLng(location.getLatitude(), location.getLongitude()));
                            }
                        }
                    });


            /*
            //LocationServices.getFusedLocationProviderClient.requestLocationUpdates(client, locationRequest, this));
            //mFusedLocationClient.requestLocationUpdates(locationRequest, this)
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            boolean gpsEnabled=false;
            boolean networkEnabled=false;
            try{
                gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
            try{
                networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}
            Log.d("last known", gpsEnabled + " " + locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            Log.d("last known", networkEnabled  + " " + locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));

            //onLocationChanged(locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER));
            Log.d("locationReq", locationRequest.toString());
            Log.d("Client", client.toString());*/
        }
    }


    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("Place", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("Place", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }

    }*/

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    @Override
    public void onTaskCompleted() {
        Random rand = new Random();
        if (getNearbyPlacesData.getNull() == false && getNearbyPlacesData.getSize() != 0) {
            randomIndex = rand.nextInt(getNearbyPlacesData.getSize());
        }
        else {
            Toast.makeText(this, "No nearby places found", Toast.LENGTH_LONG);
        }
        buttonRandom.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void getPhotoDetails(String placeId) {

        // Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
        List<Place.Field> fields = Arrays.asList(Place.Field.PHOTO_METADATAS);

// Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, fields);

        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {

            final Place place = response.getPlace();
            System.out.println(placeId  + " placeId" + place.getPhotoMetadatas());
            final String placeId2 = placeId;
            // Get the photo metadata.
            List<PhotoMetadata> photoMetadatas;
            if (place.getPhotoMetadatas() != null) {
                photoMetadatas = place.getPhotoMetadatas();
                for (PhotoMetadata photoMetadata : photoMetadatas) {

                    String attributions = photoMetadata.getAttributions();
                    FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                            .setMaxWidth(1000) // Optional.
                            .setMaxHeight(400) // Optional.
                            .build();

                    placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                        count++;
                        Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    //activityRef.findViewById(R.)
                        System.out.println(bitmaps + " bitmap");
                        bitmaps.add(bitmap);
                        System.out.println(bitmap.getWidth() + " Files dr" + bitmap.getHeight());

                        if (count >= photoMetadatas.size() || bitmaps.size() == 3) {


                            adapter = new Adapter(bitmaps, this);
                            ViewPager viewPager = findViewById(R.id.viewPager);
                            viewPager.setAdapter(adapter);
                            viewPager.setPadding(0, 0, 0, 0);
                            bitmaps = new ArrayList<Bitmap>();
                            count = 0;
                            return;
                        }
                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            int statusCode = apiException.getStatusCode();
                            // Handle error with given status code.
                            Log.e("Place_not_found", "Place not found: " + exception.getMessage());
                        }
                    });
                }
            }


        });
    }
}
