package com.example.cloudcalc.badge.type;

import java.util.Objects;

public class TypeBadge {

    private String name;
    private String startDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeBadge typeBadge = (TypeBadge) o;
        return Objects.equals(name, typeBadge.name) && Objects.equals(startDate, typeBadge.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, startDate);
    }
}
