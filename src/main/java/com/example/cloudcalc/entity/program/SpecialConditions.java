package com.example.cloudcalc.entity.program;

import java.util.List;
import java.util.Objects;

public class SpecialConditions {
    private List<String> badgesToIgnore;
    private int badgesPerPoint;
    private int pointsPerBadge;

    public List<String> getBadgesToIgnore() {
        return badgesToIgnore;
    }

    public void setBadgesToIgnore(List<String> badgesToIgnore) {
        this.badgesToIgnore = badgesToIgnore;
    }

    public int getBadgesPerPoint() {
        return badgesPerPoint;
    }

    public void setBadgesPerPoint(int badgesPerPoint) {
        this.badgesPerPoint = badgesPerPoint;
    }

    public int getPointsPerBadge() {
        return pointsPerBadge;
    }

    public void setPointsPerBadge(int pointsPerBadge) {
        this.pointsPerBadge = pointsPerBadge;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SpecialConditions that = (SpecialConditions) obj;
        return badgesPerPoint == that.badgesPerPoint &&
                pointsPerBadge == that.pointsPerBadge &&
                Objects.equals(badgesToIgnore, that.badgesToIgnore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(badgesToIgnore, badgesPerPoint, pointsPerBadge);
    }

    @Override
    public String toString() {
        return "SpecialConditions{" +
                "badgesToIgnore=" + badgesToIgnore +
                ", badgesPerPoint=" + badgesPerPoint +
                ", pointsPerBadge=" + pointsPerBadge +
                '}';
    }
}
