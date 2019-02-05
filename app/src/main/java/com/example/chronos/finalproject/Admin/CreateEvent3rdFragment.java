package com.example.chronos.finalproject.Admin;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chronos.finalproject.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class CreateEvent3rdFragment extends Fragment {

    Bitmap eventPic;
    ImageView edImageView;

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    FragmentManager fragmentManager;

    // Resultado de permisos para subir foto de perfil, en caso de negarse mostrar mensaje
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            } else {
                Toast.makeText(getContext(), getString(R.string.ask_for_permission), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Lanzar actividad de fotos almacenadas para seleccionar foto de perfil, si selecciono una el usuario mostrar en imageView
    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                eventPic = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                edImageView.setImageBitmap(eventPic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo de conversion de bitMap a String para subir a la base de datos
    public String bitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create_event3rd, container, false);

        edImageView = rootView.findViewById(R.id.evImageView);

        Button selectEvPicButton = rootView.findViewById(R.id.selectEvPicButton);
        Button evCancelButton = rootView.findViewById(R.id.evCancelButton);
        Button finishCreationButton = rootView.findViewById(R.id.finishCreationButton);

        // Funcion para el boton de seleccionar, al intentar subir una foto revisar permiso de lectura, si no hay pedirlos, si hay ejecutar metodo getPhoto()
        selectEvPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    getPhoto();
                }
            }
        });

        // Cancelar registro y volver a login preguntando en un display alert
        evCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.cancel_op))
                        .setMessage(getString(R.string.cancel_op_detail))
                        .setPositiveButton(getString(R.string.accept_message), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AdminEventsListFragment adminEventsListFragment = new AdminEventsListFragment();
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.adminPlaceholder, adminEventsListFragment)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        })
                        .setNegativeButton(getString(R.string.decline_message), null)
                        .show();
            }
        });

        // Actualizar informacion
        finishCreationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Actualizar informacion en la base o crear nuevo evento
                DatabaseReference newEventRef = FirebaseDatabase.getInstance().getReference("Eventos/");
                HashMap<String, Object> eventToEdit = (HashMap<String, Object>) getArguments().getSerializable("ValoresEvento");
                String key;
                if (eventToEdit.containsKey("IDEvento")) {
                    key = (String) eventToEdit.get("IDEvento");
                } else {
                    key = newEventRef.push().getKey();
                }
                Map<String, Object> newEvent = new HashMap<>();
                newEvent.put(key + "/Nombre", eventToEdit.get("Nombre"));
                newEvent.put(key + "/NombreEncargado", eventToEdit.get("NombreEncargado"));
                newEvent.put(key + "/Latitud", eventToEdit.get("Latitud"));
                newEvent.put(key + "/Longitud", eventToEdit.get("Longitud"));
                newEvent.put(key + "/DiaEvento", eventToEdit.get("DiaEvento"));
                newEvent.put(key + "/MesEvento", eventToEdit.get("MesEvento"));
                newEvent.put(key + "/AnioEvento", eventToEdit.get("AnioEvento"));
                newEvent.put(key + "/HoraInicial",eventToEdit.get("HoraInicial") );
                newEvent.put(key + "/HoraFinal",eventToEdit.get("HoraFinal") );
                newEvent.put(key + "/Cupo", eventToEdit.get("Cupo"));
                newEvent.put(key + "/Direccion", eventToEdit.get("Direccion"));
                newEvent.put(key + "/Cancelado", false);
                newEventRef.updateChildren(newEvent);

                // Subir foto de evento o reemplazarla
                if (eventPic == null) {
                    eventPic = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_picture);
                }
                DatabaseReference evPicRef = FirebaseDatabase.getInstance().getReference("Eventos-Fotos/" + key);
                evPicRef.setValue(bitMapToString(eventPic));

                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, 0);
            }
        });

        return rootView;
    }

}
