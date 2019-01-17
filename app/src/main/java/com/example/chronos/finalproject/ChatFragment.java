package com.example.chronos.finalproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.chronos.finalproject.MainMenu.IDUser;

public class ChatFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inicializar fragmento y elementos de la UI
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        final EditText messageEditText = rootView.findViewById(R.id.messageEditText);
        final ImageButton sendImageButton = rootView.findViewById(R.id.sendImageButton);

        // Inicializar arreglo de mensajes y arreglo para comparar mensajes ya obtenidos
        final ArrayList<String> previousMessages = new ArrayList<>();
        final ArrayList<String> messages = new ArrayList<>();
        final ListView messagesListView = rootView.findViewById(R.id.messagesListView);
        final ArrayAdapter messagesArrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, messages);
        messagesListView.setAdapter(messagesArrayAdapter);

        // valores para actualizar convesacion
        final String foreignUserID, foreignUserName, conversationKey;
        foreignUserID = getArguments().getString("foreignUserID");
        foreignUserName = getArguments().getString("foreignUserName");
        conversationKey = getArguments().getString("conversationKey");
        // Referencia para actualizar mensajes en cada cambio
        final DatabaseReference allMessages = FirebaseDatabase.getInstance().getReference("Conversaciones/" + conversationKey + "/Mensajes");
        allMessages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, Object> singleMessage = (HashMap<String, Object>) data.getValue();
                        String tempMsg = "";
                        if (singleMessage.get("IDOrigen").toString().equals(IDUser)) {
                            tempMsg = "> ";
                        }
                        tempMsg += singleMessage.get("Contenido").toString();
                        messages.add(tempMsg);
                        previousMessages.add(data.getKey());
                    }
                }
                messagesArrayAdapter.notifyDataSetChanged();
                sendImageButton.setEnabled(true);
                allMessages.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (!previousMessages.contains(dataSnapshot.getKey())) {
                             HashMap<String, Object> newMessage = (HashMap<String, Object>) dataSnapshot.getValue();
                            String tempMsg = "";
                            if (newMessage.get("IDOrigen").toString().equals(IDUser)) {
                                tempMsg = "> ";
                            }
                            tempMsg += newMessage.get("Contenido").toString();
                            messages.add(tempMsg);
                            messagesArrayAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Boton para enviar mensaje
        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!messageEditText.getText().toString().equals("")) {
                    String messageKey = allMessages.push().getKey();
                    Map<String, Object> newMessage = new HashMap<>();
                    newMessage.put(messageKey + "/Contenido", messageEditText.getText().toString());
                    newMessage.put(messageKey + "/IDOrigen", IDUser);
                    allMessages.updateChildren(newMessage);
                } else {
                    Toast.makeText(getContext(), getString(R.string.empty_msg), Toast.LENGTH_SHORT).show();
                }
                messageEditText.setText("");
            }
        });

        return rootView;
    }

}
