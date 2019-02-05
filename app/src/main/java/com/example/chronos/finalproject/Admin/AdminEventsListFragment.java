package com.example.chronos.finalproject.Admin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.chronos.finalproject.Models.UserData;
import com.example.chronos.finalproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminEventsListFragment extends Fragment
        implements EditEventsAdapter.EditEventListener, EditEventsAdapter.EraseEventListener {

    ArrayList<String> eventsNameList = new ArrayList<>();
    ArrayList<HashMap<String, Object>> fullInfoEvents = new ArrayList<>();
    EditEventsAdapter editEventsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inicializar fragmento y elementos de la UI
        View rootView = inflater.inflate(R.layout.fragment_admin_events_list, container, false);
        ListView editEventsListView = rootView.findViewById(R.id.editEventsListView);
        Button createEvent = rootView.findViewById(R.id.createEventButton);

        // Inicializar arreglos para listView
        editEventsAdapter = new EditEventsAdapter(eventsNameList, fullInfoEvents, getContext(), this, this);
        editEventsListView.setAdapter(editEventsAdapter);

        // Buscar eventos creados por el usuario administrador
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("Eventos");
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, Object> singleEvent = (HashMap<String, Object>) data.getValue();
                        singleEvent.put("IDEvento", data.getKey());
                        if (singleEvent.get("NombreEncargado").toString().equals(UserData.getInstance().getUserId())) {
                            eventsNameList.add((String) singleEvent.get("Nombre"));
                            fullInfoEvents.add(singleEvent);
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

    @Override
    public void editEvent(HashMap<String, Object> eventInfo) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("EditEvent", eventInfo);
        CreateEvent1stFragment createEvent1stFragment = new CreateEvent1stFragment();
        createEvent1stFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.adminPlaceholder, createEvent1stFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void eraseEvent(final HashMap<String, Object> eventInfo, final int position) {
        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.erase_event_confirm))
                .setMessage(getString(R.string.erase_event_detail))
                .setPositiveButton(getString(R.string.accept_message), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference eraseEvRef = FirebaseDatabase.getInstance().getReference("Eventos/" + eventInfo.get("IDEvento"));
                        eraseEvRef.setValue(null);
                        eventsNameList.remove(position);
                        fullInfoEvents.remove(position);
                        editEventsAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(getString(R.string.decline_message), null)
                .show();
    }
}
