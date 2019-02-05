package com.example.chronos.finalproject.User;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chronos.finalproject.R;
import com.example.chronos.finalproject.Models.UserData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EventDetailsFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    LatLng singleLatLng;

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    FragmentManager fragmentManager;

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
        View rootView = inflater.inflate(R.layout.fragment_event_details, container, false);
        mMapView = rootView.findViewById(R.id.mapView2);
        mMapView.onCreate(savedInstanceState);

        // Inicializar el HashMap con informacion del evento y LatLng para marcador unico de ese evento
        final HashMap<String, Object> selectedEvent = (HashMap<String, Object>) getArguments().getSerializable("selectedEvent");
        singleLatLng = new LatLng((double) selectedEvent.get("Latitud"), (double) selectedEvent.get("Longitud"));

        // Definir valores para vista de detalles
        TextView detEventNameTextView = rootView.findViewById(R.id.detEventNameTextView);
        TextView detOrganizerNameTextView = rootView.findViewById(R.id.detOrganizerNameTextView);
        TextView detDateTextView = rootView.findViewById(R.id.detDateTextView);
        TextView detQuotaTextView = rootView.findViewById(R.id.detQuotaTextView);
        TextView detAddressTextView = rootView.findViewById(R.id.detAddressTextView);
        Button assistEventButton = rootView.findViewById(R.id.assistEventButton);

        detEventNameTextView.setText(selectedEvent.get("Nombre").toString());
        detOrganizerNameTextView.setText(getString(R.string.organizer_name) + " " + selectedEvent.get("NombreEncargado").toString());
        detDateTextView.setText(getString(R.string.date_event) + " " + selectedEvent.get("DiaEvento").toString() + "/" + selectedEvent.get("MesEvento") + "/" + selectedEvent.get("AnioEvento"));
        detQuotaTextView.setText(getString(R.string.quota) + " " + selectedEvent.get("Cupo").toString());
        detAddressTextView.setText(getString(R.string.event_address) + " " + selectedEvent.get("Direccion").toString());

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
                googleMap.addMarker(new MarkerOptions()
                        .position(singleLatLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(singleLatLng).zoom(13).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        assistEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference eventAssistants = FirebaseDatabase.getInstance().getReference("Usuarios-Eventos" + "/" + selectedEvent.get("IDEvento").toString());
                eventAssistants.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> allAssistants;
                        if (dataSnapshot.hasChildren()) {
                            allAssistants = (HashMap<String, Object>) dataSnapshot.getValue();
                        } else {
                            allAssistants = new HashMap<>();
                        }
                        allAssistants.put(UserData.getInstance().getUserId(), true);
                        eventAssistants.updateChildren(allAssistants);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Toast toast = Toast.makeText(getContext(), getString(R.string.toast_assistance), Toast.LENGTH_LONG);
                TextView toastModifier = toast.getView().findViewById(android.R.id.message);
                toastModifier.setGravity(Gravity.CENTER);
                toast.show();

                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, 0);
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
