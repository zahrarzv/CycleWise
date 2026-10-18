package com.gitgirls.cyclewise.models;

public final class ModelMessages {
    private ModelMessages() {}

    // In Cycle Class:
    public static final String ERROR_INVALID_CYCLE_DATES =
            "End date cannot be before start date.";
    public static final String ERROR_MISSING_CYCLE_DATES =
            "Start date and end date are required.";

    // In CycleDate Class:
    public static final String ERROR_NULL_DATE = "Date cannot be null.";

    // In User Class:
    public static final String ERROR_NULL_OR_EMPTY_USERNAME = "Username cannot be null or empty.";
    public static final String ERROR_NULL_OR_EMPTY_PASSWORD = "Password cannot be null or empty.";
}