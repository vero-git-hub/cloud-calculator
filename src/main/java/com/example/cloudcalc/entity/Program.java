package com.example.cloudcalc.entity;

import com.example.cloudcalc.controller.ProgramController;
import com.example.cloudcalc.model.CountConditionModel;

import java.time.LocalDate;
import java.util.List;

public class Program {
    private String name;
    private LocalDate date;
    private List<CountConditionModel> conditions;

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
}