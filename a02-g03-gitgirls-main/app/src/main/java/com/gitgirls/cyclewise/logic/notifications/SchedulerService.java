package com.gitgirls.cyclewise.logic.notifications;

import android.util.Log;

import com.gitgirls.cyclewise.exceptions.InvalidCycleDateException;
import com.gitgirls.cyclewise.exceptions.NotEnabledException;
import com.gitgirls.cyclewise.logic.cycleprediction.PeriodCalculator;
import com.gitgirls.cyclewise.models.CycleDate;
import com.gitgirls.cyclewise.models.NotificationSettings;
import com.gitgirls.cyclewise.persistence.NotificationSettingsPersistence;
import com.gitgirls.cyclewise.presentation.notification.NotificationMapper;
import java.time.LocalDate;

public class SchedulerService {
    private final NotificationSettingsPersistence persistence;
    private final NotificationMapper mapper;
    private final NotificationValidator validator;

    public SchedulerService(NotificationSettingsPersistence persistence, NotificationMapper mapper) {
        this.persistence = persistence;
        this.mapper = mapper;
        this.validator = new NotificationValidator();
    }

    public void execute(String username, CycleDate nextCycle, PeriodCalculator calculator)
            throws NotEnabledException, InvalidCycleDateException {

        validator.validateDate(nextCycle);

        NotificationSettings settings = persistence.getSettings(username);

        validator.checkEnable(settings);

        NotificationScheduler scheduler = new NotificationScheduler(calculator, settings, validator);
        LocalDate notificationDate = scheduler.getNotificationDate();

        mapper.scheduleNotification(
                notificationDate,
                settings.getReminderTime(),
                nextCycle.getDate(),
                settings.getDaysBeforeReminder()
        );
    }

    public boolean saveAndSchedule(String username, NotificationSettings settings,
                                   CycleDate nextCycle, PeriodCalculator calculator) {
        boolean saved = persistence.save(settings, username);

        if (saved) {
            if (!settings.isEnabled()) {
                mapper.cancelNotification();
            } else {
                try {
                    execute(username, nextCycle, calculator);
                } catch (InvalidCycleDateException | IllegalArgumentException | NotEnabledException e) {
                    throw new IllegalStateException(SchedulerConstants.NO_NOTIF, e);
                } catch (IllegalStateException e) {
                    throw new IllegalStateException(SchedulerConstants.BAD_STATE, e);
                }
            }
        }
        return saved;
    }
    public NotificationSettings getSettings(String username) {
        return persistence.getSettings(username);
    }
}