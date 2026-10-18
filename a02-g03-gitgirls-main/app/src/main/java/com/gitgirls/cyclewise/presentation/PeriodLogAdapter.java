package com.gitgirls.cyclewise.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gitgirls.cyclewise.R;
import com.gitgirls.cyclewise.models.PeriodLog;
import com.gitgirls.cyclewise.models.Symptoms;

import com.gitgirls.cyclewise.models.SymptomConstants;

import java.util.ArrayList;
import java.util.List;

public class PeriodLogAdapter extends RecyclerView.Adapter<PeriodLogAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(PeriodLog log);
    }

    private final List<PeriodLog> logs = new ArrayList<>();
    private final OnDeleteClickListener deleteClickListener;

    public PeriodLogAdapter(List<PeriodLog> initialLogs, OnDeleteClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
        setLogs(initialLogs);
    }

    public void setLogs(List<PeriodLog> newLogs) {
        logs.clear();
        if (newLogs != null) {
            logs.addAll(newLogs);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_period_log, parent, false);
        return new ViewHolder(v);
    }

    // onBindViewHolder now include start/end dates, flow, and notes entered from user.
    // Add helper methods to prevent onBindViewHolder from doing too much work - violate S Principle in SOLID.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PeriodLog log = logs.get(position);
        holder.title.setText(formatTitle(holder.itemView, log));
        holder.subtitle.setText(formatSubtitle(holder.itemView, log));
        bindDeleteButton(holder, log);
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    // ----------- Helper methods for onBindViewHolder method ------------------

    private String formatTitle(View view, PeriodLog log) {
        String start = formatStartDate(view, log);
        String end = formatEndDate(view, log);
        String flow = formatFlow(view, log);
        return view.getContext().getString(R.string.log_title_format, start, end, flow);
    }

    private String formatSubtitle(View view, PeriodLog log) {
        String symptoms = formatSymptoms(view, log.getSymptoms());
        String notes = formatNotes(view, log.getNotes());
        return view.getContext().getString(R.string.log_subtitle_format, symptoms, notes);
    }

    private String formatStartDate(View view, PeriodLog log) {
        if (log.getStartDate() == null || log.getStartDate().getDate() == null) {
            return view.getContext().getString(R.string.log_unknown_date);
        }
        return log.getStartDate().getDate().toString();
    }

    private String formatEndDate(View view, PeriodLog log) {
        if (log.getEndDate() == null || log.getEndDate().getDate() == null) {
            return view.getContext().getString(R.string.log_ongoing);
        }
        return log.getEndDate().getDate().toString();
    }

    private String formatFlow(View view, PeriodLog log) {
        return (log.getFlow() == null)
                ? view.getContext().getString(R.string.log_no_flow)
                : FlowDisplayMapper.toDisplayString(view.getContext(), log.getFlow());
    }

    private String formatSymptoms(View view, Symptoms symptoms) {
        List<String> symptomList = new ArrayList<>();
        if (symptoms != null) {
            if (symptoms.hasCramps()) symptomList.add(formatSymptom(view, SymptomConstants.CRAMPS));
            if (symptoms.hasHeadache()) symptomList.add(formatSymptom(view, SymptomConstants.HEADACHE));
            if (symptoms.hasFatigue()) symptomList.add(formatSymptom(view, SymptomConstants.FATIGUE));
            if (symptoms.hasBloating()) symptomList.add(formatSymptom(view, SymptomConstants.BLOATING));
            if (symptoms.hasLowerBackPain()) symptomList.add(formatSymptom(view, SymptomConstants.LOWER_BACK_PAIN));
            if (symptoms.hasNausea()) symptomList.add(formatSymptom(view, SymptomConstants.NAUSEA));
            if (symptoms.hasDizziness()) symptomList.add(formatSymptom(view, SymptomConstants.DIZZINESS));
            if (symptoms.hasRestlessness()) symptomList.add(formatSymptom(view, SymptomConstants.RESTLESSNESS));
            if (symptoms.hasFoodCravings()) symptomList.add(formatSymptom(view, SymptomConstants.FOOD_CRAVINGS));
            if (symptoms.hasBrainFog()) symptomList.add(formatSymptom(view, SymptomConstants.BRAIN_FOG));
            if (symptoms.hasAnxiety()) symptomList.add(formatSymptom(view, SymptomConstants.ANXIETY));
            if (symptoms.hasIrritability()) symptomList.add(formatSymptom(view, SymptomConstants.IRRITABILITY));
        }
        return symptomList.isEmpty() ? view.getContext().getString(R.string.log_none) : String.join(", ", symptomList);
    }

    //Do UI formatting in presentation (replace _ -> space)
    private String formatSymptom(View view, String symptom) {
        return SymptomDisplayMapper.toDisplayString(view.getContext(), symptom);
    }

    private String formatNotes(View view, String notes) {
        return (notes == null || notes.trim().isEmpty()) ? view.getContext().getString(R.string.log_no_notes) : notes;
    }

    private void bindDeleteButton(ViewHolder holder, PeriodLog log) {
        if (deleteClickListener == null) {
            holder.deleteButton.setVisibility(View.GONE);
            holder.deleteButton.setOnClickListener(null);
        } else {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> deleteClickListener.onDeleteClick(log));
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;
        Button deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.logTitle);
            subtitle = itemView.findViewById(R.id.logSubtitle);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}