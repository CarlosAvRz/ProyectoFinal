package com.example.chronos.finalproject.User;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.chronos.finalproject.LogInSignUpOptionsActivity;
import com.example.chronos.finalproject.Models.UserData;
import com.example.chronos.finalproject.R;

public class MainMenu extends AppCompatActivity {

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    FragmentManager fragmentManager = getSupportFragmentManager();
    ConstraintLayout mainLayout;
    ConstraintSet includeBarConstraintSet, excludeBarConstraintSet;

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
                    //includeBarConstraintSet.applyTo(mainLayout);
                    return true;
                case R.id.navigation_posts:
                    fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.placeHolderFrameLayout, new PostsFragment())
                            .addToBackStack(BACK_STACK_ROOT_TAG)
                            .commit();
                    //excludeBarConstraintSet.applyTo(mainLayout);
                    return true;
                case R.id.navigation_profile:
                    fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.placeHolderFrameLayout, new SelfProfile())
                            .addToBackStack(BACK_STACK_ROOT_TAG)
                            .commit();
                    //excludeBarConstraintSet.applyTo(mainLayout);
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

        // Prepare constraints for changing size of container framelayout
        mainLayout = findViewById(R.id.container);
        includeBarConstraintSet = new ConstraintSet();
        excludeBarConstraintSet = new ConstraintSet();

        includeBarConstraintSet.clone(mainLayout);
        excludeBarConstraintSet.clone(mainLayout);

        excludeBarConstraintSet.connect(R.id.placeHolderFrameLayout,ConstraintSet.BOTTOM,R.id.navigation,ConstraintSet.TOP,0);
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
                            UserData.getInstance().cleanInstance();
                            Intent intent = new Intent(getApplicationContext(), LogInSignUpOptionsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
