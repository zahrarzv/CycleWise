package com.gitgirls.cyclewise.models;

public class Cycle {
    private final CycleDate startDate;
    private final CycleDate endDate;

    public Cycle(CycleDate startDate, CycleDate endDate){
        if(!validateCycleDates(startDate, endDate)){
            throw new IllegalArgumentException(ModelMessages.ERROR_INVALID_CYCLE_DATES);
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public CycleDate getStartDate() {
        return startDate;
    }

    public CycleDate getEndDate() {
        return endDate;
    }

    private boolean validateCycleDates(CycleDate startDate, CycleDate endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }

        return startDate.isBefore(endDate)
                || startDate.getDate().isEqual(endDate.getDate());
    }

    // Builder pattern
    public static CycleBuilder builder() {
        return new CycleBuilder();
    }

    public static class CycleBuilder {
        private CycleDate startDate;
        private CycleDate endDate;

        public CycleBuilder withStartDate(CycleDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public CycleBuilder withEndDate(CycleDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Cycle build() {
            if (startDate == null || endDate == null) {
                throw new IllegalStateException(ModelMessages.ERROR_MISSING_CYCLE_DATES);
            }
            return new Cycle(startDate, endDate);
        }
    }
}