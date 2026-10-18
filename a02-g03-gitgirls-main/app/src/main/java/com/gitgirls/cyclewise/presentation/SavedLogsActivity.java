package com.gitgirls.cyclewise.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitgirls.cyclewise.presentation.notification.NotificationSettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gitgirls.cyclewise.R;
import com.gitgirls.cyclewise.application.PeriodTracker;
import com.gitgirls.cyclewise.exceptions.InvalidPeriodLogException;
import com.gitgirls.cyclewise.exceptions.PeriodLogException;
import com.gitgirls.cyclewise.logic.periodlogging.PeriodLogService;
import com.gitgirls.cyclewise.models.PeriodLog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SavedLogsActivity extends AppCompatActivity {

    private enum SortOrder {
        NONE,
        EARLIEST_TO_LATEST,
        LATEST_TO_EARLIEST
    }

    private PeriodLogService periodLogService;
    private PeriodLogAdapter adapter;
    private TextView emptyText;
    private Button sortButton;

    private SortOrder currentSortOrder = SortOrder.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_logs);

        periodLogService = ((PeriodTracker) getApplication()).getPeriodLogService();

        emptyText = findViewById(R.id.emptyText);
        sortButton = findViewById(R.id.sortButton);

        RecyclerView savedLogsList = findViewById(R.id.savedLogsList);
        adapter = new PeriodLogAdapter(new ArrayList<>(), this::confirmDeleteLog);
        savedLogsList.setLayoutManager(new LinearLayoutManager(this));
        savedLogsList.setAdapter(adapter);

        sortButton.setOnClickListener(v -> showSortOptions(v));

        // --- Bottom Navigation ---
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_history); // highlight History tab since we're on this screen

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_history) {
                return true; // already here, do nothing
            } else if (id == R.id.nav_log) {
                startActivity(new Intent(this, PeriodLogActivity.class));
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

        refresh();
    }

    private void showSortOptions(View view) {
        List<PeriodLog> logs = periodLogService.getAllLogs();
        if (logs == null || logs.isEmpty()) return;

        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.sort_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.sort_earliest_to_latest) {
                currentSortOrder = SortOrder.EARLIEST_TO_LATEST;
                refresh();
                return true;
            } else if (itemId == R.id.sort_latest_to_earliest) {
                currentSortOrder = SortOrder.LATEST_TO_EARLIEST;
                refresh();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private List<PeriodLog> sortLogs(List<PeriodLog> logs) {
        if (logs == null) return new ArrayList<>();

        List<PeriodLog> sortedLogs = new ArrayList<>(logs);
        if (currentSortOrder == SortOrder.NONE) return sortedLogs;

        // prevents a crash if one bad/incomplete log accidentally gets into the list
        Comparator<PeriodLog> comparator = Comparator.comparing(
                log -> log.getStartDate() == null || log.getStartDate().getDate() == null
                        ? null
                        : log.getStartDate().getDate(),
                Comparator.nullsLast(Comparator.naturalOrder())
        );

        if (currentSortOrder == SortOrder.LATEST_TO_EARLIEST) {
            comparator = comparator.reversed();
        }

        sortedLogs.sort(comparator);
        return sortedLogs;
    }

    private void confirmDeleteLog(PeriodLog log) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_log_title))
                .setMessage(getString(R.string.delete_log_message))
                .setPositiveButton(getString(R.string.delete_button), (dialog, which) -> deleteLog(log))
                .setNegativeButton(getString(R.string.cancel_button), null)
                .show();
    }

    private void deleteLog(PeriodLog log) {
        try {
            periodLogService.deleteLog(log.getID());
            Toast.makeText(this, getString(R.string.log_deleted), Toast.LENGTH_SHORT).show();
            refresh();
        } catch (InvalidPeriodLogException | PeriodLogException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void refresh() {
        List<PeriodLog> logs = periodLogService.getAllLogs();
        List<PeriodLog> displayedLogs = sortLogs(logs);

        if (displayedLogs.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }

        adapter.setLogs(displayedLogs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
}