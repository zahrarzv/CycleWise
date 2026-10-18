package com.gitgirls.cyclewise.models;

public class Symptoms {

    private final boolean cramps;
    private final boolean headache;
    private final boolean fatigue;
    private final boolean bloating;
    private final boolean lowerBackPain;
    private final boolean nausea;
    private final boolean dizziness;
    private final boolean restlessness;
    private final boolean foodCravings;
    private final boolean brainFog;
    private final boolean anxiety;
    private final boolean irritability;

    public Symptoms(
            boolean cramps,
            boolean headache,
            boolean fatigue,
            boolean bloating,
            boolean lowerBackPain,
            boolean nausea,
            boolean dizziness,
            boolean restlessness,
            boolean foodCravings,
            boolean brainFog,
            boolean anxiety,
            boolean irritability
    ) {
        this.cramps = cramps;
        this.headache = headache;
        this.fatigue = fatigue;
        this.bloating = bloating;
        this.lowerBackPain = lowerBackPain;
        this.nausea = nausea;
        this.dizziness = dizziness;
        this.restlessness = restlessness;
        this.foodCravings = foodCravings;
        this.brainFog = brainFog;
        this.anxiety = anxiety;
        this.irritability = irritability;
    }


    // Create a copy constructor that copies the symptoms
    // and is used in periodLog for the getSymptoms() method
    public Symptoms(Symptoms other) {
        this(
                other.hasCramps(),
                other.hasHeadache(),
                other.hasFatigue(),
                other.hasBloating(),
                other.hasLowerBackPain(),
                other.hasNausea(),
                other.hasDizziness(),
                other.hasRestlessness(),
                other.hasFoodCravings(),
                other.hasBrainFog(),
                other.hasAnxiety(),
                other.hasIrritability()
        );
    }

    public boolean hasCramps() { return cramps; }
    public boolean hasHeadache() { return headache; }
    public boolean hasFatigue() { return fatigue; }
    public boolean hasBloating() { return bloating; }
    public boolean hasLowerBackPain() { return lowerBackPain; }
    public boolean hasNausea() { return nausea; }
    public boolean hasDizziness() { return dizziness; }
    public boolean hasRestlessness() { return restlessness; }
    public boolean hasFoodCravings() { return foodCravings; }
    public boolean hasBrainFog() { return brainFog; }
    public boolean hasAnxiety() { return anxiety; }
    public boolean hasIrritability() { return irritability; }
}