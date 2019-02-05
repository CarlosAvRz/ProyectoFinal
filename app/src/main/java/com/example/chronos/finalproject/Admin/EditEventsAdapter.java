package com.example.chronos.finalproject.Admin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chronos.finalproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class EditEventsAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list;
    private ArrayList<HashMap<String, Object>> allInfo;
    private Context context;
    private EditEventListener editEventListener;
    private EraseEventListener eraseEventListener;

    public EditEventsAdapter(ArrayList<String> list, ArrayList<HashMap<String, Object>> allInfo, Context context, EditEventListener editEventListener, EraseEventListener eraseEventListener) {
        this.list = list;
        this.allInfo = allInfo;
        this.context = context;
        this.editEventListener = editEventListener;
        this.eraseEventListener = eraseEventListener;
    }

    public interface EditEventListener {
        void editEvent(HashMap<String, Object> eventInfo);
    }

    public interface EraseEventListener {
        void eraseEvent(HashMap<String, Object> eventInfo, int position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.event_list_item, null);
        }

        // Handle TextView and display string from your list
        TextView listEvNameTextView = view.findViewById(R.id.listEvNameTextView);
        listEvNameTextView.setText(list.get(position));

        // Handle buttons and add onClickListeners
        Button listEdButton = view.findViewById(R.id.listEdButton);
        Button listEraseButton = view.findViewById(R.id.listEraseButton);

        listEdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("Usuarios-Eventos");
                eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            HashMap<String, Object> allEvents = (HashMap<String, Object>) dataSnapshot.getValue();
                            if (!allEvents.containsKey(allInfo.get(position).get("IDEvento").toString())) {
                                editEventListener.editEvent(allInfo.get(position));
                            } else {
                                Toast.makeText(context, context.getString(R.string.event_with_users), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        listEraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("Usuarios-Eventos");
                eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            HashMap<String, Object> allEvents = (HashMap<String, Object>) dataSnapshot.getValue();
                            if (!allEvents.containsKey(allInfo.get(position).get("IDEvento").toString())) {
                                eraseEventListener.eraseEvent(allInfo.get(position), position);
                            } else {
                                Toast.makeText(context, context.getString(R.string.event_with_users), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        return view;
    }
}
