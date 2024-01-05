package com.example.cloudcalc.entity;

import com.example.cloudcalc.model.CountConditionModel;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Program {
    private int id;
    private String name;
    private LocalDate date;
    private List<CountConditionModel> conditions;

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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<CountConditionModel> getConditions() {
        return conditions;
    }

    public void setConditions(List<CountConditionModel> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Program program = (Program) obj;
        return Objects.equals(id, program.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Program{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", conditions=" + conditions +
                '}';
    }
}