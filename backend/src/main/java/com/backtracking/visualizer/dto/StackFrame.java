package com.backtracking.visualizer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StackFrame {
    private String functionName;
    private Map<String, Object> parameters = new HashMap<>();
    private Map<String, Object> localVariables = new HashMap<>();
    private int lineNumber;
} 