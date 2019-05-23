package com.example.chronos.finalproject.User;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chronos.finalproject.Models.UXMethods;
import com.example.chronos.finalproject.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class EventsMap extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    ViewGroup root;
    //Button showDetailsButton;

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
        // Inicializar vista raiz y elementos para el fragmento del mapa desde fragment_events_map.xml
        final View rootView = inflater.inflate(R.layout.fragment_events_map, container, false);
        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        // Inflater para obtener elementos xml del popup y poder modificarlos
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View detailsView = layoutInflater.inflate(R.layout.popup_details_event, new LinearLayout(rootView.getContext()), false);
        final TextView eventNameTextView = detailsView.findViewById(R.id.eventNameTextView);
        final TextView organizerNameTextView = detailsView.findViewById(R.id.organizerNameTextView);
        final TextView dateEventTextView = detailsView.findViewById(R.id.dateEventTextView);
        final TextView quotaEventTextView = detailsView.findViewById(R.id.quotaEventTextView);

        // Tamaño de pantalla para calcular tamaño de popup
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int pxScreenWidth = size.x;
        int pxPopupMarginsWidth = UXMethods.convertDensityPointsToPixels(120, getContext());

        // Popup y elementos
        final PopupWindow popupWindow = new PopupWindow(detailsView, pxScreenWidth - pxPopupMarginsWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        final Button showDetailsButton = detailsView.findViewById(R.id.showDetailsButton);
        final TextView quotaAvailableTextView = detailsView.findViewById(R.id.quotaAvailableTextView);
        root = (ViewGroup) getActivity().getWindow().getDecorView().getRootView();

        // ArrayList para contener a todos los eventos y eventIndex para guardar indice y arreglo para guardar estado (accesible o no)
        final ArrayList<HashMap<String, Object>> eventsList = new ArrayList<>();
        final int[] eventIndex = {0};
        final ArrayList<Boolean> accessibleEvent = new ArrayList<>();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                int pixelsBottomPadding = UXMethods.convertDensityPointsToPixels(60, getActivity());
                googleMap.setPadding(0, 0, 0, pixelsBottomPadding);

                // Solicitar permisos
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    googleMap.setMyLocationEnabled(true);
                }

                //mostrar todos los eventos
                DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("Eventos");
                eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                final HashMap<String, Object> eventValues = (HashMap<String, Object>) data.getValue();
                                eventValues.put("IDEvento", data.getKey());

                                // Determinar fecha para mostrar evento si es proximo
                                long eventFlag = 0;
                                try {
                                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                    String eventDay = eventValues.get("DiaEvento").toString();
                                    String eventMonth = eventValues.get("MesEvento").toString();
                                    String eventYear = eventValues.get("AnioEvento").toString();
                                    String eventStart = eventValues.get("HoraInicial").toString();
                                    Date eventDate = dateFormat.parse(eventDay + "/" + eventMonth + "/" + eventYear + " " + eventStart + ":00:00");
                                    Date actualDate = new Date();
                                    eventFlag = eventDate.getTime() - actualDate.getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (Long.signum(eventFlag) == 1) {
                                    eventsList.add(eventValues);

                                    // Para cada evento recuperado buscar en la lista de participantes
                                    DatabaseReference assistantsRef = FirebaseDatabase.getInstance().getReference("Usuarios-Eventos/" + data.getKey());
                                    assistantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            double latitude = (double) eventValues.get("Latitud");
                                            double longitude = (double) eventValues.get("Longitud");
                                            LatLng latLng = new LatLng(latitude, longitude);
                                            if (dataSnapshot.hasChildren()) {
                                                HashMap<String, Object> eventAssistants = (HashMap<String, Object>) dataSnapshot.getValue();
                                                Integer numberOfAssistants = eventAssistants.size();
                                                if (numberOfAssistants >= Long.valueOf(eventValues.get("Cupo").toString())) {
                                                    googleMap.addMarker(new MarkerOptions()
                                                            .position(latLng)
                                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                                    accessibleEvent.add(false);
                                                } else {
                                                    googleMap.addMarker(new MarkerOptions()
                                                            .position(latLng)
                                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                                                    accessibleEvent.add(true);
                                                }
                                            } else {
                                                googleMap.addMarker(new MarkerOptions()
                                                        .position(latLng)
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                                                accessibleEvent.add(true);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // Listener para los marcadores
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        double markerLatitude = marker.getPosition().latitude;
                        double markerLongitude = marker.getPosition().longitude;
                        for (int i = 0; i < eventsList.size(); i++) {
                            HashMap<String, Object> singleEvent = eventsList.get(i);
                            if (markerLatitude == (double) singleEvent.get("Latitud") && markerLongitude == (double) singleEvent.get("Longitud")) {
                                eventNameTextView.setText(singleEvent.get("Nombre").toString());
                                organizerNameTextView.setText(getString(R.string.organizer_name) + " " + singleEvent.get("NombreEncargado").toString());
                                dateEventTextView.setText(getString(R.string.date_event) + " " + singleEvent.get("DiaEvento").toString() + "/" + singleEvent.get("MesEvento").toString() + "/" + singleEvent.get("AnioEvento").toString());
                                quotaEventTextView.setText(getString(R.string.quota) + " " + singleEvent.get("Cupo").toString());
                                if (accessibleEvent.get(i).equals(true)) {
                                    quotaAvailableTextView.setText(getString(R.string.quota_available));
                                    showDetailsButton.setEnabled(true);
                                } else {
                                    quotaAvailableTextView.setText(getString(R.string.quota_empty));
                                    showDetailsButton.setEnabled(false);
                                }
                                eventIndex[0] = i;
                            }
                        }
                        popupWindow.setAnimationStyle(R.style.popup_window_animation);
                        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        popupWindow.setFocusable(true);
                        popupWindow.setOutsideTouchable(true);
                        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                UXMethods.clearDim(root);
                            }
                        });
                        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                        root = (ViewGroup) getActivity().getWindow().getDecorView().getRootView();
                        UXMethods.applyDim(root, 0.5f);
                        return true;
                    }
                });

                // Funcion para el boton que inicia el fragmento de detalles y le pasa los datos del evento
                showDetailsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("selectedEvent", eventsList.get(eventIndex[0]));
                        EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
                        eventDetailsFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.placeHolderFrameLayout, eventDetailsFragment)
                                .addToBackStack(null)
                                .commit();
                        popupWindow.dismiss();
                        UXMethods.clearDim(root);
                    }
                });

                // Para zooming automaticamente en el zocalo de la ciudad de Oaxaca
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(17.0436248,-96.7119411)).zoom(13).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
