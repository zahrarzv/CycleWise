package com.gitgirls.cyclewise.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.TextView;
import android.view.View;
import android.content.res.ColorStateList;

import androidx.appcompat.app.AppCompatActivity;

import com.gitgirls.cyclewise.R;
import com.gitgirls.cyclewise.application.PeriodTracker;
import com.gitgirls.cyclewise.logic.cycleprediction.CycleConversionService;
import com.gitgirls.cyclewise.exceptions.InvalidCycleDateException;
import com.gitgirls.cyclewise.exceptions.NotEnabledException;
import com.gitgirls.cyclewise.logic.cycleprediction.CycleHistory;
import com.gitgirls.cyclewise.logic.cycleprediction.PeriodCalculator;
import com.gitgirls.cyclewise.logic.notifications.NotificationScheduler;
import com.gitgirls.cyclewise.logic.notifications.NotificationValidator;
import com.gitgirls.cyclewise.logic.notifications.SchedulerConstants;
import com.gitgirls.cyclewise.logic.notifications.SchedulerService;
import com.gitgirls.cyclewise.logic.periodlogging.PeriodLogService;
import com.gitgirls.cyclewise.logic.phaseprediction.PhaseCalculator;
import com.gitgirls.cyclewise.models.Cycle;
import com.gitgirls.cyclewise.models.CycleDate;
import com.gitgirls.cyclewise.models.NotificationSettings;
import com.gitgirls.cyclewise.models.PeriodLog;
import com.gitgirls.cyclewise.models.Phases;

import com.gitgirls.cyclewise.persistence.NotificationSettingsPersistence;
import com.gitgirls.cyclewise.presentation.notification.NotificationMapper;
import com.gitgirls.cyclewise.presentation.notification.NotificationSettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import android.content.Intent;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PredictionActivity extends AppCompatActivity {

    private PeriodLogService periodLogService;

    private static final int ONE_DAY_AFTER = 1;
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM d, yyyy");

    private TextView todayDateText;
    private TextView phaseTitleText;
    private TextView cycleDayText;
    private TextView nextPeriodCardText;
    private TextView avgCycleCardText;
    private TextView tipsTitleText;
    private TextView tipOneText;
    private TextView tipTwoText;
    private TextView tipThreeText;

    private View phaseDot1;
    private View phaseDot2;
    private View phaseDot3;
    private View phaseDot4;
    private View phaseDot5;

    private View phaseLine1;
    private View phaseLine2;
    private View phaseLine3;
    private View phaseLine4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        periodLogService = ((PeriodTracker) getApplication()).getPeriodLogService();

        bindViews();
        showPredictionDashboard();
        setupBottomNavigation();
    }

    private void bindViews() {
        todayDateText = findViewById(R.id.todayDateText);
        phaseTitleText = findViewById(R.id.phaseTitleText);
        cycleDayText = findViewById(R.id.cycleDayText);
        nextPeriodCardText = findViewById(R.id.nextPeriodCardText);
        avgCycleCardText = findViewById(R.id.avgCycleCardText);
        tipsTitleText = findViewById(R.id.tipsTitleText);
        tipOneText = findViewById(R.id.tipOneText);
        tipTwoText = findViewById(R.id.tipTwoText);
        tipThreeText = findViewById(R.id.tipThreeText);

        phaseDot1 = findViewById(R.id.phaseDot1);
        phaseDot2 = findViewById(R.id.phaseDot2);
        phaseDot3 = findViewById(R.id.phaseDot3);
        phaseDot4 = findViewById(R.id.phaseDot4);
        phaseDot5 = findViewById(R.id.phaseDot5);

        phaseLine1 = findViewById(R.id.phaseLine1);
        phaseLine2 = findViewById(R.id.phaseLine2);
        phaseLine3 = findViewById(R.id.phaseLine3);
        phaseLine4 = findViewById(R.id.phaseLine4);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_predict);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_predict) {
                return true;
            } else if (id == R.id.nav_log) {
                startActivity(new Intent(this, PeriodLogActivity.class));
                return true;
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, SavedLogsActivity.class));
                return true;
            }else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, NotificationSettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    private void showPredictionDashboard() {
        try {
            List<PeriodLog> logs = periodLogService.getAllLogs();

            showTodayDate();

            PeriodLog ongoingLog = getLatestOngoingLog(logs);
            List<Cycle> cycles = CycleConversionService.fromPeriodLogs(logs);

            if (ongoingLog != null) {
                showOngoingCycleState(ongoingLog, cycles);
                return;
            }

            if (cycles.isEmpty()) {
                showNoDataState();
                return;
            }

            showPredictedCycleState(cycles);

        } catch (IllegalArgumentException | IllegalStateException e) {
            Log.e("PredictionActivity", getString(R.string.prediction_error_log), e);
            showNoDataState();
        }
    }

    private void showTodayDate() {
        todayDateText.setText(getString(
                R.string.prediction_today_date,
                LocalDate.now().format(DATE_FORMATTER)
        ));
    }

    private void showPredictedCycleState(List<Cycle> cycles) {
        CycleHistory cycleHistory = new CycleHistory(cycles);
        PeriodCalculator periodCalculator = new PeriodCalculator(cycleHistory);
        PhaseCalculator phaseCalculator = new PhaseCalculator(cycleHistory, periodCalculator);

        Phases phaseToday = phaseCalculator.calculatePhaseToday();
        CycleDate nextCycle = periodCalculator.calculateNextCycleStart();

        updatePredictionUi(phaseToday, cycleHistory, periodCalculator, nextCycle);
        scheduleNotification(nextCycle, periodCalculator);

    }

    private void updatePredictionUi(
            Phases phaseToday,
            CycleHistory cycleHistory,
            PeriodCalculator periodCalculator,
            CycleDate nextCycle
    ) {
        phaseTitleText.setText(formatPhaseName(phaseToday));
        updatePhaseProgress(phaseToday);

        cycleDayText.setText(getString(
                R.string.prediction_cycle_day,
                getCycleDay(cycleHistory, periodCalculator)
        ));

        nextPeriodCardText.setText(getString(
                R.string.prediction_next_cycle_card,
                nextCycle.getDate().toString()
        ));

        avgCycleCardText.setText(getString(
                R.string.prediction_avg_cycle_card,
                periodCalculator.calculateAvgCycleLength()
        ));

        applyTipsForPhase(phaseToday);
    }

    private void showNoDataState() {
        showTodayDate();
        showNoPredictionInfo();
        showDefaultTips();
        resetPhaseProgress();
    }

    private void showNoPredictionInfo() {
        phaseTitleText.setText(getString(R.string.prediction_no_phase));
        cycleDayText.setText(getString(R.string.prediction_no_cycle_day));
        nextPeriodCardText.setText(getString(R.string.prediction_no_data));
        avgCycleCardText.setText(getString(R.string.prediction_avg_cycle_unavailable));
    }

    private void showDefaultTips() {
        tipsTitleText.setText(getString(R.string.prediction_tips_title));
        tipOneText.setText(getString(R.string.prediction_tip_default_1));
        tipTwoText.setText(getString(R.string.prediction_tip_default_2));
        tipThreeText.setText(getString(R.string.prediction_tip_default_3));
    }

    private String formatPhaseName(Phases phase) {
        switch (phase) {
            case MENSTRUATION:
                return getString(R.string.phase_menstruation);
            case FOLLICULAR:
                return getString(R.string.phase_follicular);
            case OVULATION:
                return getString(R.string.phase_ovulation);
            case LUTEAL:
                return getString(R.string.phase_luteal);
            case PREMENSTRUAL:
                return getString(R.string.phase_premenstrual);
            default:
                return getString(R.string.prediction_no_phase);
        }
    }

    private int getCycleDay(CycleHistory cycleHistory, PeriodCalculator periodCalculator) {
        LocalDate today = LocalDate.now();
        LocalDate lastCycleStart = cycleHistory.getLastCycle().getStartDate().getDate();
        LocalDate predictedNextStart = periodCalculator.calculateNextCycleStart().getDate();

        LocalDate referenceStart = today.isBefore(predictedNextStart)
                ? lastCycleStart
                : predictedNextStart;

        return (int) ChronoUnit.DAYS.between(referenceStart, today) + ONE_DAY_AFTER;
    }

    private void setTips(int tipOneResId, int tipTwoResId, int tipThreeResId) {
        tipsTitleText.setText(getString(R.string.prediction_tips_title));
        tipOneText.setText(getString(tipOneResId));
        tipTwoText.setText(getString(tipTwoResId));
        tipThreeText.setText(getString(tipThreeResId));
    }
    private void applyTipsForPhase(Phases phase) {
        switch (phase) {
            case MENSTRUATION:
                setTips(
                        R.string.tip_menstruation_1,
                        R.string.tip_menstruation_2,
                        R.string.tip_menstruation_3
                );
                break;
            case FOLLICULAR:
                setTips(
                        R.string.tip_follicular_1,
                        R.string.tip_follicular_2,
                        R.string.tip_follicular_3
                );
                break;
            case OVULATION:
                setTips(
                        R.string.tip_ovulation_1,
                        R.string.tip_ovulation_2,
                        R.string.tip_ovulation_3
                );
                break;
            case LUTEAL:
                setTips(
                        R.string.tip_luteal_1,
                        R.string.tip_luteal_2,
                        R.string.tip_luteal_3
                );
                break;
            case PREMENSTRUAL:
                setTips(
                        R.string.tip_premenstrual_1,
                        R.string.tip_premenstrual_2,
                        R.string.tip_premenstrual_3
                );
                break;
        }
    }

    private PeriodLog getLatestOngoingLog(List<PeriodLog> logs) {
        if (logs == null || logs.isEmpty()) {
            return null;
        }

        PeriodLog latestOngoing = null;

        for (PeriodLog log : logs) {
            if (log.getEndDate() == null &&
                    (latestOngoing == null ||
                            log.getStartDate().getDate().isAfter(latestOngoing.getStartDate().getDate()))) {
                latestOngoing = log;
            }
        }

        return latestOngoing;
    }

    private void showOngoingCycleState(PeriodLog ongoingLog, List<Cycle> cycles) {
        updateOngoingCycleUi(ongoingLog);
        updateOngoingAvgCycle(cycles);
        applyTipsForPhase(Phases.MENSTRUATION);
    }

    private void updateOngoingCycleUi(PeriodLog ongoingLog) {
        phaseTitleText.setText(getString(R.string.phase_menstruation));
        updatePhaseProgress(Phases.MENSTRUATION);

        int cycleDay = (int) ChronoUnit.DAYS.between(
                ongoingLog.getStartDate().getDate(),
                LocalDate.now()
        ) + ONE_DAY_AFTER;

        cycleDayText.setText(getString(R.string.prediction_cycle_day, cycleDay));
        nextPeriodCardText.setText(getString(R.string.prediction_current_cycle));
    }

    private void updateOngoingAvgCycle(List<Cycle> cycles) {
        if (cycles.isEmpty()) {
            avgCycleCardText.setText(getString(R.string.prediction_avg_cycle_unavailable));
            return;
        }

        CycleHistory cycleHistory = new CycleHistory(cycles);
        PeriodCalculator periodCalculator = new PeriodCalculator(cycleHistory);

        avgCycleCardText.setText(getString(
                R.string.prediction_avg_cycle_card,
                periodCalculator.calculateAvgCycleLength()
        ));
    }

    // Helper methods for phase progress bar:
    private void tintView(View view, int color) {
        view.setBackgroundTintList(ColorStateList.valueOf(color));
    }
    private void resetPhaseProgress() {
        int inactiveColor = android.graphics.Color.parseColor("#F9D5E2");

        tintView(phaseDot1, inactiveColor);
        tintView(phaseDot2, inactiveColor);
        tintView(phaseDot3, inactiveColor);
        tintView(phaseDot4, inactiveColor);
        tintView(phaseDot5, inactiveColor);

        tintView(phaseLine1, inactiveColor);
        tintView(phaseLine2, inactiveColor);
        tintView(phaseLine3, inactiveColor);
        tintView(phaseLine4, inactiveColor);
    }

    private void updatePhaseProgress(Phases phase) {
        int activeColor = android.graphics.Color.parseColor("#D4537E");

        resetPhaseProgress();

        switch (phase) {
            case MENSTRUATION:
                tintView(phaseDot1, activeColor);
                break;

            case FOLLICULAR:
                tintView(phaseDot1, activeColor);
                tintView(phaseLine1, activeColor);
                tintView(phaseDot2, activeColor);
                break;

            case OVULATION:
                tintView(phaseDot1, activeColor);
                tintView(phaseLine1, activeColor);
                tintView(phaseDot2, activeColor);
                tintView(phaseLine2, activeColor);
                tintView(phaseDot3, activeColor);
                break;

            case LUTEAL:
                tintView(phaseDot1, activeColor);
                tintView(phaseLine1, activeColor);
                tintView(phaseDot2, activeColor);
                tintView(phaseLine2, activeColor);
                tintView(phaseDot3, activeColor);
                tintView(phaseLine3, activeColor);
                tintView(phaseDot4, activeColor);
                break;

            case PREMENSTRUAL:
                tintView(phaseDot1, activeColor);
                tintView(phaseLine1, activeColor);
                tintView(phaseDot2, activeColor);
                tintView(phaseLine2, activeColor);
                tintView(phaseDot3, activeColor);
                tintView(phaseLine3, activeColor);
                tintView(phaseDot4, activeColor);
                tintView(phaseLine4, activeColor);
                tintView(phaseDot5, activeColor);
                break;
        }
    }
    private void scheduleNotification(CycleDate nextPeriodDate,
                                      PeriodCalculator periodCalculator) {
        try {
            PeriodTracker app = (PeriodTracker) getApplication();
            String username = app.getLoggedInUser().getUsername();

            SchedulerService useCase = app.getSchedulerService(this);

            useCase.execute(username, nextPeriodDate, periodCalculator);

        } catch (NotEnabledException e) {
            Toast.makeText(this, SchedulerConstants.NOTI_NOT_ENABLED, Toast.LENGTH_SHORT).show();
        } catch (InvalidCycleDateException | IllegalArgumentException e) {
            Toast.makeText(this, SchedulerConstants.INVALID_DATES, Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            Toast.makeText(this, SchedulerConstants.BAD_STATE, Toast.LENGTH_SHORT).show();
        }
    }
}