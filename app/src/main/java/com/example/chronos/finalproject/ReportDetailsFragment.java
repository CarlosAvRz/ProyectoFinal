package com.example.chronos.finalproject;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ReportDetailsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inicializar fragmento y elementos de la UI
        View rootView = inflater.inflate(R.layout.fragment_report_details, container, false);

        TextView reportTypeTextView = rootView.findViewById(R.id.reportTypeTextView);
        TextView detOffendedTextView = rootView.findViewById(R.id.detOffendedTextView);
        TextView detOffenderTextView = rootView.findViewById(R.id.detOffenderTextView);
        TextView detContentTextView = rootView.findViewById(R.id.detContentTextView);

        Button warningButton = rootView.findViewById(R.id.warningButton);
        Button deleteButton = rootView.findViewById(R.id.deleteButton);

        // Variables para determinar donde colocar advertencia o a quien bloquear
        HashMap<String, Object> reportInfo = (HashMap<String, Object>) getArguments().getSerializable("ReportInfo");
        final String reportID = getArguments().getString("ReportID");

        String IDOffended = reportInfo.get("IDUsuario").toString();
        final String IDOffender = reportInfo.get("IDReportado").toString();

        // Colocar mensajes a los textView
        reportTypeTextView.setText(reportInfo.get("Tipo").toString());
        detOffendedTextView.setText(getString(R.string.offended_user) + " " + IDOffended);
        detOffenderTextView.setText(getString(R.string.offender_user) + " " + IDOffender);
        detContentTextView.setText(getString(R.string.report_reason) + " " + reportInfo.get("Contenido").toString());

        // Click para boton de advertencia
        warningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference warningRef = FirebaseDatabase.getInstance().getReference("Usuarios/" + IDOffender + "/Advertencias");
                warningRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Integer usersWarnings = 1;
                        if (dataSnapshot.getValue() != null) {
                            usersWarnings += (Integer) dataSnapshot.getValue();
                        }
                        warningRef.setValue(usersWarnings);
                        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("Reportes/" + reportID);
                        reportRef.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ReportsAdminFragment reportsAdminFragment = new ReportsAdminFragment();
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.adminPlaceholder, reportsAdminFragment)
                                        .addToBackStack(null)
                                        .commit();
                                Toast.makeText(getContext(), getString(R.string.warning_added), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        // Click para boton de eliminar cuenta
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference("Usuarios/" + IDOffender + "/Advertencias");
                Integer usersWarnings = 3;
                deleteRef.setValue(usersWarnings);
                DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("Reportes/" + reportID);
                reportRef.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ReportsAdminFragment reportsAdminFragment = new ReportsAdminFragment();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.adminPlaceholder, reportsAdminFragment)
                                .addToBackStack(null)
                                .commit();
                        Toast.makeText(getContext(), getString(R.string.user_deleted), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return rootView;
    }

}
