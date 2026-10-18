package com.gitgirls.cyclewise.persistence;

import com.gitgirls.cyclewise.models.PeriodLog;

import java.util.List;
import java.util.Optional;

public interface PeriodLogPersistence {
    List<PeriodLog> getAllPeriodLogs(String username);
    Optional<PeriodLog> getByID(int id, String username);
    boolean add(PeriodLog periodLog, String username);
    boolean update(PeriodLog periodLog, String username);
    boolean delete(int id, String username);
}