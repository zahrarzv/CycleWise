package com.gitgirls.cyclewise.models;

import java.time.LocalTime;

public class NotificationSettings {
    private int daysBeforeReminder;

    private boolean isEnabled;

    private LocalTime reminderTime;

     public NotificationSettings(int daysBeforeReminder, boolean isEnabled, LocalTime reminderTime){
        this.daysBeforeReminder = daysBeforeReminder;
        this.isEnabled = isEnabled;
        this.reminderTime = reminderTime;
    }
        //getters
    public int getDaysBeforeReminder() {
        return daysBeforeReminder;
    }
    public boolean isEnabled(){
        return isEnabled;
    }

    public LocalTime getReminderTime() {
        return reminderTime;
    }

        //setters
    public void setDaysBeforeReminder(int daysBeforeReminder) {
        this.daysBeforeReminder = daysBeforeReminder;
    }
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
    public void setReminderTime(LocalTime reminderTime) {
        this.reminderTime = reminderTime;
    }

}
