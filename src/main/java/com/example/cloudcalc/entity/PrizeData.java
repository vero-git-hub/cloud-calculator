package com.example.cloudcalc.entity;

public class PrizeData {
    private String program;
    private int earnedPoints;
    private String prize;

    public PrizeData(String program, int earnedPoints, String prize) {
        this.program = program;
        this.earnedPoints = earnedPoints;
        this.prize = prize;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public int getEarnedPoints() {
        return earnedPoints;
    }

    public void setEarnedPoints(int earnedPoints) {
        this.earnedPoints = earnedPoints;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }
}