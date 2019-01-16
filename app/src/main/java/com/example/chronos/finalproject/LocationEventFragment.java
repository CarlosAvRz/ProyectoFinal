package com.example.chronos.finalproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LocationEventFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    LatLng singleLatLng;
    String address;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_location_event, container, false);
        final TextView addressTextView = rootView.findViewById(R.id.addressTextView);
        mMapView = rootView.findViewById(R.id.mapView3);
        mMapView.onCreate(savedInstanceState);
        final Button evFinishButton = rootView.findViewById(R.id.evFinishButton);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // Solicitar permisos
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
                googleMap.setMyLocationEnabled(true);

                // Zoom automatico en el zocalo de la ciudad de Oaxaca
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(17.0436248,-96.7119411)).zoom(13).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                // click largo para poner marcador
                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        // Agregar marcador
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                        singleLatLng = latLng;

                        // Mostrar direccion
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        try {
                            List<Address> markerInfo = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            address = markerInfo.get(0).getAddressLine(0);
                            addressTextView.setText(address);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

                evFinishButton.setEnabled(true);
            }
        });

        evFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (singleLatLng != null) {
                    Bundle bundle = new Bundle();
                    HashMap<String, Object> eventValues = (HashMap<String, Object>) getArguments().getSerializable("ValoresEvento");
                    eventValues.put("Latitud", singleLatLng.latitude);
                    eventValues.put("Longitud", singleLatLng.longitude);
                    eventValues.put("Direccion", address);
                    eventValues.put("Cancelado", false);
                    bundle.putSerializable("ValoresEvento", eventValues);
                    SelectImageEvent selectImageEvent = new SelectImageEvent();
                    Log.i("TestApp", eventValues.toString());
                    selectImageEvent.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.adminPlaceholder, selectImageEvent)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(getContext(), getString(R.string.location_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            try {
                mMapView.onDestroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }
}
