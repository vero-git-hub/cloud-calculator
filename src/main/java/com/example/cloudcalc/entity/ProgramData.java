package com.example.cloudcalc.entity;

import java.util.List;

public class ProgramData {
    private String programName;
    private int earnedPoints;
    private List<PrizeInfo> prizeInfoList;

    public ProgramData() {
    }

    public ProgramData(String programName, int earnedPoints, List<PrizeInfo> prizeInfoList) {
        this.programName = programName;
        this.earnedPoints = earnedPoints;
        this.prizeInfoList = prizeInfoList;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public int getEarnedPoints() {
        return earnedPoints;
    }

    public void setEarnedPoints(int earnedPoints) {
        this.earnedPoints = earnedPoints;
    }

    public List<PrizeInfo> getPrizeInfoList() {
        return prizeInfoList;
    }

    public void setPrizeInfoList(List<PrizeInfo> prizeInfoList) {
        this.prizeInfoList = prizeInfoList;
    }
}