package apps.ftumj.ac.id.androidgeneratetest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.List;

import apps.ftumj.ac.id.androidgeneratetest.model.Rumah;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Marker mCurrLocationMarker;
    private Location mLastLocation;

    private List<Rumah> listRumah;

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    public static final String TAG = MapsActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listRumah = Utils.getListRumah(Utils.loadJson(getResources(), R.raw.data));
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latLng.latitude, latLng.longitude)).zoom(12).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //menghentikan pembaruan lokasi
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        GenerateTest(location.getLatitude(), location.getLongitude());


    }

    private void GenerateTest(double latitude, double longtitude) {
        double latit[] = new double[listRumah.size()];
        double longit[] = new double[listRumah.size()];

//        latit[0] = latitude;
//        longit[0] = longtitude;
        LatLng current = new LatLng(latitude, longtitude);
        LatLng rumahibadah[] = new LatLng[listRumah.size()];
        int idx = 0;
        for (Rumah rumah : listRumah) {
            LatLng latLng = new LatLng(new Double(rumah.getLng()), new Double(rumah.getLat()));
            rumahibadah[idx] = latLng;
            latit[idx] = latLng.latitude;
            longit[idx] = latLng.longitude;
            ++idx;
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title(rumah.getNama().toUpperCase()).snippet(rumah.getAlamat()));

            drawLine(current, latLng, Color.RED);

        }


        String hitung[] = new String[listRumah.size()];
        double jarak[] = new double[listRumah.size()];

        for (int indx = 0; indx <= listRumah.size() - 1; indx++) {
            hitung[indx] = hitungjarak(current.latitude, current.longitude, latit[indx], longit[indx]);
            jarak[indx] = Double.parseDouble(hitung[indx]);
        }


        int idxTerdekat = 0;
        double tmp = jarak[0];
        for (int indx = 0; indx <= jarak.length - 2; indx++) {

            if (tmp > jarak[indx]) {
                tmp = jarak[indx];
                idxTerdekat = indx;
            }

        }

        Rumah terdekat = listRumah.get(idxTerdekat);
        LatLng destination = new LatLng(Double.parseDouble(terdekat.getLng()), Double.parseDouble(terdekat.getLat()));
        drawLine(current, destination, Color.BLUE);


//
//        for (int a = 0; a <= listRumah.size() - 2; a++) {
//
//            for (int b = a + 1; b <= listRumah.size()-1; b++) {
//
//                hitung[a][b] = hitungjarak(latit[a], longit[a], latit[b], longit[b]);
//                jarak[a][b] = Double.parseDouble(hitung[a][b]);
//
//            }
//
//        }
//
//        Double lati = latitude;
//        Double longi = longtitude;
//        LatLng latLng = new LatLng(lati, longi);
//        MarkerOptions markerlatLng = new MarkerOptions();
//        markerlatLng.position(latLng);
//        mMap.addMarker(markerlatLng);
//
//        LatLng terdekat[] = new LatLng[15];
//        double tmp;
//        LatLng tmp1;
//
//        for (int c = 0; c <= listRumah.size() - 2; c++) {
//
//            for (int a = c + 1; a <= listRumah.size()-1; a++) {
//                for (int b = a + 1; b <= listRumah.size()-1; b++) {
//
//                    if (jarak[c][b] < jarak[c][a]) {
//                        tmp = jarak[c][b];
//                        tmp1 = rumahibadah[b];
//
//
//                        jarak[c][b] = jarak[c][a];
//                        rumahibadah[b] = rumahibadah[a];
//
//                        jarak[c][a] = tmp;
//                        rumahibadah[a] = tmp1;
//                    }
//                }
//                terdekat[c] = rumahibadah[c + 1];
//            }
//
//        }
//
//
//        Polyline line = mMap.addPolyline(new PolylineOptions()
//                .add(latLng, terdekat[0])
//                .width(5)
//                .color(Color.RED));

    }

    public void drawLine(LatLng location, LatLng destination, int color) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(color);
        polylineOptions.width(5);
        polylineOptions.add(location)
                .add(location)
                .add(destination);
        mMap.addPolyline(polylineOptions);
    }


    public static String hitungjarak(Double a, Double b, Double c, Double d) {
        Location lokCek = new Location("Titik Lokasi");
        lokCek.setLatitude(a);
        lokCek.setLongitude(b);
        Location newLoc = new Location("TUJUAN");
        newLoc.setLatitude(c);
        newLoc.setLongitude(d);
        double distance = 6371 * Math.acos(Math.cos(Math.toRadians(newLoc.getLatitude())) * Math.cos(Math.toRadians(lokCek.getLatitude())) * Math.cos(Math.toRadians(lokCek.getLongitude()) - Math.toRadians(newLoc.getLongitude())) + Math.sin(Math.toRadians(newLoc.getLatitude())) * Math.sin(Math.toRadians(lokCek.getLatitude())));
        DecimalFormat df = new DecimalFormat(".##");
        String jarak = String.valueOf(df.format(distance));
        return jarak;

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng ftumj = new LatLng(-6.1724369, 106.8701908);
        mMap.addMarker(new MarkerOptions().position(ftumj).title("FT UMJ"));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(ftumj).zoom(12).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                try {
                    StringBuilder urlString = new StringBuilder();
                    String daddr = (String.valueOf(marker.getPosition().latitude) + "," + String.valueOf(marker.getPosition().longitude));
                    urlString.append("http://maps.google.com/maps?f=d&hl=en");
                    urlString.append("&saddr=" + String.valueOf(mMap.getMyLocation().getLatitude()) + "," + String.valueOf(mMap.getMyLocation().getLongitude()));
                    urlString.append("&daddr=" + daddr);
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString.toString()));
                    startActivity(i);
                } catch (Exception ee) {
                    Toast.makeText(getApplicationContext(), "Lokasi Saat Ini Belum Didapatkan, Coba Nyalakan GPS, Keluar Ruangan dan Tunggu Beberapa Saat", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        //Memulai Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                GenerateTest(ftumj.latitude, ftumj.longitude);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            GenerateTest(ftumj.latitude, ftumj.longitude);
        }


    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
         mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Izin diberikan.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Izin ditolak.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

}
