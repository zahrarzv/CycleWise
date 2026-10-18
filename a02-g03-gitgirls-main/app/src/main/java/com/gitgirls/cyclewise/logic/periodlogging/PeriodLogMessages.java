package com.gitgirls.cyclewise.logic.periodlogging;

public final class PeriodLogMessages {

    public static final String PERSISTENCE_REQUIRED = "PeriodLogPersistence cannot be null.";
    public static final String SERVICE_NOT_READY = "No user is set. Period log service is not ready.";

    public static final String ADD_FAILED = "Failed to add period log.";
    public static final String LOG_NOT_FOUND = "Period log not found.";
    public static final String UPDATE_FAILED_NOT_FOUND = "Failed to update period log. Log not found.";
    public static final String DELETE_FAILED_NOT_FOUND = "Failed to delete period log. Log not found.";

    public static final String LOG_REQUIRED = "Period log cannot be null.";
    public static final String START_DATE_REQUIRED = "Start date is required.";
    public static final String START_DATE_FUTURE = "Start date cannot be in the future.";
    public static final String END_DATE_BEFORE_START = "End date cannot be before start date.";
    public static final String END_DATE_FUTURE = "End date cannot be in the future.";
    public static final String FLOW_REQUIRED = "Flow is required.";
    public static final String SYMPTOMS_REQUIRED = "Symptoms are required.";
    public static final String NOTES_TOO_LONG = "Notes cannot exceed 500 characters.";
    public static final String INVALID_ID = "ID must be >= 0.";

    private PeriodLogMessages() {}
}