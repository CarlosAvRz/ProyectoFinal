package com.example.chronos.finalproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ReportsAdminFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inicializar fragmento y elementos de la UI
        View rootView = inflater.inflate(R.layout.fragment_reports_admin, container, false);

        final ArrayList<String> IDReports = new ArrayList<>();
        final ArrayList<HashMap<String, Object>> reportsFullInfo= new ArrayList<>();

        final ArrayList<HashMap<String, String>> reportsList = new ArrayList<>();
        ListView reportsListView = rootView.findViewById(R.id.reportsListView);
        final ListAdapter prevEvListAdapter = new SimpleAdapter(
                getContext(),
                reportsList,
                R.layout.reports_list_item,
                new String[]{"offendedUser","offenderUser", "reportType", "reportContent"},
                new int[]{R.id.offendedUser,R.id.offenderUser, R.id.reportType, R.id.reportContent});
        reportsListView.setAdapter(prevEvListAdapter);

        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("Reportes");
        reportsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, Object> singleReport = (HashMap<String, Object>) data.getValue();
                        IDReports.add(data.getKey());
                        reportsFullInfo.add(singleReport);
                        HashMap<String, String> newReport = new HashMap<>();
                        newReport.put("offendedUser", getString(R.string.offended_user) + " " + singleReport.get("IDUsuario").toString());
                        newReport.put("offenderUser", getString(R.string.offender_user) + " " + singleReport.get("IDReportado").toString());
                        newReport.put("reportType", singleReport.get("Tipo").toString());
                        newReport.put("reportContent", singleReport.get("Contenido").toString());
                        reportsList.add(newReport);
                        ((SimpleAdapter) prevEvListAdapter).notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Click para mostrar los detalles y permitir realizar desiciones
        reportsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("ReportID", IDReports.get(position));
                bundle.putSerializable("ReportInfo", reportsFullInfo.get(position));
                ReportDetailsFragment reportDetailsFragment = new ReportDetailsFragment();
                reportDetailsFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.adminPlaceholder, reportDetailsFragment)
                        .addToBackStack(null)
                        .commit();

            }
        });

        return rootView;
    }

}
