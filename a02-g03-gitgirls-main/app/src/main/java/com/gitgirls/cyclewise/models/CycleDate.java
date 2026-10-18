package com.gitgirls.cyclewise.models;

import java.time.LocalDate;

public class CycleDate {
    private final LocalDate date;
    public CycleDate(LocalDate date) {
        if( date == null) {
            throw new IllegalArgumentException(ModelMessages.ERROR_NULL_DATE);
        }

        this.date = date;
    }

    public LocalDate getDate(){
        return date;
    }

    // Added minusDays and plusDays
    public CycleDate plusDays (int days) {
        return new CycleDate(date.plusDays(days));
    }

    public CycleDate minusDays (int days) {
        return new CycleDate(date.minusDays(days));
    }

    public boolean isBefore(CycleDate secondDate){
        return date.isBefore(secondDate.getDate());
    }

    // Added a toString()
    public String toString() {
        return date.toString();
    }
}
