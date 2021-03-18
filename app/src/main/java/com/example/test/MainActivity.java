package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.graphics.ColorUtils;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationSource;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.MarkerIcons;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_REQUEST_INTERVAL = 1000;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private boolean locationEnabled;
    private Button bt_kdh_range;
    private Button bt_kdh_inform;
    private ImageView img_knh_alarmbtn;
    private static NaverMap map;
    private static LatLng Last_coord;
    public static LatLng Click_coord = new LatLng(37.5666102, 126.9783881);
    private CircleOverlay circleOverlay;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void Move(View v)
    {
        Intent move = new Intent(MainActivity.this, Alarm_Activity.class);
        startActivity(move);
    }
    private void init()
    {
        ///////////////////////////////////////////////////////////////////////////
        //NaverMapSdk.getInstance(this).setClient(
          //      new NaverMapSdk.NaverCloudPlatformClient("jit743xg27"));
        ///////////////////////////////////////////////////////////////////////////////

        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        bt_kdh_range = findViewById(R.id.bt_kdh_range);
        bt_kdh_inform =findViewById(R.id.bt_kdh_inform);

    }
    public static class CustomLocationSource implements LocationSource, NaverMap.OnMapClickListener {
        private OnLocationChangedListener listener;
        public static boolean MapNullCheck = false;
        public static Marker markerWithSubCaption = new Marker();
        @Override
        public void activate(@NonNull OnLocationChangedListener listener) {
            this.listener = listener;
        }

        @Override
        public void deactivate() {
            listener = null;
        }

        @Override
        public void onMapClick(@NonNull PointF point, @NonNull LatLng coord) {
            if (listener == null) {
                return;
            }

            Click_coord = coord;

            double distance = MainActivity.Cal_distance(coord);
            if(distance<1) {
                MapNullCheck = false;
                markerWithSubCaption.setPosition(coord);
                markerWithSubCaption.setIcon(MarkerIcons.PINK);
                markerWithSubCaption.setCaptionTextSize(14);
                markerWithSubCaption.setCaptionText("사고발생지역");
                markerWithSubCaption.setCaptionMinZoom(12);
                markerWithSubCaption.setSubCaptionTextSize(10);
                markerWithSubCaption.setSubCaptionColor(Color.GRAY);
                markerWithSubCaption.setSubCaptionText("사고");
                markerWithSubCaption.setSubCaptionMinZoom(13);
                markerWithSubCaption.setMap(MainActivity.map);
            }
            else
            {
                MapNullCheck = true;
                markerWithSubCaption.setMap(null);
            }
            Log.d("test","Click = " + coord.toString() + "/"+ distance);
        }
    }

    public static double Cal_distance(LatLng coord)
    {
        int R = 6371;
        double lat1 = Math.PI / 180.0 *Last_coord.latitude;
        double lon1 = Math.PI / 180.0 *Last_coord.longitude;
        double lat2 = Math.PI / 180.0 *coord.latitude;
        double lon2 = Math.PI / 180.0 *coord.longitude;
        double dLat = (lat2-lat1);
        double dLon = (lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double b = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R*b;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        map = naverMap;
        CircularProgressDrawable progressDrawable = new CircularProgressDrawable(this);
        progressDrawable.setStyle(CircularProgressDrawable.LARGE);
        progressDrawable.setColorSchemeColors(Color.WHITE);
        progressDrawable.start();
        tryEnableLocation();

        int color = Color.RED;
        circleOverlay = new CircleOverlay();
        circleOverlay.setRadius(1000);
        circleOverlay.setColor(ColorUtils.setAlphaComponent(color, 31));
        circleOverlay.setOutlineColor(color);
        circleOverlay.setCenter(new LatLng(37.5666102, 126.9783881));
        circleOverlay.setMap(naverMap);
        bt_kdh_range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(circleOverlay.isVisible())
                {
                    circleOverlay.setVisible(false);
                }
                else
                {
                    circleOverlay.setVisible(true);
                }
            }
        });
        bt_kdh_inform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MMM","클릭" + CustomLocationSource.MapNullCheck );
                if(CustomLocationSource.MapNullCheck == true)
                {
                    Log.d("MMM","null");
                    return;
                }
                else
                {
                    Log.d("MMM","not null");
                    Intent intent = new Intent(MainActivity.this, DialogActivity.class);
                    startActivity(intent);
                }
            }
        });
        CustomLocationSource locationSource = new CustomLocationSource();

        naverMap.setLocationSource(locationSource);
        naverMap.setOnMapClickListener(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Face);
    }
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override // 이동할때 받음
        public void onLocationResult(LocationResult locationResult) {
            if (map == null) {
                return;
            }
            Location lastLocation = locationResult.getLastLocation();
            LatLng coord = new LatLng(lastLocation);
            LocationOverlay locationOverlay = map.getLocationOverlay();
            locationOverlay.setPosition(coord);
            locationOverlay.setBearing(lastLocation.getBearing());
            map.moveCamera(CameraUpdate.scrollTo(coord));
            locationOverlay.setVisible(true);
            circleOverlay.setCenter(coord);
            Last_coord = coord;

            Log.d("test","Move = " + coord.toString());
        }
    };
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int grantResult : grantResults) {
                if (grantResult != PermissionChecker.PERMISSION_GRANTED) {
                    return;
                }
            }
            enableLocation();
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        enableLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disableLocation();
    }
    private void tryEnableLocation() {
        if (ContextCompat.checkSelfPermission(this, PERMISSIONS[0]) == PermissionChecker.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, PERMISSIONS[1]) == PermissionChecker.PERMISSION_GRANTED) {
            enableLocation();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }
    private void enableLocation() {
        new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        LocationRequest locationRequest = new LocationRequest();
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
                        locationRequest.setFastestInterval(LOCATION_REQUEST_INTERVAL);

                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .requestLocationUpdates(locationRequest, locationCallback, null);
                        locationEnabled = true;
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })
                .addApi(LocationServices.API)
                .build()
                .connect();
    }
    private void disableLocation() {
        if (!locationEnabled) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        locationEnabled = false;
    }
}