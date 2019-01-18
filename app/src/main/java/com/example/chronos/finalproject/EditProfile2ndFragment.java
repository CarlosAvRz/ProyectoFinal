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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

import static com.example.chronos.finalproject.MainMenu.IDUser;
import static com.example.chronos.finalproject.MainMenu.FullNameUser;

public class EditProfile2ndFragment extends Fragment {

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
        // Inicializar fragmento y elementos de la UI
        View rootView = inflater.inflate(R.layout.fragment_edit_profile2nd, container, false);

        edImageView = rootView.findViewById(R.id.edImageView);
        ;
        Button edSelectProfilePicButton = rootView.findViewById(R.id.edSelectProfilePicButton);
        Button edCancelButton = rootView.findViewById(R.id.edCancelButton);
        Button edFinishRegButton = rootView.findViewById(R.id.edFinishRegButton);

        final String name = getArguments().getString("name");
        final String lastName = getArguments().getString("lastName");
        final String mLastName = getArguments().getString("mLastName");
        final String email = getArguments().getString("email");
        final String password = getArguments().getString("password");
        final String birthDay = getArguments().getString("birthDay");
        final String birthMonth = getArguments().getString("birthMonth");
        final String birthYear = getArguments().getString("birthYear");

        // Imagen previa de perfil
        DatabaseReference prevImageRef = FirebaseDatabase.getInstance().getReference("Usuarios-FotosPerfil/" + IDUser + "/fotoPerfil");
        prevImageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                prevProfPic = stringToBitMap((String) dataSnapshot.getValue());
                profilePic = prevProfPic;
                edImageView.setImageBitmap(profilePic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                                SelfProfile selfProfile = new SelfProfile();
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.placeHolderFrameLayout, selfProfile)
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

                // Actualizar nombre
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Usuarios/" + IDUser);
                Map<String, Object> valuesToSet = new HashMap<>();
                valuesToSet.put("Rol", "Usuario");
                valuesToSet.put("Nombre", name);
                valuesToSet.put("ApellidoPat", lastName);
                valuesToSet.put("ApellidoMat", mLastName);
                valuesToSet.put("DiaNac", birthDay);
                valuesToSet.put("MesNac", birthMonth);
                valuesToSet.put("AnioNac", birthYear);
                valuesToSet.put("Correo", email);
                valuesToSet.put("Contrasenia", password);
                usersRef.updateChildren(valuesToSet);

                // Actualizar foto de perfil solo si es diferente
                if (profilePic != prevProfPic) {
                    DatabaseReference profPicUserRef = FirebaseDatabase.getInstance().getReference("Usuarios-FotosPerfil/" + IDUser);
                    Map<String, Object> photoUpdate = new HashMap<>();
                    photoUpdate.put("fotoPerfil", bitMapToString(profilePic));
                    profPicUserRef.updateChildren(photoUpdate);
                }

                // Actualizar publicaciones con el nombre nuevo y nodos de amigos
                final String tempFullName = name + " " + lastName + " " + mLastName;
                FullNameUser = tempFullName;
                DatabaseReference updatePostsRef = FirebaseDatabase.getInstance().getReference("Publicaciones");
                updatePostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                HashMap<String, Object> singlePost = (HashMap<String, Object>) data.getValue();
                                if (singlePost.get("IDUsuario").toString().equals(IDUser)) {
                                    DatabaseReference singlePostUpdate = FirebaseDatabase.getInstance().getReference("Publicaciones/" + data.getKey() + "/NombreUsuario");
                                    singlePostUpdate.setValue(tempFullName);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                DatabaseReference updateFriendsRef = FirebaseDatabase.getInstance().getReference("Amigos");
                updateFriendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                HashMap<String, Object> friendList = (HashMap<String, Object>) data.getValue();
                                if (friendList.containsKey(IDUser)) {
                                    DatabaseReference singleFriendUpdate = FirebaseDatabase.getInstance().getReference("Amigos/" + data.getKey() + "/" + IDUser);
                                    singleFriendUpdate.setValue(tempFullName);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //  Lanzar el fragmento de perfil propio
                Toast.makeText(getContext(), getString(R.string.values_updated), Toast.LENGTH_SHORT).show();
                SelfProfile selfProfile = new SelfProfile();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.placeHolderFrameLayout, selfProfile)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
    }
}
