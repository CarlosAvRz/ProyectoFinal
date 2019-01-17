package com.example.chronos.finalproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.chronos.finalproject.AdminMainMenu.IDUser;

public class AdminEventsListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inicializar fragmento y elementos de la UI
        View rootView = inflater.inflate(R.layout.fragment_admin_events_list, container, false);
        ListView editEventsListView = rootView.findViewById(R.id.editEventsListView);
        Button createEvent = rootView.findViewById(R.id.createEventButton);

        // Inicializar arreglos para listView
        final ArrayList<String> eventsNameList = new ArrayList<>();
        final EditEventsAdapter editEventsAdapter = new EditEventsAdapter(eventsNameList, getContext());
        editEventsListView.setAdapter(editEventsAdapter);

        // Buscar eventos creados por el usuario administrador
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("Eventos");
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, Object> singleEvent = (HashMap<String, Object>) data.getValue();
                        if (singleEvent.get("NombreEncargado").toString().equals(IDUser)) {
                            eventsNameList.add((String) singleEvent.get("Nombre"));
                            editEventsAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Crear un nuevo evento
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEvent1stFragment createEvent1stFragment = new CreateEvent1stFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.adminPlaceholder, createEvent1stFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
    }
}
