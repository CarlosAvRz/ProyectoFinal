package com.example.chronos.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chronos.finalproject.User.Register1stActivity;

public class LogInSignUpOptionsActivity extends AppCompatActivity {

    Button logInButton;
    Button signUpButton;
    Button facebookButton;

    public void onTestUserPressed(View view) {
        LinearLayout linearLayout = (LinearLayout)view;
        linearLayout.setSelected(true);
        Toast.makeText(this, "En desarrollo...", Toast.LENGTH_SHORT).show();
    }

    public void onSignUpPressed(View view) {
        LinearLayout linearLayout = (LinearLayout)view;
        linearLayout.setSelected(true);
        Intent intent = new Intent(this, Register1stActivity.class);
        startActivity(intent);
    }

    public void onLogInPressed(View view) {
        LinearLayout linearLayout = (LinearLayout)view;
        linearLayout.setSelected(true);
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    public void onFacebookPressed(View view) {
        LinearLayout linearLayout = (LinearLayout)view;
        linearLayout.setSelected(true);
        Toast.makeText(this, "En desarrollo...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_sign_up_options);
    }
}
