package com.gitgirls.cyclewise.models;

public class PeriodLog{
    public static final int UNASSIGNED_ID = 0;
    private int id;
    private CycleDate startD;
    private CycleDate endD;
    private Flow flow;
    private Symptoms symptoms;
    private String notes;

    public PeriodLog(int id, CycleDate startDate, CycleDate endDate, Flow flow, Symptoms symptoms, String notes) {
        this.id = id;
        this.startD = startDate;
        this.endD = endDate;
        this.flow = flow;
        this.symptoms = symptoms;
        this.notes = notes;
    }

    public int getID() {
        return id;
    }

    public CycleDate getStartDate() {
        return startD;
    }
    public CycleDate getEndDate() {
        return endD;
    }

    public Flow getFlow() {
        return flow;
    }

    public Symptoms getSymptoms() {
        if (symptoms == null) {
            return null;
        }
        return new Symptoms(symptoms);
    }
    public String getNotes() {
        return (notes == null) ? "" : notes;
    }

    // removes leading whitespace in notes
    public void sanitizeNotes() {
        if (notes == null) {
            this.notes = "";
        }
        else {
            this.notes = notes.trim();
        }
    }
    void setId(int id) {
        this.id = id;
    }
}