package com.example.cloudcalc.entity;

import java.util.Objects;

public class Prize {
    private int id;
    private String name;
    private String program;
    private int points;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prize prize = (Prize) o;
        return points == prize.points &&
                Objects.equals(id, prize.id) &&
                Objects.equals(name, prize.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, points);
    }

    @Override
    public String toString() {
        return "Prize{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                ", program='" + program + '\'' +
                ", points=" + points +
                '}';
    }
}