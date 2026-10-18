package com.gitgirls.cyclewise.persistence.stub;

import com.gitgirls.cyclewise.persistence.PeriodLogPersistence;
import com.gitgirls.cyclewise.models.PeriodLog;
import com.gitgirls.cyclewise.models.Symptoms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PeriodLogPersistenceStub implements PeriodLogPersistence {

    private List<PeriodLog> periodLogs = new ArrayList<>();
    private int nextID = 1;

    private static final String ADD = "add";
    private static final String UPDATE = "update";

    @Override
    public List<PeriodLog> getAllPeriodLogs(String username) {
        return Collections.unmodifiableList(periodLogs);
    }

    @Override
    public Optional<PeriodLog> getByID(int id, String username) {
        for (PeriodLog periodLog : periodLogs) {
            if (periodLog.getID() == id) {
                return Optional.of(periodLog);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean add(PeriodLog periodLog, String username) {
        checkNonNullLog(periodLog, ADD);

        int id = periodLog.getID();

        if (id == PeriodLog.UNASSIGNED_ID) {
            id = nextID++;
        } else if (existsById(id)) {
            return false; // duplicate id, add failed
        }

        // Ensure nextID is always greater than the current ID
        if (id >= nextID) {
            nextID = id + 1;
        }

        PeriodLog newPeriodLog = new PeriodLog(
                id,
                periodLog.getStartDate(),
                periodLog.getEndDate(),
                periodLog.getFlow(),
                new Symptoms(periodLog.getSymptoms()),
                periodLog.getNotes()
        );
        periodLogs.add(newPeriodLog);
        return true;
    }

    @Override
    public boolean update(PeriodLog periodLog, String username) {
        checkNonNullLog(periodLog, UPDATE);

        for (int i = 0; i < periodLogs.size(); i++) {
            if (periodLogs.get(i).getID() == periodLog.getID()) {
                PeriodLog updatedPeriodLog = new PeriodLog(
                        periodLog.getID(),
                        periodLog.getStartDate(),
                        periodLog.getEndDate(),
                        periodLog.getFlow(),
                        new Symptoms(periodLog.getSymptoms()),
                        periodLog.getNotes()
                );
                periodLogs.set(i, updatedPeriodLog);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(int id, String username) {
        for (int i = 0; i < periodLogs.size(); i++) {
            if (periodLogs.get(i).getID() == id) {
                periodLogs.remove(i);
                return true;
            }
        }
        return false;
    }


    public List<PeriodLog> getAllPeriodLogs() {
        return getAllPeriodLogs(null);
    }

    public Optional<PeriodLog> getByID(int id) {
        return getByID(id, null);
    }

    // --------------------- Private helpers ---------------
    private static void checkNonNullLog(PeriodLog periodLog, String operation) {
        if (periodLog == null) {
            throw new IllegalArgumentException("Period log cannot be null for " + operation);
        }
    }

    private boolean existsById(int id) {
        for (PeriodLog periodLog : periodLogs) {
            if (periodLog.getID() == id) return true;
        }
        return false;
    }
}