package com.example.chronos.finalproject;

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
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class SelectImageEvent extends Fragment {

    Bitmap profilePic, prevProfPic;
    ImageView edImageView;

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
                profilePic = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                edImageView.setImageBitmap(profilePic);
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
        View rootView = inflater.inflate(R.layout.fragment_select_image_event, container, false);

        edImageView = rootView.findViewById(R.id.evImageView);
        ;
        Button edSelectProfilePicButton = rootView.findViewById(R.id.selectEvPicButton);
        Button edCancelButton = rootView.findViewById(R.id.evCancelButton);
        Button edFinishRegButton = rootView.findViewById(R.id.finishCreationButton);

        // Funcion para el boton de seleccionar, al intentar subir una foto revisar permiso de lectura, si no hay pedirlos, si hay ejecutar metodo getPhoto()
        edSelectProfilePicButton.setOnClickListener(new View.OnClickListener() {
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
        edCancelButton.setOnClickListener(new View.OnClickListener() {
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

        // Actualizar informacion de perfil y todos los posts de ese usuario
        edFinishRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profilePic == null) {
                    profilePic = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_picture);
                }

                DatabaseReference newEventRef = FirebaseDatabase.getInstance().getReference("Eventos/");
                String key = newEventRef.push().getKey();
                HashMap<String, Object> eventValues = (HashMap<String, Object>) getArguments().getSerializable("ValoresEvento");
                Map<String, Object> newEvent = new HashMap<>();
                newEvent.put(key + "/Nombre", eventValues.get("Nombre"));
                newEvent.put(key + "/NombreEncargado", eventValues.get("NombreEncargado"));
                newEvent.put(key + "/Latitud", eventValues.get("Latitud"));
                newEvent.put(key + "/Longitud", eventValues.get("Longitud"));
                newEvent.put(key + "/DiaEvento", eventValues.get("DiaEvento"));
                newEvent.put(key + "/MesEvento", eventValues.get("MesEvento"));
                newEvent.put(key + "/AnioEvento", eventValues.get("AnioEvento"));
                newEvent.put(key + "/HoraInicial",eventValues.get("HoraInicial") );
                newEvent.put(key + "/HoraFinal",eventValues.get("HoraFinal") );
                newEvent.put(key + "/Cupo", eventValues.get("Cupo"));
                newEvent.put(key + "/Direccion", eventValues.get("Direccion"));
                newEvent.put(key + "/Cancelado", eventValues.get("Cancelado"));
                newEventRef.updateChildren(newEvent);

                AdminEventsListFragment adminEventsListFragment = new AdminEventsListFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.adminPlaceholder, adminEventsListFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
    }

}
