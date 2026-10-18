package com.gitgirls.cyclewise.logic.notifications;

import com.gitgirls.cyclewise.exceptions.InvalidCycleDateException;
import com.gitgirls.cyclewise.exceptions.NotEnabledException;
import com.gitgirls.cyclewise.models.CycleDate;
import com.gitgirls.cyclewise.models.NotificationSettings;

public class NotificationValidator {
    // simple public constructor
    public NotificationValidator() {}

    // Validate that notifications are enabled
    public void checkEnable(NotificationSettings settings) {
        if (settings == null) {
            throw new IllegalArgumentException(SchedulerConstants.NULL_NOTI_SETTING);
        }

        if (!settings.isEnabled()) {
            throw new NotEnabledException(SchedulerConstants.NOTI_NOT_ENABLED);
        }
    }

    // Validate that the cycle date is valid
    public void validateDate(CycleDate date) {
        if (date == null || date.getDate() == null) {
            throw new InvalidCycleDateException(SchedulerConstants.INVALID_DATES);
        }
    }
}