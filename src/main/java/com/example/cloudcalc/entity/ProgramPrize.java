package com.example.cloudcalc.entity;

import java.util.List;

public class ProgramPrize {
    private String program;
    private List<PrizeInfo> prizeInfoList;

    public ProgramPrize() {
    }

    public ProgramPrize(String program, List<PrizeInfo> prizeInfoList) {
        this.program = program;
        this.prizeInfoList = prizeInfoList;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public List<PrizeInfo> getPrizeInfoList() {
        return prizeInfoList;
    }

    public void setPrizeInfoList(List<PrizeInfo> prizeInfoList) {
        this.prizeInfoList = prizeInfoList;
    }

    @Override
    public String toString() {
        return "ProgramPrize{" +
                "program='" + program + '\'' +
                ", prizeInfoList=" + prizeInfoList +
                '}';
    }
}