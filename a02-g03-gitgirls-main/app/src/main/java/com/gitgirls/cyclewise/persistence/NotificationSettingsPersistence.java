package com.gitgirls.cyclewise.persistence;
import com.gitgirls.cyclewise.models.NotificationSettings;

public interface NotificationSettingsPersistence {
    NotificationSettings getSettings(String username);
    boolean save(NotificationSettings settings, String username);
}
