package com.example.ecotrack.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecotrack.FeedbackActivity;
import com.example.ecotrack.MyReport;
import com.example.ecotrack.R;
import com.example.ecotrack.ReportOverflowingBins;
import com.example.ecotrack.RequestSpecialWastePickup;

public class EcoServicesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_eco_services, container, false);

        //set onclicklistener for every button
        view.findViewById(R.id.reportoverflowingbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReportOverflowingBins.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.requestbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RequestSpecialWastePickup.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.myReportBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyReport.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.feedbackbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}