package com.backtracking.visualizer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraceStep {
    private int step;
    private String function;
    private Map<String, Object> variables = new HashMap<>();
    private List<StackFrame> callStack = new ArrayList<>();
    private int line;
    private String action;
    private String details;
} 