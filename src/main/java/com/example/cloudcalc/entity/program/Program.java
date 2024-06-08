package com.example.cloudcalc.entity.program;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Program {
    private int id;
    private String name;
    private LocalDate date;
    private List<CountCondition> conditions;
    private SpecialConditions specialConditions;

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

    public List<CountCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<CountCondition> conditions) {
        this.conditions = conditions;
    }

    public SpecialConditions getSpecialConditions() {
        return specialConditions;
    }

    public void setSpecialConditions(SpecialConditions specialConditions) {
        this.specialConditions = specialConditions;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Program program = (Program) obj;
        return id == program.id &&
                Objects.equals(name, program.name) &&
                Objects.equals(date, program.date) &&
                Objects.equals(conditions, program.conditions) &&
                Objects.equals(specialConditions, program.specialConditions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, date, conditions, specialConditions);
    }

    @Override
    public String toString() {
        return "Program{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", conditions=" + conditions +
                ", specialConditions=" + specialConditions +
                '}';
    }
}