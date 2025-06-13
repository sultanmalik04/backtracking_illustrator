package com.backtracking.visualizer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class TraceStep {
    private int step;
    private String function;
    private Map<String, Object> variables; // e.g., arrays, hashmaps, etc.
    private int line; // optional, if you can get it
    private String action; // e.g., "swap", "add", "remove", "backtrack"
    private String details; // e.g., "Swapped nums[0] and nums[1]"
} 