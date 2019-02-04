package com.example.chronos.finalproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static java.util.Arrays.asList;

public class EditProfile1stFragment extends Fragment {

    Spinner edDaySpinner, edMonthSpinner, edYearSpinner;
    EditText edNameEditText, edLastNameEditText, edMLastNameEditText, edEmailEditText, edPasswordEditText;
    TextView edValidNameTextView, edValidLastNameTextView, edValidmLastNameTextView, edValidEmailTextView,
            edValidPasswordTextView, edValidDateTextView, edDayTextView, edMonthTextView, edYearTextView;

    public int isValidName(EditText editText) {
        String text = editText.getText().toString();
        if (text.equals("")) {
            return 1; // Cadena Vacia
        }
        if (text.contains(" ") && (text.startsWith(" ") || text.endsWith(" "))) {
            return 2; // Espacios al inicio o al final
        }
        String noSpacesText = text.trim().replaceAll(" +", " ");
        if (!text.equals(noSpacesText)) {
            return 3; // espacios entre palabras
        }
        return 0; // nombre valido
    }

    public boolean isValidEmail() {
        return (!TextUtils.isEmpty(edEmailEditText.getText()) && Patterns.EMAIL_ADDRESS.matcher(edEmailEditText.getText()).matches());
    }

    public boolean isValidPassword() {
        return (edPasswordEditText.getText().length() >= 8);
    }

    public boolean isValidDate() {
        String day = edDaySpinner.getSelectedItem().toString();
        String month = edMonthSpinner.getSelectedItem().toString();
        String year = edYearSpinner.getSelectedItem().toString();
        ArrayList<String> months31 = new ArrayList<>(asList("1", "3", "5", "7", "8", "10", "12"));

        if (day.equals("31") && !months31.contains(month)) {
            return false;
        }
        if (day.equals("30") && month.equals("2")) {
            return false;
        }
        if (day.equals("29") && month.equals("2")) {
            if (Integer.valueOf(year) % 4 != 0) {
                return false;
            } else if (Integer.valueOf(year) % 100 == 0) {
                if (Integer.valueOf(year) % 400 != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inicializar fragmento y elementos de la UI
        View rootView = inflater.inflate(R.layout.fragment_edit_profile1st, container, false);

        // Inicializar variables para comprobar mas adelante si los datos son correctos
        // Spinner
        edDaySpinner = rootView.findViewById(R.id.edDaySpinner);
        edMonthSpinner = rootView.findViewById(R.id.edMonthSpinner);
        edYearSpinner = rootView.findViewById(R.id.edYearSpinner);

        // EditText
        edNameEditText = rootView.findViewById(R.id.edNameEditText);
        edLastNameEditText = rootView.findViewById(R.id.edLastNameEditText);
        edMLastNameEditText = rootView.findViewById(R.id.edMLastNameEditText);
        edEmailEditText = rootView.findViewById(R.id.edEmailEditText);
        edPasswordEditText = rootView.findViewById(R.id.edPasswordEditText);

        // TextView
        edValidNameTextView = rootView.findViewById(R.id.edValidNameTextView);
        edValidLastNameTextView = rootView.findViewById(R.id.edValidLastNameTextView);
        edValidmLastNameTextView = rootView.findViewById(R.id.edValidmLastNameTextView);
        edValidEmailTextView = rootView.findViewById(R.id.edValidEmailTextView);
        edValidPasswordTextView = rootView.findViewById(R.id.edValidPasswordTextView);
        edValidDateTextView = rootView.findViewById(R.id.edValidDateTextView);
        edDayTextView = rootView.findViewById(R.id.edDayTextView);
        edMonthTextView = rootView.findViewById(R.id.edMonthTextView);
        edYearTextView = rootView.findViewById(R.id.edYearTextView);

        // Button
        final Button edNextButton = rootView.findViewById(R.id.edNextButton);

        // parent Layout
        LinearLayout parentLayout = rootView.findViewById(R.id.editProf1LinearLayout);

        // Inicializar listas de numeros para poder mostrarlos en los spinner
        Integer[] dayNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};
        Integer[] monthNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        Integer[] yearNumbers = new Integer[39];
        for (int i = 0, year = 1980; i < yearNumbers.length; i++, year++) {
            yearNumbers[i] = year;
        }

        // Unir las listas de numeros a los spinner
        final ArrayAdapter<Integer> edDayArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dayNumbers);
        edDaySpinner.setAdapter(edDayArrayAdapter);
        final ArrayAdapter<Integer> edMonthArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, monthNumbers);
        edMonthSpinner.setAdapter(edMonthArrayAdapter);
        final ArrayAdapter<Integer> edYearArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, yearNumbers);
        edYearSpinner.setAdapter(edYearArrayAdapter);

        // FocusListener para validar campos
        // nameEditText
        edNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    switch (isValidName(edNameEditText)) {
                        case 1:
                            edValidNameTextView.setText(R.string.empty_error);
                            break;
                        case 2:
                            edValidNameTextView.setText(R.string.whitespace_eror);
                            break;
                        case 3:
                            edValidNameTextView.setText(R.string.many_whitespace_error);
                            break;
                        default:
                            edValidNameTextView.setText("");
                    }
                }
            }
        });
        // lastNameEditText
        edLastNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    switch (isValidName(edLastNameEditText)) {
                        case 1:
                            edValidLastNameTextView.setText(R.string.empty_error);
                            break;
                        case 2:
                            edValidLastNameTextView.setText(R.string.whitespace_eror);
                            break;
                        case 3:
                            edValidLastNameTextView.setText(R.string.many_whitespace_error);
                            break;
                        default:
                            edValidLastNameTextView.setText("");
                    }
                }
            }
        });
        // mLastNameEditText
        edMLastNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    switch (isValidName(edMLastNameEditText)) {
                        case 1:
                            edValidmLastNameTextView.setText(R.string.empty_error);
                            break;
                        case 2:
                            edValidmLastNameTextView.setText(R.string.whitespace_eror);
                            break;
                        case 3:
                            edValidmLastNameTextView.setText(R.string.many_whitespace_error);
                            break;
                        default:
                            edValidmLastNameTextView.setText("");
                    }
                }
            }
        });
        // emailEditText
        edEmailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!isValidEmail()) {
                        edValidEmailTextView.setText(getString(R.string.invalid_email));
                    } else {
                        edValidEmailTextView.setText("");
                    }
                }
            }
        });
        // passwordEditText. validar tambien al presionar cada tecla
        edPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!isValidPassword()) {
                        edValidPasswordTextView.setText(getString(R.string.invalid_password));
                    } else {
                        edValidPasswordTextView.setText("");
                    }
                }
            }
        });
        edPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isValidPassword()) {
                    edValidPasswordTextView.setText(getString(R.string.invalid_password));
                } else {
                    edValidPasswordTextView.setText("");
                }
            }
        });
        // Spinners (solo OnItemSelected)
        Spinner.OnItemSelectedListener spinnerOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isValidDate()) {
                    edValidDateTextView.setText(getString(R.string.invalid_date));
                } else {
                    edValidDateTextView.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        edDaySpinner.setOnItemSelectedListener(spinnerOnItemSelectedListener);
        edMonthSpinner.setOnItemSelectedListener(spinnerOnItemSelectedListener);
        edYearSpinner.setOnItemSelectedListener(spinnerOnItemSelectedListener);

        // Ocultar teclado clicklistener para textViews y touchListener para spinners
        View.OnClickListener keyboardOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(edValidNameTextView);
            }
        };
        parentLayout.setOnClickListener(keyboardOnClickListener);

        edValidNameTextView.setOnClickListener(keyboardOnClickListener);
        edValidLastNameTextView.setOnClickListener(keyboardOnClickListener);
        edValidmLastNameTextView.setOnClickListener(keyboardOnClickListener);
        edValidEmailTextView.setOnClickListener(keyboardOnClickListener);
        edValidPasswordTextView.setOnClickListener(keyboardOnClickListener);
        edValidDateTextView.setOnClickListener(keyboardOnClickListener);
        edDayTextView.setOnClickListener(keyboardOnClickListener);
        edMonthTextView.setOnClickListener(keyboardOnClickListener);
        edYearTextView.setOnClickListener(keyboardOnClickListener);

        View.OnTouchListener keyboardOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(edValidNameTextView);
                v.performClick();
                return true;
            }
        };
        edDaySpinner.setOnTouchListener(keyboardOnTouchListener);
        edMonthSpinner.setOnTouchListener(keyboardOnTouchListener);
        edYearSpinner.setOnTouchListener(keyboardOnTouchListener);

        // Descarga de datos previos
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Usuarios/" + UserData.getInstance().getUserId());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> userData = (HashMap<String, Object>) dataSnapshot.getValue();
                edNameEditText.setText(userData.get("Nombre").toString());
                edLastNameEditText.setText(userData.get("ApellidoPat").toString());
                edMLastNameEditText.setText(userData.get("ApellidoMat").toString());
                edEmailEditText.setText(userData.get("Correo").toString());
                edPasswordEditText.setText(userData.get("Contrasenia").toString());

                Integer birthDay = Integer.valueOf(userData.get("DiaNac").toString());
                Integer birthMonth = Integer.valueOf(userData.get("MesNac").toString());
                Integer birthYear = Integer.valueOf(userData.get("AnioNac").toString());
                edDaySpinner.setSelection(edDayArrayAdapter.getPosition(birthDay));
                edMonthSpinner.setSelection(edMonthArrayAdapter.getPosition(birthMonth));
                edYearSpinner.setSelection(edYearArrayAdapter.getPosition(birthYear));

                edNextButton.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // boton para validar datos y pasar a la siguiente actividad
        edNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidName(edNameEditText) == 0 && isValidName(edLastNameEditText) == 0 && isValidName(edMLastNameEditText) == 0 && isValidEmail() && isValidPassword() && isValidDate()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", edNameEditText.getText().toString());
                    bundle.putString("lastName", edLastNameEditText.getText().toString());
                    bundle.putString("mLastName", edMLastNameEditText.getText().toString());
                    bundle.putString("email", edEmailEditText.getText().toString());
                    bundle.putString("password", edPasswordEditText.getText().toString());
                    bundle.putString("birthDay", edDaySpinner.getSelectedItem().toString());
                    bundle.putString("birthMonth", edMonthSpinner.getSelectedItem().toString());
                    bundle.putString("birthYear", edYearSpinner.getSelectedItem().toString());
                    EditProfile2ndFragment editProfile2ndFragment = new EditProfile2ndFragment();
                    editProfile2ndFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.placeHolderFrameLayout, editProfile2ndFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    edPasswordEditText.requestFocus();
                    edEmailEditText.requestFocus();
                    edMLastNameEditText.requestFocus();
                    edLastNameEditText.requestFocus();
                    edNameEditText.requestFocus();
                    getActivity().getCurrentFocus().clearFocus();
                    if (!isValidDate()) {
                        edValidDateTextView.setText(getString(R.string.invalid_date));
                    } else {
                        edValidDateTextView.setText("");
                    }
                    new AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(getString(R.string.general_error))
                            .setMessage(getString(R.string.description_error))
                            .setPositiveButton(getString(R.string.ok_option), null)
                            .show();
                }
            }
        });

        return rootView;
    }

}
