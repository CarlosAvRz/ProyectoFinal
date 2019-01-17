package com.example.chronos.finalproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import java.util.concurrent.ExecutionException;

import static java.util.Arrays.asList;

import static com.example.chronos.finalproject.AdminMainMenu.IDUser;

public class CreateEvent1stFragment extends Fragment {

    Spinner evDaySpinner, evMonthSpinner, evYearSpinner;
    HashMap<String, Object> eventToEdit;

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
        final Spinner evInitHourSpinner = rootView.findViewById(R.id.evHourSpinner);
        final Spinner evEndHourSpinner = rootView.findViewById(R.id.evMinuteSpinner);

        Button evNextButton = rootView.findViewById(R.id.evNextButton);

        // Inicializar listas de numeros para poder mostrarlos en los spinner
        Integer[] dayNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};
        Integer[] monthNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        Integer[] yearNumbers = {2018, 2019};
        Integer[] hourNumbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};

        // Unir las listas de numeros a los spinner
        ArrayAdapter<Integer> evDayArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dayNumbers);
        evDaySpinner.setAdapter(evDayArrayAdapter);

        ArrayAdapter<Integer> evMonthArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, monthNumbers);
        evMonthSpinner.setAdapter(evMonthArrayAdapter);

        ArrayAdapter<Integer> evYearArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, yearNumbers);
        evYearSpinner.setAdapter(evYearArrayAdapter);

        ArrayAdapter<Integer> evHourArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, hourNumbers);
        evInitHourSpinner.setAdapter(evHourArrayAdapter);
        evEndHourSpinner.setAdapter(evHourArrayAdapter);

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

        // Revisar si se esta editando un mapa, de lo contrario generar uno nuevo para pasarlo a la siguiente actividad
        try {
            eventToEdit = (HashMap<String, Object>) getArguments().getSerializable("EditEvent");
            evNameEditText.setText(eventToEdit.get("Nombre").toString());
            evQuotaEditText.setText(eventToEdit.get("Cupo").toString());

            Integer eventDay = Integer.valueOf(eventToEdit.get("DiaEvento").toString());
            Integer eventMonth = Integer.valueOf(eventToEdit.get("MesEvento").toString());
            Integer eventYear = Integer.valueOf(eventToEdit.get("AnioEvento").toString());
            Integer eventInitHour = Integer.valueOf(eventToEdit.get("HoraInicial").toString());
            Integer eventEndHour = Integer.valueOf(eventToEdit.get("HoraFinal").toString());
            evDaySpinner.setSelection(evDayArrayAdapter.getPosition(eventDay));
            evMonthSpinner.setSelection(evMonthArrayAdapter.getPosition(eventMonth));
            evYearSpinner.setSelection(evYearArrayAdapter.getPosition(eventYear));
            evInitHourSpinner.setSelection(evHourArrayAdapter.getPosition(eventInitHour));
            evEndHourSpinner.setSelection(evHourArrayAdapter.getPosition(eventEndHour));
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                eventHour = evInitHourSpinner.getSelectedItem().toString();
                eventMin = evEndHourSpinner.getSelectedItem().toString();
                if (!eventName.equals("") && !eventOrg.equals("") && !eventQuota.equals("") && evValidDataTextView.getText().toString().equals("")) {
                    Bundle bundle = new Bundle();
                    if (eventToEdit == null) {
                        eventToEdit = new HashMap<>();
                    }
                    eventToEdit.put("Nombre", eventName);
                    eventToEdit.put("NombreEncargado", eventOrg);
                    eventToEdit.put("Cupo", eventQuota);
                    eventToEdit.put("DiaEvento", eventDay);
                    eventToEdit.put("MesEvento", eventMonth);
                    eventToEdit.put("AnioEvento", eventYear);
                    eventToEdit.put("HoraInicial", eventHour);
                    eventToEdit.put("HoraFinal", eventMin);
                    bundle.putSerializable("ValoresEvento", eventToEdit);
                    CreateEvent2ndFragment createEvent2ndFragment = new CreateEvent2ndFragment();
                    createEvent2ndFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.adminPlaceholder, createEvent2ndFragment)
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
