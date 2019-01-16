package com.example.chronos.finalproject;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.chronos.finalproject.MainMenu.IDUser;
import static com.example.chronos.finalproject.MainMenu.FullNameUser;

public class ForeignProfile extends Fragment {

    String conversationKey;

    public Bitmap stringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // obtener argumentos del post, pasados desde PostsFragment y toda la informacion del usuario
        final String foreignUserID = getArguments().getString("ForeignUserID");
        final String foreignUserName = getArguments().getString("ForeignUserName");

        // Inicializar vista y elementos
        View rootView = inflater.inflate(R.layout.fragment_foreign_profile, container, false);
        final TextView forNameTextView = rootView.findViewById(R.id.forNameTextView);
        final TextView forAgeTextView = rootView.findViewById(R.id.forAgeTextView);
        final ImageView forPicImageView = rootView.findViewById(R.id.forPicImageView);
        final ImageView reportImageView = rootView.findViewById(R.id.reportImageView);
        final Button forPrevEvButton = rootView.findViewById(R.id.forPrevEvButton);
        final Button forNextEvButton = rootView.findViewById(R.id.forNextEvButton);
        final Button reqFriendsButton = rootView.findViewById(R.id.reqFriendButton);
        final Button msgForButton = rootView.findViewById(R.id.msgForButton);

        // Inicializar la vista para las ventanas popup para reportes
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View reportView = layoutInflater.inflate(R.layout.popup_report, null);
        final Spinner reportTypeSpinner = reportView.findViewById(R.id.reportTypeSpinner);
        final EditText reportContentEditText = reportView.findViewById(R.id.reportContentEditText);
        final Button sendReportButton = reportView.findViewById(R.id.sendReportButton);
        final PopupWindow popupWindow = new PopupWindow(reportView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Guardar color por defecto del texto e inicializar boton preEvnButton
        final ColorStateList defaultBtnColors = forPrevEvButton.getTextColors();
        forPrevEvButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));

        // Inicializar listas de eventos previos y proximos, arreglos que serviran para llenarla y adaptadores
        final ArrayList<String> IDEvents = new ArrayList<>();

        final ArrayList<HashMap<String, String>> prevEvents = new ArrayList<>();
        final ListView forPrevEvListView = rootView.findViewById(R.id.forPrevEvListView);
        final ListAdapter prevEvListAdapter = new SimpleAdapter(
                getContext(),
                prevEvents,
                R.layout.user_events_list_item,
                new String[]{"EventName","EventDate"},
                new int[]{R.id.userEventNameTextView,R.id.userEventDateTextView});
        forPrevEvListView.setAdapter(prevEvListAdapter);

        final ArrayList<HashMap<String, String>> nextEvents = new ArrayList<>();
        final ListView forNextEvListView = rootView.findViewById(R.id.forNextEvListView);
        final ListAdapter nextEvListAdapter = new SimpleAdapter(
                getContext(),
                nextEvents,
                R.layout.user_events_list_item,
                new String[]{"EventName","EventDate"},
                new int[]{R.id.userEventNameTextView,R.id.userEventDateTextView});
        forNextEvListView.setAdapter(nextEvListAdapter);

        // Inicializar spinner con opciones de reporte
        String [] reportType = {"Contenido sexual", "Contenido violento", "Spam", "Lenguaje ofensivo"};
        reportTypeSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, reportType));

        // Obtener edad y nombre
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Usuarios/" + foreignUserID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    try {
                        Map<String, Object> userValues = (HashMap<String, Object>) dataSnapshot.getValue();
                        forNameTextView.setText(userValues.get("Nombre").toString());
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        String birthDay = (String) userValues.get("DiaNac");
                        String birthMonth = (String) userValues.get("MesNac");
                        String birthYear = (String) userValues.get("AnioNac");
                        Date actualDate = new Date();
                        Date userDate = dateFormat.parse(birthYear + "/" + birthMonth + "/" + birthDay);
                        long userAge = (actualDate.getTime() - userDate.getTime()) / 31536000000L;
                        forAgeTextView.setText(String.valueOf(userAge) + " " + getString(R.string.age));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Obtener imagen de perfil
        DatabaseReference profPicRef = FirebaseDatabase.getInstance().getReference("Usuarios-FotosPerfil/" + foreignUserID);
        profPicRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Map<String, Object> userImage = (HashMap<String, Object>) dataSnapshot.getValue();
                    String imageString = userImage.get("fotoPerfil").toString();
                    forPicImageView.setImageBitmap(stringToBitMap(imageString));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Obtener lista de amigos para determinar los estados de los botones de agregar y mensajes
        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("Amigos/" + IDUser);
        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isFriend = false;
                if (dataSnapshot.hasChildren()) {
                    HashMap<String, Object> userFriends = (HashMap<String, Object>) dataSnapshot.getValue();
                    if (userFriends.containsKey(foreignUserID)) {
                        isFriend = true;
                    }
                }
                if (isFriend) {
                    msgForButton.setEnabled(true);
                } else {
                    reqFriendsButton.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Obtener lista de eventos y añadir el evento donde se encuentre el usuario
        DatabaseReference userEventsRef = FirebaseDatabase.getInstance().getReference("Usuarios-Eventos");
        userEventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Map<String, Object> eventAssistants = (HashMap<String, Object>) data.getValue();
                        if (eventAssistants.containsKey(foreignUserID)) {
                            IDEvents.add(data.getKey());
                        }
                    }
                }
                for (String IDEvent : IDEvents) {
                    DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("Eventos/" + IDEvent);
                    eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                try {
                                    Map<String, Object> eventInfo = (HashMap<String, Object>) dataSnapshot.getValue();
                                    HashMap<String, String> singleEvent = new HashMap<>();
                                    singleEvent.put("EventName", eventInfo.get("Nombre").toString());
                                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                    String eventDay = eventInfo.get("DiaEvento").toString();
                                    String eventMonth = eventInfo.get("MesEvento").toString();
                                    String eventYear = eventInfo.get("AnioEvento").toString();
                                    String eventStart = eventInfo.get("HoraInicial").toString();
                                    Date eventDate = dateFormat.parse(eventDay + "/" + eventMonth + "/" + eventYear + " " + eventStart + ":00:00");
                                    Date actualDate = new Date();
                                    long eventFlag = eventDate.getTime() - actualDate.getTime();
                                    singleEvent.put("EventDate", dateFormat.format(eventDate));
                                    if (Long.signum(eventFlag) == 1) {
                                        nextEvents.add(singleEvent);
                                        ((SimpleAdapter) nextEvListAdapter).notifyDataSetChanged();
                                    } else {
                                        prevEvents.add(singleEvent);
                                        ((SimpleAdapter) prevEvListAdapter).notifyDataSetChanged();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Botones para cambiar listas
        forPrevEvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forPrevEvButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                forPrevEvListView.setVisibility(View.VISIBLE);
                forNextEvButton.setTextColor(defaultBtnColors);
                forNextEvListView.setVisibility(View.INVISIBLE);
            }
        });
        forNextEvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forNextEvButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                forNextEvListView.setVisibility(View.VISIBLE);
                forPrevEvButton.setTextColor(defaultBtnColors);
                forPrevEvListView.setVisibility(View.INVISIBLE);
            }
        });

        // Boton para enviar solicitud de amistad
        reqFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference friendReq = FirebaseDatabase.getInstance().getReference("Solicitudes/" + foreignUserID);
                friendReq.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap<String, Object> foreignUserReq;
                        if (dataSnapshot.getValue() != null) {
                            foreignUserReq = (HashMap<String, Object>) dataSnapshot.getValue();
                            if (!foreignUserReq.containsKey(IDUser)) {
                                foreignUserReq.put(IDUser, FullNameUser);
                                Toast.makeText(getContext(), getString(R.string.sent_request), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), getString(R.string.existing_request), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            foreignUserReq = new HashMap<>();
                            foreignUserReq.put(IDUser, FullNameUser);
                            Toast.makeText(getContext(), getString(R.string.sent_request), Toast.LENGTH_SHORT).show();
                        }
                        friendReq.setValue(foreignUserReq);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        // Boton para conversar si es amigo
        msgForButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buscar conversacion existente o crear un nodo nuevo para "Conversacion" y actualizar tambien nodos de "Usuarios-Conversaciones"
                DatabaseReference conversationRef = FirebaseDatabase.getInstance().getReference("Usuarios-Conversaciones/" + IDUser + "/" + foreignUserID);
                conversationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            DatabaseReference newConversationRef = FirebaseDatabase.getInstance().getReference();
                            conversationKey = newConversationRef.push().getKey();
                            Map<String, Object> conversationValues = new HashMap<>();
                            conversationValues.put("Conversaciones/" + conversationKey + "/IDUsuario1", IDUser);
                            conversationValues.put("Conversaciones/" + conversationKey + "/IDUsuario2", foreignUserID);
                            conversationValues.put("Usuarios-Conversaciones/" + IDUser + "/" + foreignUserID, conversationKey);
                            conversationValues.put("Usuarios-Conversaciones/" + foreignUserID + "/" + IDUser, conversationKey);
                            newConversationRef.updateChildren(conversationValues);
                        } else {
                            conversationKey = dataSnapshot.getValue().toString();
                        }
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        // cambiar color de texto para elemento seleccionado del spinner
        reportTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // levantar reporte, mandar solo si hay contenido
        reportImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportContentEditText.setText("");
                reportTypeSpinner.setSelection(0);
                sendReportButton.setEnabled(false);
                popupWindow.setAnimationStyle(R.style.popup_window_animation);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
            }
        });
        reportContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    sendReportButton.setEnabled(true);
                } else {
                    sendReportButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        sendReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("Reportes");
                String key = reportRef.push().getKey();
                HashMap<String, Object> newReport = new HashMap<>();
                newReport.put(key + "/IDUsuario", IDUser);
                newReport.put(key + "/IDReportado", foreignUserID);
                newReport.put(key + "/Tipo", reportTypeSpinner.getSelectedItem());
                newReport.put(key + "/Contenido", reportContentEditText.getText().toString());
                reportRef.updateChildren(newReport);
                Toast.makeText(getContext(), getString(R.string.report_sent), Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}