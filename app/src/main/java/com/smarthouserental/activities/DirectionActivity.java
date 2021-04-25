package com.smarthouserental.activities;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.smarthouserental.R;
import com.smarthouserental.util.DirectionsJSONParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DirectionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Bundle extras;
    private double s_latitide,s_longitude,d_latitide,d_longitude;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Route Direction");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        extras = getIntent().getExtras();
        if (extras != null){
            s_latitide = extras.getDouble("s_latitide");
            s_longitude = extras.getDouble("s_longitude");
            d_latitide = extras.getDouble("d_latitide");
            d_longitude = extras.getDouble("d_longitude");


            String url = getDirectionsUrl(new LatLng(s_latitide,s_longitude), new LatLng(d_latitide,d_longitude));
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);


        }

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(s_latitide, s_longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,13));

        MarkerOptions sourceOption = new MarkerOptions()
                .position(new LatLng(s_latitide,s_longitude))
                .snippet("Source Position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        MarkerOptions destination = new MarkerOptions()
                .position(new LatLng(d_latitide,d_longitude))
                .snippet("Source Position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));


        mMap.addMarker(sourceOption);
        mMap.addMarker(destination);
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        String parameters = str_origin+"&"+str_dest;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line ;
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();
            br.close();

        }catch(Exception e){
           e.printStackTrace();
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points ;
            PolylineOptions lineOptions = null;
           if (result.size() != 0){
               for(int i=0;i<result.size();i++){
                   points = new ArrayList();
                   lineOptions = new PolylineOptions();

                   List<HashMap<String, String>> path = result.get(i);
                   for(int j=0;j <path.size();j++){
                       HashMap<String,String> point = path.get(j);

                       double lat = Double.parseDouble(point.get("lat"));
                       double lng = Double.parseDouble(point.get("lng"));
                       LatLng position = new LatLng(lat, lng);

                       points.add(position);
                   }
                   lineOptions.addAll(points);
                   lineOptions.width(7);
                   lineOptions.color(Color.RED);

               }
               mMap.addPolyline(lineOptions);
           }else {
               Toast.makeText(DirectionActivity.this,"Daily limit is over",Toast.LENGTH_LONG).show();

           }
        }
    }




}
