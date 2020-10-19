package com.example.prog02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    protected Intent runIntent;
    private FusedLocationProviderClient fusedLocationClient;
    private RequestQueue queue;
    AlertDialog alertDialog;
    public String Civic_URL, Civic_URL_params, Geo_URL, API_Key, latlng_String, address, encoded_Address, city, state;
    public JSONObject senatorOne, senatorTwo, congressRep;
    EditText editTextAddress, editTextZipCode;
    ImageButton currentLocationButton, randomLocationButton;
    Double latitude, longitude, maxLatitude, maxLongitude, minLatitude, minLongitude, randLatitude, randLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate Strings for API Requests and Necessary Other Variables
        Civic_URL = "https://civicinfo.googleapis.com/civicinfo/v2/representatives?address=";
        Civic_URL_params = "&includeOffices=true&levels=country&roles=legislatorUpperBody&roles=legislatorLowerBody";
        Geo_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
        API_Key = "&key=AIzaSyCC5urIjixnUEPLKHJXS2Wv6Me-yYifFg8";

        currentLocationButton = findViewById(R.id.buttonCurrentLocation);
        randomLocationButton = findViewById(R.id.buttonRandomLocation);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextZipCode = findViewById(R.id.editTextZipCode);
        runIntent = new Intent(this, LocationOutputActivity.class);

        // Instantiate Alert Dialogue Box for Bad Address Inputs
        alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Invalid Address");
        alertDialog.setMessage("Please try re-entering your address");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // Instantiate Volley Services
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        queue = new RequestQueue(cache, network);
        queue.start();

        //Instantiate Location Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            return;
        } else{
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            });
        }

        // Actions for EditText for Fuller Address Input
        editTextAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 7){
                    editTextAddress.setCompoundDrawablesWithIntrinsicBounds(R.drawable.blue_circle_input_empty,0,0,0);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 7){
                    editTextAddress.setCompoundDrawablesWithIntrinsicBounds(R.drawable.blue_circle_input_filled,0,0,0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editTextAddress.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() <= (editTextAddress.getCompoundDrawables()[0].getBounds().width())) {
                        address = editTextAddress.getText().toString();
                        queue.add(getRepFromAddress());
                        return true;
                    }
                }
                return false;
            }
        });

        // Actions for EditText for Zip Code Input
        editTextZipCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 5){
                    editTextZipCode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.blue_circle_input_empty,0,0,0);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 5){
                    editTextZipCode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.blue_circle_input_filled,0,0,0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editTextZipCode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() <= (editTextZipCode.getCompoundDrawables()[0].getBounds().width())) {
                        address = editTextZipCode.getText().toString();
                        queue.add(getRepFromAddress());
                        return true;
                    }
                }
                return false;
            }
        });

        // Actions for Current Location Input
        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latlng_String = String.format("latlng=%f,%f", latitude, longitude);
                queue.add(getAddressFromLatLng());
            }
        });

        // Actions for Random Location Input
        randomLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maxLatitude = 41.8;
                maxLongitude = -81.5;
                minLatitude = 33.8;
                minLongitude = -116.2;
                Random r = new Random();
                randLatitude = minLatitude + (maxLatitude - minLatitude) * r.nextDouble();
                randLongitude = minLongitude + (maxLongitude - minLongitude) * r.nextDouble();
                latlng_String = String.format("latlng=%f,%f", randLatitude, randLongitude);
                queue.add(getAddressFromLatLng());
            }
        });

    }

    // Function Used to Call GeoCoder API and Reverse GeoCode Position from Lat/Long
    public JsonObjectRequest getAddressFromLatLng(){
        String url = Geo_URL + latlng_String + "&result_type=street_address" + API_Key;
        return new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            JSONObject results = (JSONObject) jsonArray.get(0);
                            address = results.getString("formatted_address");
                            queue.add(getRepFromAddress());
                        } catch (JSONException e) {
                            alertDialog.show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alertDialog.show();
            }
        });
    }

    // Function Used to Call Google Civic API and Get Representatives for a Given Address
    public JsonObjectRequest getRepFromAddress(){
        encoded_Address = Uri.encode(address);
        String url = Civic_URL + encoded_Address + Civic_URL_params + API_Key;
        return new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // User Might Under-Specify Address (e.g. Many Congresspeople in NYC, but still only 2 Senators)
                            if (response.getJSONArray("officials").get(2) == null){
                                alertDialog.show();
                                return;
                            }
                        } catch (JSONException e) {
                            alertDialog.show();
                            e.printStackTrace();
                            return;
                        }
                        runIntent.putExtra("json", response.toString());
                        startActivity(runIntent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alertDialog.show();
            }
        });
    }
}