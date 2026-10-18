package com.gitgirls.cyclewise.presentation.notification;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.gitgirls.cyclewise.logic.notifications.SchedulerConstants;
import com.gitgirls.cyclewise.logic.notifications.SchedulerService;
import com.google.android.material.switchmaterial.SwitchMaterial;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.gitgirls.cyclewise.R;
import com.gitgirls.cyclewise.application.PeriodTracker;
import com.gitgirls.cyclewise.logic.cycleprediction.CycleConversionService;
import com.gitgirls.cyclewise.logic.cycleprediction.CycleHistory;
import com.gitgirls.cyclewise.logic.cycleprediction.PeriodCalculator;
import com.gitgirls.cyclewise.models.Cycle;
import com.gitgirls.cyclewise.models.NotificationSettings;
import com.gitgirls.cyclewise.models.PeriodLog;
import com.gitgirls.cyclewise.presentation.PeriodLogActivity;
import com.gitgirls.cyclewise.presentation.PredictionActivity;
import com.gitgirls.cyclewise.presentation.SavedLogsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalTime;
import java.util.List;

public class NotificationSettingsActivity extends AppCompatActivity {
    private SwitchMaterial switchEnabled;
    private EditText editDaysBefore;

    private TimePicker timePicker;
    private Button btnSave;

    private String username;
    SchedulerService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        username = ((PeriodTracker) getApplication()).getLoggedInUser().getUsername();
        service = PeriodTracker.getSchedulerService(this);

        switchEnabled       = findViewById(R.id.switchEnabled);
        editDaysBefore      = findViewById(R.id.editDaysBefore);
        timePicker          = findViewById(R.id.timePicker);
        btnSave             = findViewById(R.id.btnSave);

        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        loadExistingSettings();
        btnSave.setOnClickListener(v -> saveSettings());

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_settings);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_settings) {
                return true;
            } else if (id == R.id.nav_log) {
                startActivity(new Intent(this, PeriodLogActivity.class));
                return true;
            } else if (id == R.id.nav_predict) {
                startActivity(new Intent(this, PredictionActivity.class));
                return true;
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, SavedLogsActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadExistingSettings() {
        PeriodTracker app = (PeriodTracker) getApplication();
        service = app.getSchedulerService(this);

        // Get data through the Service
        NotificationSettings settings = service.getSettings(username);

        if (settings != null) {
            switchEnabled.setChecked(settings.isEnabled());
            editDaysBefore.setText(String.valueOf(settings.getDaysBeforeReminder()));
            timePicker.setHour(settings.getReminderTime().getHour());
            timePicker.setMinute(settings.getReminderTime().getMinute());
        }
    }

    private void saveSettings() {
        String daysStr = editDaysBefore.getText().toString().trim();
        if (daysStr.isEmpty()) {
            editDaysBefore.setError(SchedulerConstants.ERROR_DAYS_REQUIRED);
            return;
        }

        int days = Integer.parseInt(daysStr);
        boolean enabled = switchEnabled.isChecked();
        LocalTime time = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
        NotificationSettings settings = new NotificationSettings(days, enabled, time);

        PeriodTracker app = (PeriodTracker) getApplication();
        service = app.getSchedulerService(this);


        List<Cycle> cycles = getCycles();

        if (cycles == null || cycles.isEmpty()) {
            Toast.makeText(this,
                    SchedulerConstants.NO_LOGS,
                    Toast.LENGTH_LONG).show();
            switchEnabled.setChecked(false); // Turn the switch back off
            return;
        }

        CycleHistory history = new CycleHistory(cycles);
        PeriodCalculator calculator = new PeriodCalculator(history);

        boolean success = service.saveAndSchedule(
                username,
                settings,
                calculator.calculateNextCycleStart(),
                calculator
        );

        if (success) {
            Toast.makeText(this, SchedulerConstants.SETTINGS_SAVED, Toast.LENGTH_SHORT).show();
        }
    }

    private List<Cycle> getCycles() {
        List<PeriodLog> logs = ((PeriodTracker) getApplication()).getPeriodLogService().getAllLogs();
        return CycleConversionService.fromPeriodLogs(logs);
    }
}