package com.example.chronos.finalproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

        HashMap<String, Object> reportInfo = (HashMap<String, Object>) getArguments().getSerializable("ReportInfo");
        String reportID = getArguments().getString("ReportID");

        // Colocar mensajes a los textView
        reportTypeTextView.setText(reportInfo.get("reportType").toString());
        detOffendedTextView.setText(reportInfo.get("offendedUser").toString());
        detOffenderTextView.setText(reportInfo.get("offenderUser").toString());
        detContentTextView.setText(getString(R.string.report_reason) + reportInfo.get("reportContent").toString());

        // Click para boton de advertencia
        warningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Click para boton de eliminar cuenta
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rootView;
    }

}
