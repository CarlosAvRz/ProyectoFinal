package com.example.chronos.finalproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static java.util.Arrays.asList;

import static com.example.chronos.finalproject.AdminMainMenu.IDUser;

public class CreateEvent1stFragment extends Fragment {

    Spinner evDaySpinner, evMonthSpinner, evYearSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inicializar fragmento y elementos de la UI
        View rootView = inflater.inflate(R.layout.fragment_create_event1st, container, false);

        final EditText evNameEditText = rootView.findViewById(R.id.evNameEditText);
        final EditText evOrgEditText = rootView.findViewById(R.id.evOrgEditText);
        evOrgEditText.setText(IDUser);
        final EditText evQuotaEditText = rootView.findViewById(R.id.evQuotaEditText);

        final TextView evValidDataTextView = rootView.findViewById(R.id.evValidDataTextView);

        final Spinner evDaySpinner = rootView.findViewById(R.id.evDaySpinner);
        final Spinner evMonthSpinner = rootView.findViewById(R.id.evMonthSpinner);
        final Spinner evYearSpinner = rootView.findViewById(R.id.evYearSpinner);
        final Spinner evHourSpinner = rootView.findViewById(R.id.evHourSpinner);
        final Spinner evMinuteSpinner = rootView.findViewById(R.id.evMinuteSpinner);

        Button evNextButton = rootView.findViewById(R.id.evNextButton);

        // Inicializar listas de numeros para poder mostrarlos en los spinner
        Integer[] dayNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};
        Integer[] monthNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        Integer[] yearNumbers = new Integer[39];
        for (int i = 0, year = 1980; i < yearNumbers.length; i++, year++) {
            yearNumbers[i] = year;
        }
        Integer[] hourNumbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
        Integer[] minuteNumbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
                31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59};

        // Unir las listas de numeros a los spinner
        evDaySpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dayNumbers));
        evMonthSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, monthNumbers));
        evYearSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, yearNumbers));
        evHourSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, hourNumbers));
        evMinuteSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, minuteNumbers));

        // Spinners (solo OnItemSelected)
        Spinner.OnItemSelectedListener spinnerOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isValidDate = true;
                String day = evDaySpinner.getSelectedItem().toString();
                String month = evMonthSpinner.getSelectedItem().toString();
                String year = evYearSpinner.getSelectedItem().toString();
                ArrayList<String> months31 = new ArrayList<>(asList("1", "3", "5", "7", "8", "10", "12"));

                if (day.equals("31") && !months31.contains(month)) {
                    isValidDate = false;
                }
                if (day.equals("30") && month.equals("2")) {
                    isValidDate = false;
                }
                if (day.equals("29") && month.equals("2")) {
                    if (Integer.valueOf(year) % 4 != 0) {
                        isValidDate = false;
                    } else if (Integer.valueOf(year) % 100 == 0) {
                        if (Integer.valueOf(year) % 400 != 0) {
                            isValidDate = false;
                        }
                    }
                }

                if (!isValidDate) {
                    evValidDataTextView.setText(getString(R.string.invalid_date));
                } else {
                    evValidDataTextView.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        evDaySpinner.setOnItemSelectedListener(spinnerOnItemSelectedListener);
        evMonthSpinner.setOnItemSelectedListener(spinnerOnItemSelectedListener);
        evYearSpinner.setOnItemSelectedListener(spinnerOnItemSelectedListener);

        // Funcion para revisar campos y pasar a la siguiente pantalla de informacion
        evNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName, eventOrg, eventQuota,eventDay, eventMonth, eventYear, eventHour, eventMin;
                eventName = evNameEditText.getText().toString();
                eventOrg = evOrgEditText.getText().toString();
                eventQuota = evQuotaEditText.getText().toString();
                eventDay = evDaySpinner.getSelectedItem().toString();
                eventMonth = evMonthSpinner.getSelectedItem().toString();
                eventYear = evYearSpinner.getSelectedItem().toString();
                eventHour = evHourSpinner.getSelectedItem().toString();
                eventMin = evMinuteSpinner.getSelectedItem().toString();
                if (!eventName.equals("") && !eventOrg.equals("") && !eventQuota.equals("") && evValidDataTextView.getText().toString().equals("")) {
                    Bundle bundle = new Bundle();
                    HashMap<String, Object> newEvent = new HashMap<>();
                    newEvent.put("Nombre", eventName);
                    newEvent.put("NombreEncargado", eventOrg);
                    newEvent.put("Cupo", eventQuota);
                    newEvent.put("DiaEvento", eventDay);
                    newEvent.put("MesEvento", eventMonth);
                    newEvent.put("AnioEvento", eventYear);
                    newEvent.put("HoraInicial", eventHour);
                    newEvent.put("HoraFinal", eventMin);
                    bundle.putSerializable("ValoresEvento", newEvent);
                    LocationEventFragment locationEventFragment = new LocationEventFragment();
                    locationEventFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.adminPlaceholder, locationEventFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
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
