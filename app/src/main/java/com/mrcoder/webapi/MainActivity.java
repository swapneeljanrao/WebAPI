package com.mrcoder.webapi;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements CustomAdapter.ContactsAdapterListener, LocationListener {

    ProgressDialog progressBar;
    private RecyclerView recyclerView;
    private TextView locationText;
    private ArrayList<City> arrayList = new ArrayList<>();
    private CustomAdapter adapter;
    private SearchView searchView;
    private String lat, lon;
    private RequestQueue queue;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);


        locationText = findViewById(R.id.txtCity);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

        getLocation();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onContactSelected(City contact) {

        Toast.makeText(getApplicationContext(), "Selected: " + contact.getRestoName() + ", " + contact.getCity(), Toast.LENGTH_LONG).show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Get current city with lat and lon

    @Override
    public void onLocationChanged(Location location) {

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address address = addresses.get(0);
            lat = location.getLatitude() + "";
            lon = location.getLongitude() + "";

            //locationText.setText("City: "+address.getSubLocality()+"Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
            locationText.setText("City: " + address.getLocality());


            getRestarent(lat, lon);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void getRestarent(String lat, String lon) {

        // String API_URL ="https://developers.zomato.com/api/v2.1/search?lat=18.666666&lon=73.791595";
        String API_URL = "https://developers.zomato.com/api/v2.1/search?lat=" + lat + "&lon=" + lon;


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        StringRequest postrequest = new StringRequest(Request.Method.GET, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response api", response);
                progressBar = new ProgressDialog(getApplicationContext());
                progressBar.setCancelable(true);
                progressBar.setMessage("Loading data...");
//                progressBar.show();

                Toast.makeText(getApplicationContext(), "Response" + response, Toast.LENGTH_LONG).show();

                try {
                    JSONObject restorantList = new JSONObject(response);
                    JSONArray restoArray = restorantList.getJSONArray("restaurants");

                    for (int i = 0; i < restoArray.length(); i++) {
                        JSONObject jsonObject = restoArray.getJSONObject(i);

                        JSONObject jsonObjRestro = jsonObject.getJSONObject("restaurant");
                        String RestoName = jsonObjRestro.getString("name"); //restorant name
                        JSONObject jsonObjLocation = jsonObjRestro.getJSONObject("location");

                        String City = jsonObjLocation.getString("city"); //city name
                        String Address = jsonObjLocation.getString("address");
                        String Latitude = jsonObjLocation.getString("latitude");
                        String Longitude = jsonObjLocation.getString("longitude");

                        City city = new City(RestoName, City, Address, Latitude, Longitude);
                        arrayList.add(city);
                        progressBar.dismiss();
                    }
                    adapter = new CustomAdapter(MainActivity.this, arrayList, MainActivity.this);

                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response api", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user-key", "b9e4f91b73939a4b1d6bd5dfaacbc3a3");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };
        queue.add(postrequest);
    }
}

