package com.example.ecotrack;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
    private List<Report> reports;
    private Context context;

    public ReportAdapter(List<Report> reports, Context context) {
        this.reports = reports;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Report report = reports.get(position);

        // Display report ID
        holder.reportId.setText(report.getId());

        // For different report types, display different data
        if ("App Issue".equals(report.getReportType())) {
            holder.reportDate.setText("N/A");  // App Issue might not have a date
        } else {
            holder.reportDate.setText(report.getDate());
        }

        holder.viewDetailsButton.setOnClickListener(v -> showDetailsDialog(report));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView reportId, reportDate;
        Button viewDetailsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reportId = itemView.findViewById(R.id.tvReportId);
            reportDate = itemView.findViewById(R.id.tvReportDate);
            viewDetailsButton = itemView.findViewById(R.id.btnViewDetails);
        }
    }

    private void showDetailsDialog(Report report) {
        String message = "Report Type: " + report.getReportType() + "\n"
                + "ID: " + report.getId() + "\n"
                + "Date: " + report.getDate() + "\n"
                + "Email: " + report.getEmail() + "\n"
                + "City: " + report.getCity() + "\n"
                + "State: " + report.getState() + "\n"
                + "Problem: " + report.getProblem() + "\n"
                + "Status: " + report.getStatus();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Report Details")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}



