package com.example.chronos.finalproject.Models;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

public class UserExperienceMethods {

    public static void hideKeyboard(@NonNull Activity activity) {
        // Revisar si algun view tiene focus
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // TODO: reemplazar todas las toast por la clase centrada (evita varias lineas de codigo)
    public static void showToast(Context context, String message, int flag) {
        Toast toast = Toast.makeText(context, message, flag);
        TextView textView = toast.getView().findViewById(android.R.id.message);
        textView.setGravity(Gravity.CENTER);
        toast.show();
    }
}
