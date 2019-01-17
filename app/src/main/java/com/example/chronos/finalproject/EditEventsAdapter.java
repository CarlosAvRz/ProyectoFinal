package com.example.chronos.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class EditEventsAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list;
    private Context context;

    public EditEventsAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
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

            }
        });
        listEraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // notifyDataSetChanged();
            }
        });

        return view;
    }
}
