package com.example.chronos.finalproject.User;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chronos.finalproject.Models.DatePickerFragment;
import com.example.chronos.finalproject.Models.UXMethods;
import com.example.chronos.finalproject.Models.UserExperienceMethods;
import com.example.chronos.finalproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class Register1stActivity extends AppCompatActivity {

    EditText nameEditText, lastNameEditText, mLastNameEditText, emailEditText, passwordEditText;
    TextView validNameTextView, validLastNameTextView, validmLastNameTextView, validEmailTextView, validPasswordTextView, validDateTextView, dateTextView;

    public int isValidName(EditText editText) {
        String text = editText.getText().toString();
        if (text.equals("")) {
            return 1; // Empty string
        }
        if (text.contains(" ") && (text.startsWith(" ") || text.endsWith(" "))) {
            return 2; // Starts or ends with whitespace
        }
        String noSpacesText = text.trim().replaceAll(" +", " ");
        if (!text.equals(noSpacesText)) {
            return 3; // Many whitespaces between words
        }
        return 0; // valid name
    }

    public boolean isValidEmail() {
        return (!TextUtils.isEmpty(emailEditText.getText()) && Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText()).matches());
    }

    public boolean isValidPassword() {
        return (passwordEditText.getText().length() >= 8);
    }

    public boolean isValidDate() {
        String dateText = dateTextView.getText().toString();
        return !dateText.equals(getString(R.string.date_text));
    }

    public void continueRegister(View view) {
        if (isValidName(nameEditText) == 0 && isValidName(lastNameEditText) == 0 && isValidName(mLastNameEditText) == 0 && isValidEmail() && isValidPassword() && isValidDate()) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Usuarios");
            final boolean[] userEmailExists = {false};
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Map<String, Object> userValues = (Map<String, Object>) data.getValue();
                            String userEmail = (String) userValues.get("Correo");
                            if (userEmail.equals(emailEditText.getText().toString())) {
                                userEmailExists[0] = true;
                            }
                        }
                        if (userEmailExists[0]) {
                            new AlertDialog.Builder(Register1stActivity.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle(getString(R.string.email_alredy_exists))
                                    .setMessage(getString(R.string.email_alredy_exists_detail))
                                    .setPositiveButton(getString(R.string.ok_option), null)
                                    .show();
                        }
                    }
                    if (!userEmailExists[0]) {
                        Intent intent = new Intent(getApplicationContext(), Register2ndActivity.class);
                        intent.putExtra("name", nameEditText.getText().toString());
                        intent.putExtra("lastName", lastNameEditText.getText().toString());
                        intent.putExtra("mLastName", mLastNameEditText.getText().toString());
                        intent.putExtra("email", emailEditText.getText().toString());
                        intent.putExtra("password", passwordEditText.getText().toString());
                        //intent.putExtra("birthDay", daySpinner.getSelectedItem().toString());
                        //intent.putExtra("birthMonth", monthSpinner.getSelectedItem().toString());
                        //intent.putExtra("birthYear", yearSpinner.getSelectedItem().toString());
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            passwordEditText.requestFocus();
            emailEditText.requestFocus();
            mLastNameEditText.requestFocus();
            lastNameEditText.requestFocus();
            nameEditText.requestFocus();
            getCurrentFocus().clearFocus();
            if (!isValidDate()) {
                validDateTextView.setText(getString(R.string.invalid_date));
            } else {
                validDateTextView.setText("");
            }
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.general_error))
                    .setMessage(getString(R.string.description_error))
                    .setPositiveButton(getString(R.string.ok_option), null)
                    .show();
        }
    }

    public void showDatePicker(View v) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                final String selectedDate = day + " / " + (month + 1) + " / " + year;
                dateTextView.setText(selectedDate);
                dateTextView.setTextColor(ContextCompat.getColor(Register1stActivity.this, android.R.color.white));
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void registerFacebook(View v) {
        Toast.makeText(this, "En desarrollo...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        ev = UXMethods.dispatchTouchEvent(this, ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1st);

        // Inicializar variables para comprobar mas adelante si los datos son correctos
        // EditText
        nameEditText = findViewById(R.id.nameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        mLastNameEditText = findViewById(R.id.mLastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        // TextView
        dateTextView = findViewById(R.id.dateTextView);
        validNameTextView = findViewById(R.id.validNameTextView);
        validLastNameTextView = findViewById(R.id.validLastNameTextView);
        validmLastNameTextView = findViewById(R.id.validmLastNameTextView);
        validEmailTextView = findViewById(R.id.validEmailTextView);
        validPasswordTextView = findViewById(R.id.validPasswordTextView);
        validDateTextView = findViewById(R.id.validDateTextView);

        // FocusListener para validar campos
        // nameEditText
        nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    switch (isValidName(nameEditText)) {
                        case 1:
                            validNameTextView.setText(R.string.empty_error);
                            break;
                        case 2:
                            validNameTextView.setText(R.string.whitespace_eror);
                            break;
                        case 3:
                            validNameTextView.setText(R.string.many_whitespace_error);
                            break;
                        default:
                            validNameTextView.setText("");
                    }
                }
            }
        });
        // lastNameEditText
        lastNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    switch (isValidName(lastNameEditText)) {
                        case 1:
                            validLastNameTextView.setText(R.string.empty_error);
                            break;
                        case 2:
                            validLastNameTextView.setText(R.string.whitespace_eror);
                            break;
                        case 3:
                            validLastNameTextView.setText(R.string.many_whitespace_error);
                            break;
                        default:
                            validLastNameTextView.setText("");
                    }
                }
            }
        });
        // mLastNameEditText
        mLastNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    switch (isValidName(mLastNameEditText)) {
                        case 1:
                            validmLastNameTextView.setText(R.string.empty_error);
                            break;
                        case 2:
                            validmLastNameTextView.setText(R.string.whitespace_eror);
                            break;
                        case 3:
                            validmLastNameTextView.setText(R.string.many_whitespace_error);
                            break;
                        default:
                            validmLastNameTextView.setText("");
                    }
                }
            }
        });
        // emailEditText
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!isValidEmail()) {
                        validEmailTextView.setText(getString(R.string.invalid_email));
                    } else {
                        validEmailTextView.setText("");
                    }
                }
            }
        });
        // passwordEditText. validar tambien al presionar cada tecla
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!isValidPassword()) {
                        validPasswordTextView.setText(getString(R.string.invalid_password));
                    } else {
                        validPasswordTextView.setText("");
                    }
                }
            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isValidPassword()) {
                    validPasswordTextView.setText(getString(R.string.invalid_password));
                } else {
                    validPasswordTextView.setText("");
                }
            }
        });
    }
}