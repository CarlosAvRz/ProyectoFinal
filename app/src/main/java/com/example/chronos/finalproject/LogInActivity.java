package com.example.chronos.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.chronos.finalproject.Admin.AdminMainMenu;
import com.example.chronos.finalproject.Models.UserData;
import com.example.chronos.finalproject.User.MainMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class LogInActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    UserData userData = UserData.getInstance();

    public void logInAccount(View view) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        final boolean[] userMatches = {false};
        final boolean[] userBlocked = {false};
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Map<String, Object> userValues = (Map<String, Object>) data.getValue();
                        String userEmail = (String) userValues.get("Correo");
                        String userPassword = (String) userValues.get("Contrasenia");
                        if (userEmail.equals(emailEditText.getText().toString()) && userPassword.equals(passwordEditText.getText().toString())) {
                            userMatches[0] = true;

                            userData.setUserId(data.getKey());
                            userData.setName((String) userValues.get("Nombre"));
                            userData.setLastName((String) userValues.get("ApellidoPat"));
                            userData.setmLastName((String) userValues.get("ApellidoMat"));
                            userData.setBirthDay((String) userValues.get("DiaNac"));
                            userData.setBirthMonth((String) userValues.get("MesNac"));
                            userData.setBirthYear((String) userValues.get("AnioNac"));
                            userData.setEmail((String) userValues.get("Correo"));
                            userData.setRole((String) userValues.get("Rol"));
                            userData.setPassword((String) userValues.get("Contrasenia"));

                            if (userValues.containsKey("Advertencias") && (Long) userValues.get("Advertencias") >= 3) {
                                userData.setWarnings((Long) userValues.get("Advertencias"));
                                userBlocked[0] = true;
                            } else {
                                userData.setWarnings(0L);
                            }
                        }
                    }
                    if (userMatches[0]) {
                        if (userData.getRole().equals("Usuario")) {
                            if (!userBlocked[0]) {
                                Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                                startActivity(intent);
                            } else {
                                new AlertDialog.Builder(LogInActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle(getString(R.string.wrong_credentials))
                                        .setMessage(getString(R.string.user_banned))
                                        .setPositiveButton(getString(R.string.login_accept), null)
                                        .show();
                            }
                        } else {
                            Intent intent = new Intent(getApplicationContext(), AdminMainMenu.class);
                            startActivity(intent);
                        }
                    } else {
                        new AlertDialog.Builder(LogInActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(getString(R.string.wrong_credentials))
                                .setMessage(getString(R.string.wrong_credentials_detail))
                                .setPositiveButton(getString(R.string.ok_option), null)
                                .show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
    }
}
