package com.example.cloudcalc.entity.dailystat;

import java.util.HashMap;
import java.util.Map;

/**
 * @author v_code
 **/
public class ScanResult {
    private Map<String, Map<String, Integer>> results = new HashMap<>();

    public void addScanResult(String profileName, String programName, int points) {
        Map<String, Integer> programResults = results.computeIfAbsent(profileName, k -> new HashMap<>());
        programResults.put(programName, points);
    }

    public Map<String, Map<String, Integer>> getAllResults() {
        return results;
    }
}