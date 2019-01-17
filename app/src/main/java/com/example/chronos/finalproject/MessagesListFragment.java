package com.example.chronos.finalproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.chronos.finalproject.MainMenu.IDUser;

public class MessagesListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inicializar fragmento y variables de la UI
        View rootView = inflater.inflate(R.layout.fragment_messages_list, container, false);;

        final ArrayList<HashMap<String, String>> conversations = new ArrayList<>();
        final ArrayList<String> forUserIDConv = new ArrayList<>();
        final ArrayList<String> convKeys = new ArrayList<>();
        ListView conversationsListView = rootView.findViewById(R.id.conversationsListView);
        final ListAdapter prevEvListAdapter = new SimpleAdapter(
                getContext(),
                conversations,
                R.layout.friend_req_list_item,
                new String[]{"ForeignUserName"},
                new int[]{R.id.friendReqTextView});
        conversationsListView.setAdapter(prevEvListAdapter);

        DatabaseReference userConvRef = FirebaseDatabase.getInstance().getReference("Usuarios-Conversaciones/" + IDUser);
        userConvRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    final HashMap<String, Object> userConv = (HashMap<String, Object>) dataSnapshot.getValue();
                    for (final String key : userConv.keySet()) {
                        DatabaseReference foreignUserName = FirebaseDatabase.getInstance().getReference("Usuarios/" + key);
                        foreignUserName.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChildren()) {
                                    HashMap<String, Object> forUserData = (HashMap<String, Object>) dataSnapshot.getValue();
                                    String forUserName = forUserData.get("Nombre").toString() + " " + forUserData.get("ApellidoPat").toString() + " " + forUserData.get("ApellidoMat").toString();
                                    HashMap<String, String> newConv = new HashMap<>();
                                    newConv.put("ForeignUserName", forUserName);
                                    conversations.add(newConv);
                                    forUserIDConv.add(key);
                                    convKeys.add((String) userConv.get(key));
                                    ((SimpleAdapter) prevEvListAdapter).notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // click para llevar al chat con ese usuario
        conversationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String foreignUserID, foreignUserName, conversationKey;
                foreignUserID = forUserIDConv.get(position);
                foreignUserName = conversations.get(position).get("ForeignUserName");
                conversationKey = convKeys.get(position);

                // Iniciar conversacion
                Bundle bundle = new Bundle();
                bundle.putString("foreignUserID", foreignUserID);
                bundle.putString("foreignUserName", foreignUserName);
                bundle.putString("conversationKey", conversationKey);
                ChatFragment chatFragment = new ChatFragment();
                chatFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.placeHolderFrameLayout, chatFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
    }

}
