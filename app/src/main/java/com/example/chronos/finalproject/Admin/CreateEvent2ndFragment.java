package com.example.chronos.finalproject.Admin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chronos.finalproject.R;
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

public class CreateEvent2ndFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    //LatLng singleLatLng;
    //String address;
    HashMap<String, Object> eventToEdit;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Preparar mensaje de error
        Toast toast = Toast.makeText(getContext(), getString(R.string.permission_req), Toast.LENGTH_SHORT);
        TextView toastModifier = toast.getView().findViewById(android.R.id.message);
        toastModifier.setGravity(Gravity.CENTER);

        // Comprobar que los permisos sean concedidos y que sean de ACCES_FINE_LOCATION
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
            } else {
                toast.show();
            }
        } else {
            toast.show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create_event2nd, container, false);
        final TextView addressTextView = rootView.findViewById(R.id.addressTextView);
        mMapView = rootView.findViewById(R.id.mapView3);
        mMapView.onCreate(savedInstanceState);
        final Button evFinishButton = rootView.findViewById(R.id.evFinishButton);

        // Recuperar valores de la actividad anterior
        eventToEdit = (HashMap<String, Object>) getArguments().getSerializable("ValoresEvento");

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
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    googleMap.setMyLocationEnabled(true);
                }

                // Zoom automatico en el zocalo de la ciudad de Oaxaca
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(17.0436248,-96.7119411)).zoom(13).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                // Intenta colocar marcador previo y mostrar direccion previa (si viene de edicion en lugar de creacion) y activar el boton
                try {
                    double latitude = (double) eventToEdit.get("Latitud");
                    double longitude = (double) eventToEdit.get("Longitud");;
                    LatLng previousLatLng = new LatLng(latitude, longitude);
                    googleMap.addMarker(new MarkerOptions()
                            .position(previousLatLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                    String previousAddress = (String) eventToEdit.get("Direccion");
                    addressTextView.setText(previousAddress);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // click largo para poner marcador
                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        // Agregar marcador
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                        eventToEdit.put("Latitud", latLng.latitude);
                        eventToEdit.put("Longitud", latLng.longitude);

                        // Mostrar direccion
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        try {
                            List<Address> markerInfo = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            String newAddress = markerInfo.get(0).getAddressLine(0);
                            addressTextView.setText(newAddress);
                            eventToEdit.put("Direccion", newAddress);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

        evFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventToEdit.containsKey("Latitud") && eventToEdit.containsKey("Longitud") && eventToEdit.containsKey("Direccion")) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ValoresEvento", eventToEdit);
                    CreateEvent3rdFragment createEvent3rdFragment = new CreateEvent3rdFragment();
                    createEvent3rdFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.adminPlaceholder, createEvent3rdFragment)
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
