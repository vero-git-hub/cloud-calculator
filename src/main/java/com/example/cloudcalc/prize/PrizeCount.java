package com.example.cloudcalc.prize;

public class PrizeCount {
    private final String name;
    private final String program;
    private final long count;

    public PrizeCount(String name, String program, long count) {
        this.name = name;
        this.program = program;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public String getProgram() {
        return program;
    }

    public long getCount() {
        return count;
    }
}
