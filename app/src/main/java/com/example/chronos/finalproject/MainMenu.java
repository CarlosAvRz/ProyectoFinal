package com.example.chronos.finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainMenu extends AppCompatActivity {

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    FragmentManager fragmentManager = getSupportFragmentManager();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_events:
                    fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.placeHolderFrameLayout, new EventsMap())
                            .addToBackStack(BACK_STACK_ROOT_TAG)
                            .commit();
                    return true;
                case R.id.navigation_posts:
                    fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.placeHolderFrameLayout, new PostsFragment())
                            .addToBackStack(BACK_STACK_ROOT_TAG)
                            .commit();
                    return true;
                case R.id.navigation_profile:
                    fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.placeHolderFrameLayout, new SelfProfile())
                            .addToBackStack(BACK_STACK_ROOT_TAG)
                            .commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_events);
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() == 1) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.loguot_title))
                    .setMessage(getString(R.string.loguot_message))
                    .setPositiveButton(getString(R.string.accept_message), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), LogInSignUpOptionsActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(getString(R.string.decline_message), null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
}
