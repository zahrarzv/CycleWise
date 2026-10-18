/**
 * Provides period log operations for the currently logged-in user.
 */

package com.gitgirls.cyclewise.logic.periodlogging;

import com.gitgirls.cyclewise.exceptions.InvalidPeriodLogException;
import com.gitgirls.cyclewise.exceptions.PeriodLogException;
import com.gitgirls.cyclewise.models.PeriodLog;
import com.gitgirls.cyclewise.persistence.PeriodLogPersistence;

import java.util.List;

public class PeriodLogService {
    private final PeriodLogManager manager;


    public PeriodLogService(PeriodLogPersistence persistence, String username) {
        this.manager = new PeriodLogManager(persistence, username);
    }

    public void addLog(PeriodLog log) throws InvalidPeriodLogException, PeriodLogException {
        manager.add(log);
    }

    public PeriodLog getLogById(int id) throws InvalidPeriodLogException, PeriodLogException {
        return manager.getLogById(id);
    }

    public List<PeriodLog> getAllLogs() {
        return manager.getAll();
    }

    public void updateLog(PeriodLog log) throws InvalidPeriodLogException, PeriodLogException {
        manager.updateLog(log);
    }

    public void deleteLog(int id) throws InvalidPeriodLogException, PeriodLogException {
        manager.deleteLog(id);
    }
}