package com.example.sabry.muhammed.mapsarea;


import android.support.design.widget.NavigationView;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.sabry.muhammed.mapsarea.AreaUtil.calcArea;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
        , NavigationView.OnNavigationItemSelectedListener
        , GoogleMap.OnPolylineClickListener
        , GoogleMap.OnPolygonClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {
    ActionBarDrawerToggle toggle;
    @BindView(R.id.NavigationViewWidget)
    NavigationView navigationView;

    @BindView(R.id.HomeScreenDrawerLayout)
    DrawerLayout drawer;

    @BindView(R.id.HomeScreenToolbar)
    Toolbar mainToolbar;
    private GoogleMap mMap;

    private static int noOfMarkers;
    private static PolygonOptions polylineOptions;
    private static Polygon polygon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        noOfMarkers = 0;
        setSupportActionBar(mainToolbar);
        toggle = new ActionBarDrawerToggle(
                this
                , drawer
                , mainToolbar
                , R.string.navigation_drawer_open
                , R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            // Handle the camera action
            case R.id.normalMap:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.satelliteMap:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        polylineOptions = new PolygonOptions();
        // Set listeners for click events.
        googleMap.setOnPolylineClickListener(this);
        googleMap.setOnPolygonClickListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMarkerDragListener(this);
    }


    public boolean isInside(LatLng position) {
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (noOfMarkers >= 3) {
            if (isInside(latLng))
                return;
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng).title("First").draggable(true);
        mMap.addMarker(markerOptions);
        noOfMarkers++;
        Draw(latLng, true);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng pos = marker.getPosition();
        marker.remove();
        noOfMarkers--;
        Draw(pos, false);
        return true;

    }


    private void Draw(LatLng pos, boolean add) {

        if (polygon != null)
            polygon.remove();
        if (add) {
            polylineOptions.add(pos);
        } else if (noOfMarkers >= 0) {
            List<LatLng> list = polylineOptions.getPoints();
            polylineOptions = new PolygonOptions();
            for (LatLng lat : list) {
                if (lat.latitude == pos.latitude && lat.latitude == pos.latitude)
                    continue;
                polylineOptions.add(lat);
            }
        }
        if (noOfMarkers >= 3) {
            polygon = mMap.addPolygon(polylineOptions);
            polygon.setClickable(true);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }


    @Override
    public void onMarkerDragEnd(Marker marker) {
    }

    @Override
    public void onPolygonClick(Polygon polygon) {
        Double area = calcArea(polygon.getPoints());
        if (area == 0) {
            return;
        }
        if (area > 1000000) {
            area = Double.parseDouble(new DecimalFormat("#.00").format(area / 1000000));
            Toast.makeText(this, "Area of this place = " + area + "MKm", Toast.LENGTH_LONG).show();
        } else if (area > 1000) {

            area = Double.parseDouble(new DecimalFormat("#.00").format(area / 1000));
            Toast.makeText(this, "Area of this place = " + area + "Km", Toast.LENGTH_LONG).show();

        } else {
            area = Double.parseDouble(new DecimalFormat("#.00").format(area));
            Toast.makeText(this, "Area of this place = " + area + "M", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
    }
}
