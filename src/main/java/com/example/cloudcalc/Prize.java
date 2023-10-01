package com.example.cloudcalc;

import java.util.Objects;

public class Prize {
    private String name;
    private String type;
    private int count;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prize prize = (Prize) o;
        return count == prize.count &&
                Objects.equals(name, prize.name) &&
                Objects.equals(type, prize.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, count);
    }
}
