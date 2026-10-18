package com.gitgirls.cyclewise.presentation;
import com.gitgirls.cyclewise.presentation.notification.NotificationSettingsActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import android.app.DatePickerDialog;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.res.ColorStateList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitgirls.cyclewise.R;

import com.gitgirls.cyclewise.exceptions.PeriodLogException;
import com.gitgirls.cyclewise.exceptions.InvalidPeriodLogException;
import com.gitgirls.cyclewise.models.*;
import com.gitgirls.cyclewise.application.PeriodTracker;
import com.gitgirls.cyclewise.logic.periodlogging.PeriodLogService;
import com.gitgirls.cyclewise.models.SymptomConstants;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.widget.TextView;
import android.content.Intent;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import android.view.View;

import android.util.TypedValue;
import java.time.format.DateTimeFormatter;

public class PeriodLogActivity extends AppCompatActivity {

    private PeriodLogService periodLogService;
    private TextView startDateTextView;
    private TextView endDateTextView;
    private Button saveLogButton;

    private android.widget.CheckBox noEndDateCheck;

    private EditText notesInput;
    private PeriodLogAdapter adapter;

    private java.time.LocalDate selectedStartDate = null;
    private java.time.LocalDate selectedEndDate = null;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private MaterialButton flowLight;
    private MaterialButton flowMedium;
    private MaterialButton flowHeavy;
    private Flow selectedFlow = null;

    private LinearLayout symptomsClickableArea;
    private TextView symptomsSelectedText;
    private ChipGroup symptomsChipGroup;
    private String[] symptomItems;

    private final List<String> symptomKeys = SymptomConstants.ALL_SYMPTOMS;
    private boolean[] selectedSymptoms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_period_log);

        periodLogService = ((PeriodTracker) getApplication()).getPeriodLogService();

        startDateTextView = findViewById(R.id.startDateTextView);
        endDateTextView = findViewById(R.id.endDateTextView);
        noEndDateCheck = findViewById(R.id.noEndDateCheck);
        saveLogButton = findViewById(R.id.saveLogButton);

        selectedStartDate = null;
        startDateTextView.setText(getString(R.string.tap_to_select));

        flowLight = findViewById(R.id.flowLight);
        flowMedium = findViewById(R.id.flowMedium);
        flowHeavy = findViewById(R.id.flowHeavy);

        flowLight.setOnClickListener(v -> selectFlow(Flow.LIGHT, flowLight));
        flowMedium.setOnClickListener(v -> selectFlow(Flow.MEDIUM, flowMedium));
        flowHeavy.setOnClickListener(v -> selectFlow(Flow.HEAVY, flowHeavy));

        symptomsClickableArea = findViewById(R.id.symptomsClickableArea);
        symptomsSelectedText = findViewById(R.id.symptomsSelectedText);
        symptomsChipGroup = findViewById(R.id.symptomsChipGroup);

        symptomItems = getResources().getStringArray(R.array.symptoms);
        selectedSymptoms = new boolean[symptomItems.length];

        symptomsClickableArea.setOnClickListener(v -> showSymptomsDialog());
        updateSymptomsSummary();

        notesInput = findViewById(R.id.notesInput);

        RecyclerView list = findViewById(R.id.periodLogList);
        adapter = new PeriodLogAdapter(java.util.Collections.emptyList(), null);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        startDateTextView.setOnClickListener(v -> pickDate(true));
        endDateTextView.setOnClickListener(v -> pickDate(false));

        noEndDateCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            endDateTextView.setEnabled(!isChecked);
            if (isChecked) {
                selectedEndDate = null;
                endDateTextView.setText(getString(R.string.log_ongoing));
            } else {
                endDateTextView.setText(getString(R.string.tap_to_select));
            }
        });

        saveLogButton.setOnClickListener(v -> {
            try {
                addPeriodLog();
                clearInputs();
                refreshList();
                Toast.makeText(this, R.string.period_log_saved, Toast.LENGTH_SHORT).show();
            } catch (InvalidPeriodLogException ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            } catch (PeriodLogException ex) {
                String errorMsg = getString(R.string.error_save_failed, ex.getMessage());
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            } catch (IllegalStateException | IllegalArgumentException ex) {
                Toast.makeText(this, R.string.error_unexpected, Toast.LENGTH_LONG).show();
            }
        });

        refreshList();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_log);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_log) {
                return true;
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, SavedLogsActivity.class));
                return true;
            } else if (id == R.id.nav_predict) {
                startActivity(new Intent(this, PredictionActivity.class));
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, NotificationSettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    private void pickDate(boolean isStart) {
        LocalDate initial = LocalDate.now();
        if (isStart && selectedStartDate != null) {
            initial = selectedStartDate;
        } else if (!isStart && selectedEndDate != null) {
            initial = selectedEndDate;
        }

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    LocalDate chosen = LocalDate.of(year, month + 1, dayOfMonth);
                    if (isStart) {
                        selectedStartDate = chosen;
                        startDateTextView.setText(chosen.format(dateFormatter));
                    } else {
                        selectedEndDate = chosen;
                        endDateTextView.setText(chosen.format(dateFormatter));
                    }
                },
                initial.getYear(),
                initial.getMonthValue() - 1,
                initial.getDayOfMonth()
        );

        dialog.show();
    }

    private void selectFlow(Flow flow, MaterialButton selectedButton) {
        this.selectedFlow = flow;

        resetFlowButtonUI(flowLight);
        resetFlowButtonUI(flowMedium);
        resetFlowButtonUI(flowHeavy);

        selectedButton.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.pink_3)));
        selectedButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
    }

    private void resetFlowButtonUI(MaterialButton button) {
        button.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(this, android.R.color.transparent)));
        button.setTextColor(ContextCompat.getColor(this, R.color.pink_3));
    }

    private boolean symptomSelected(String key) {
        int index = symptomKeys.indexOf(key);
        return index >= 0 && selectedSymptoms[index];
    }

    private Symptoms buildSelectedSymptoms() {
        return new Symptoms(
                symptomSelected(SymptomConstants.CRAMPS),
                symptomSelected(SymptomConstants.HEADACHE),
                symptomSelected(SymptomConstants.FATIGUE),
                symptomSelected(SymptomConstants.BLOATING),
                symptomSelected(SymptomConstants.LOWER_BACK_PAIN),
                symptomSelected(SymptomConstants.NAUSEA),
                symptomSelected(SymptomConstants.DIZZINESS),
                symptomSelected(SymptomConstants.RESTLESSNESS),
                symptomSelected(SymptomConstants.FOOD_CRAVINGS),
                symptomSelected(SymptomConstants.BRAIN_FOG),
                symptomSelected(SymptomConstants.ANXIETY),
                symptomSelected(SymptomConstants.IRRITABILITY)
        );
    }

    private void addPeriodLog() throws InvalidPeriodLogException, PeriodLogException {
        if (selectedStartDate == null) {
            throw new InvalidPeriodLogException(getString(R.string.error_select_start_date));
        }

        if (selectedFlow == null) {
            throw new InvalidPeriodLogException(getString(R.string.error_select_flow_intensity));
        }

        Symptoms symptoms = buildSelectedSymptoms();

        CycleDate start = new CycleDate(selectedStartDate);
        CycleDate end = (selectedEndDate == null) ? null : new CycleDate(selectedEndDate);

        PeriodLog log = new PeriodLog(
                PeriodLog.UNASSIGNED_ID,
                start,
                end,
                selectedFlow,
                symptoms,
                notesInput.getText().toString()
        );

        periodLogService.addLog(log);
    }

    private void refreshList() {
        List<PeriodLog> logs = periodLogService.getAllLogs();
        if (logs == null) {
            logs = new ArrayList<>();
        }

        adapter.setLogs(logs);
    }

    private void clearInputs() {
        selectedStartDate = null;
        startDateTextView.setText(getString(R.string.tap_to_select));

        selectedEndDate = null;
        endDateTextView.setText(getString(R.string.tap_to_select));

        noEndDateCheck.setChecked(false);
        endDateTextView.setEnabled(true);

        selectedFlow = null;
        resetFlowButtonUI(flowLight);
        resetFlowButtonUI(flowMedium);
        resetFlowButtonUI(flowHeavy);

        for (int i = 0; i < selectedSymptoms.length; i++) {
            selectedSymptoms[i] = false;
        }
        updateSymptomsSummary();

        notesInput.setText("");
    }

    private void showSymptomsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.PinkDialogTheme);

        builder.setTitle(R.string.select_symptoms);

        builder.setMultiChoiceItems(symptomItems, selectedSymptoms, (dialog, which, isChecked) -> {
            selectedSymptoms[which] = isChecked;
        });

        builder.setPositiveButton(R.string.dialog_ok, (dialog, which) -> updateSymptomsSummary());
        builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.dismiss());
        builder.setNeutralButton(R.string.dialog_clear, (dialog, which) -> {
            for (int i = 0; i < selectedSymptoms.length; i++) {
                selectedSymptoms[i] = false;
            }
            updateSymptomsSummary();
        });

        AlertDialog dialog = builder.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(this, R.color.pink_3));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(this, R.color.pink_3));
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(
                ContextCompat.getColor(this, R.color.pink_3));
    }

    private void updateSymptomsSummary() {
        symptomsChipGroup.removeAllViews();
        boolean hasSelection = false;

        for (int i = 0; i < symptomItems.length; i++) {
            if (selectedSymptoms[i]) {
                hasSelection = true;

                Chip chip = new Chip(this);
                chip.setText(symptomItems[i]);
                chip.setClickable(false);
                chip.setCheckable(false);

                chip.setChipBackgroundColor(ColorStateList.valueOf(
                        ContextCompat.getColor(this, android.R.color.transparent)));
                chip.setChipStrokeColor(ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.pink_3)));
                chip.setChipStrokeWidth(3f);
                chip.setTextColor(ContextCompat.getColor(this, R.color.pink_3));

                float cornerRadius = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                chip.setChipCornerRadius(cornerRadius);

                symptomsChipGroup.addView(chip);
            }
        }

        if (hasSelection) {
            symptomsSelectedText.setVisibility(View.GONE);
            symptomsChipGroup.setVisibility(View.VISIBLE);
        } else {
            symptomsSelectedText.setVisibility(View.VISIBLE);
            symptomsSelectedText.setText(getString(R.string.tap_to_select));
            symptomsChipGroup.setVisibility(View.GONE);
        }
    }
}