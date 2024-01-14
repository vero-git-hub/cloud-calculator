package com.example.cloudcalc.entity.prize;

public class PrizeInfo {
    private String prize;
    private int earnedPoints;

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public int getEarnedPoints() {
        return earnedPoints;
    }

    public void setEarnedPoints(int earnedPoints) {
        this.earnedPoints = earnedPoints;
    }

    @Override
    public String toString() {
        return "PrizeInfo{" +
                "prize='" + prize + '\'' +
                ", earnedPoints=" + earnedPoints +
                '}';
    }
}