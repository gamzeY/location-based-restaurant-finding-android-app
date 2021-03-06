package com.tutorials.hp.mysqlselector.FragementActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tutorials.hp.mysqlselector.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements LocationListener {

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude





    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    protected LocationManager locationManager;
    Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maps_fragment, container, false);
        getLocation();
        sendLatLng_in_Server(getLatitude(), getLongitude());
        showGoogleMap(getLatitude(), getLongitude(), "konumum");






        return view;
    }

  /*  private void sendRequest() {
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }*/

    public Location getLocation() {
        try {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            // GPS durumunu(true/false) elde ettik
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Network durumunu(true/false) elde ettik
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                //Network olmad??????nda bu ko??ula girer
            } else {
                this.canGetLocation = true;
                //Network Provider'dan ilk lokasyonu ald??k
                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);


                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
            //E??er GPS etkin ise GPS Services kullanarak  latitude/longitude de??erlerini al??yoruz
            if (isGPSEnabled) {
                if (location == null) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }
   /* @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(getActivity(), "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) getActivity(). findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) getActivity().findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }*/
    /**
     * GPS listener kullan??lmas??n??n durdurulmas??
     * Cihazda Gps kullan??m?? durduruldu??unda, uygulamada bu metod ??a????r??l??r
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(MapsFragment.this);
        }
    }
    /**
     * latitude(enlem) de??erini donduren metod
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }
    /**
     * longitude(boylam) de??erini donduren metod
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }





    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    /**
     * Google Map ??zerinde latitude ve longitude de??erlerine g??re konumlar?? i??aretleyerek g??steren metod
     * @param lat
     * @param lng
     * @param locationName
     */
    public void showGoogleMap(double lat,double lng,String locationName){
        LatLng TutorialsPoint = new LatLng(lat, lng);
        GoogleMap googleMap = null;

        try {
            if (googleMap == null) {

                googleMap = ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map)).getMap();
               // googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
               // googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();


            }
            //Haritan??n ??zerinde bulunan, haritay?? b??y??t??p k??????ltmek i??in kullan??lan zooming button aktif ettim
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            //Harita ??zerinde i??aretlenmi?? konumlara haritay?? b??y??terek yani zoomlama yaparak fokuslanmas??n?? yapan kod
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            googleMap.setMyLocationEnabled(true);

            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            //Google Map ??zerinde konum i??aretlemeyi sa??layan imleci olusturan kod
            //title metodu; imlec konulan yere isim vermenizi sa??lar
            Marker TP = googleMap.addMarker(new MarkerOptions().position(TutorialsPoint).title(locationName));


        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Konumuma en yak??n olan yerlerin enlem ve boylam de??erlerini sunucudan alarak haritada
    //bu yerlerin i??aretlenmesini sa??layan metod
    private void sendLatLng_in_Server(double latitude,double longitude) {

        //StrictMode kullanarak,a?? eri??iminin g??venli bir ??ekilde yap??lmas??n?? sa??l??yoruz...
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String wcfUrl="http://192.168.1.104/Tez/bakalim.php" ;

        JSONObject obj = new JSONObject();
        String jsonString = "";
        try {
            //Konum de??erlerimi sunucuya g??nderiyorum...
            obj.put("latitude", latitude);
            obj.put("longitude", longitude);
            HttpClientMy HttpClientMy = new HttpClientMy();
            jsonString = HttpClientMy.callWebService(wcfUrl, obj);

            //Json objesi olusturuyoruz..
            JSONObject jsonResponse = new JSONObject(jsonString);
            //Olusturdugumuz obje ??zerinden  json string deki datalar?? kullan??yoruz..
            JSONArray jArray = jsonResponse.getJSONArray("Android");
            //Konumuma en yak??n olan yerlerin enlem ve boylam de??erlerini sunucudan ald??m.
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                String restoran =json_data.getString("adi");
                Log.w("adi", restoran);
                Double lat = json_data.getDouble("latitude");
                Double lng = json_data.getDouble("longitude");
                //ve bana en yak??n yerleri haritada i??aretleyerek g??stermek i??in showGoogleMap metodunu kulland??m
                showGoogleMap(lat, lng, restoran);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
