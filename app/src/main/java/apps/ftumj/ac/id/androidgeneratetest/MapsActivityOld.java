package apps.ftumj.ac.id.androidgeneratetest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.List;

import apps.ftumj.ac.id.androidgeneratetest.model.Rumah;

public class MapsActivityOld extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private List<Rumah> listRumah;
    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listRumah = Utils.getListRumah(Utils.loadJson(getResources(), R.raw.data));
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-6.17036419, 106.87034637);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        CameraUpdate center = CameraUpdateFactory.newLatLng(sydney);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        mMap.moveCamera(center);
        updateLocationUI();
        mMap.setMyLocationEnabled(true);

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

        //set location Manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
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
    public void onLocationChanged(Location location) {


        double latit[] = new double[listRumah.size()];
        double longit[] = new double[listRumah.size()];

        latit[0] = location.getLatitude();
        longit[0] = location.getLongitude();
        LatLng asekolah[] = new LatLng[listRumah.size()];
        PolylineOptions options = new PolylineOptions();
        int idx = 0;
        for (Rumah rumah : listRumah) {
            LatLng latLng = new LatLng(new Double(rumah.getLng()), new Double(rumah.getLat()));
            asekolah[idx] = latLng;
            ++idx;
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title(rumah.getNama().toUpperCase()).snippet(rumah.getAlamat()));

            options.add(latLng);

        }
        options.color(Color.RED).width(5);
        Polyline line2 = mMap.addPolyline(options);

        String hitung[][] = new String[listRumah.size()][listRumah.size()];
        double jarak[][] = new double[listRumah.size()][listRumah.size()];

        for (int a = 0; a <= listRumah.size() - 2; a++) {

            for (int b = a + 1; b <= listRumah.size()-1; b++) {

                hitung[a][b] = hitungjarak(latit[a], longit[a], latit[b], longit[b]);
                jarak[a][b] = Double.parseDouble(hitung[a][b]);

            }

        }

        Double lati = location.getLatitude();
        Double longi = location.getLongitude();
        LatLng latLng = new LatLng(lati, longi);
        MarkerOptions markerlatLng = new MarkerOptions();
        markerlatLng.position(latLng);
        mMap.addMarker(markerlatLng);

        LatLng terdekat[] = new LatLng[15];
        double tmp;
        LatLng tmp1;

        for (int c = 0; c <= listRumah.size() - 2; c++) {

            for (int a = c + 1; a <= listRumah.size()-1; a++) {
                for (int b = a + 1; b <= listRumah.size()-1; b++) {

                    if (jarak[c][b] < jarak[c][a]) {
                        tmp = jarak[c][b];
                        tmp1 = asekolah[b];


                        jarak[c][b] = jarak[c][a];
                        asekolah[b] = asekolah[a];

                        jarak[c][a] = tmp;
                        asekolah[a] = tmp1;
                    }
                }
                terdekat[c] = asekolah[c + 1];
            }

        }


        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(latLng, terdekat[0])
                .width(5)
                .color(Color.BLUE));


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // none
    }

    @Override
    public void onProviderEnabled(String provider) {
        // none
    }

    @Override
    public void onProviderDisabled(String provider) {
        // none
    }
}
