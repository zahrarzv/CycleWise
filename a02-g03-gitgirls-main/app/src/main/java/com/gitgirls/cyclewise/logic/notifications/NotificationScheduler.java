package com.gitgirls.cyclewise.logic.notifications;

import static com.gitgirls.cyclewise.logic.notifications.SchedulerConstants.NULL_NOTI_SETTING;
import static com.gitgirls.cyclewise.logic.notifications.SchedulerConstants.NULL_NOTI_VALIDATOR;
import static com.gitgirls.cyclewise.logic.notifications.SchedulerConstants.NULL_PERIOD_CALC;

import com.gitgirls.cyclewise.models.NotificationSettings;
import com.gitgirls.cyclewise.logic.cycleprediction.PeriodCalculator;
import com.gitgirls.cyclewise.models.CycleDate;


import java.time.LocalDate;

public class NotificationScheduler {

    private final PeriodCalculator periodCalculator;
    private final NotificationSettings settings;

    private final NotificationValidator validator;

    public NotificationScheduler(PeriodCalculator periodCalculator, NotificationSettings settings, NotificationValidator validator) {
        if (periodCalculator == null) throw new IllegalArgumentException(NULL_PERIOD_CALC);
        if (settings == null) throw new IllegalArgumentException(NULL_NOTI_SETTING);
        if (validator == null) throw new IllegalArgumentException(NULL_NOTI_VALIDATOR);
        this.periodCalculator = periodCalculator;
        this.settings = settings;
        this.validator = validator;
    }

    public LocalDate getNotificationDate() {
        validator.checkEnable(settings); // throws NotEnabledException if disabled
        CycleDate nextCycleStart = periodCalculator.calculateNextCycleStart();
        validator.validateDate(nextCycleStart);
        return nextCycleStart.getDate().minusDays(settings.getDaysBeforeReminder());
    }
}