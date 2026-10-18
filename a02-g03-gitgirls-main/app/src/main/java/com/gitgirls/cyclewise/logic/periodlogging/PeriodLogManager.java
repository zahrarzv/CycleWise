package com.gitgirls.cyclewise.logic.periodlogging;

import com.gitgirls.cyclewise.exceptions.InvalidPeriodLogException;
import com.gitgirls.cyclewise.exceptions.PeriodLogException;
import com.gitgirls.cyclewise.models.PeriodLog;
import com.gitgirls.cyclewise.persistence.PeriodLogPersistence;

import java.util.List;
import java.util.Optional;


public class PeriodLogManager {

    private final PeriodLogPersistence persistence;
    private final String username;

    public PeriodLogManager(PeriodLogPersistence persistence) {
        this(persistence, null);
    }

    public PeriodLogManager(PeriodLogPersistence persistence, String username) {
        if (persistence == null) {
            throw new IllegalArgumentException(PeriodLogMessages.PERSISTENCE_REQUIRED);
        }
        this.persistence = persistence;
        this.username = username;
    }

    public void add(PeriodLog newLog) throws InvalidPeriodLogException, PeriodLogException {
        PeriodLogValidator.validateForCreate(newLog);
        newLog.sanitizeNotes();

        boolean added = persistence.add(newLog, username);
        if (!added) {
            throw new PeriodLogException(PeriodLogMessages.ADD_FAILED);
        }
    }


    public PeriodLog getLogById(int id) throws InvalidPeriodLogException, PeriodLogException {
        PeriodLogValidator.validateID(id);

        Optional<PeriodLog> log = persistence.getByID(id, username);

        if (log.isEmpty()) {
            throw new PeriodLogException(PeriodLogMessages.LOG_NOT_FOUND);
        }

        return log.get();
    }

    public List<PeriodLog> getAll() {
        return persistence.getAllPeriodLogs(username);
    }

    public void updateLog(PeriodLog updated) throws InvalidPeriodLogException, PeriodLogException {
        PeriodLogValidator.validateForUpdate(updated);
        updated.sanitizeNotes();

        boolean updatedSuccess = persistence.update(updated, username);
        if (!updatedSuccess) {
            throw new PeriodLogException(PeriodLogMessages.UPDATE_FAILED_NOT_FOUND);
        }
    }

    public void deleteLog(int id) throws InvalidPeriodLogException, PeriodLogException {
        PeriodLogValidator.validateID(id);

        boolean deleted = persistence.delete(id, username);
        if (!deleted) {
            throw new PeriodLogException(PeriodLogMessages.DELETE_FAILED_NOT_FOUND);
        }
    }
}


