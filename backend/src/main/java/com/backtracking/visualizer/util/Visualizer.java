package com.backtracking.visualizer.util;

import java.util.HashMap;
import java.util.Map;

// This class is intended to be copied by the user into their project
// for tracing purposes. It does not perform actual visualization
// but provides hooks for the analyzer to capture state.
public class Visualizer {

    // This method is a placeholder. Its purpose is to be called by the user's
    // backtracking code. The CodeTraceGeneratorService will parse the arguments
    // of this method call to build the trace steps.
    public static void captureStep(String action, String details, Object... variables) {
        Map<String, Object> vars = new HashMap<>();
        for (int i = 0; i < variables.length; i += 2) {
            if (i + 1 < variables.length) {
                vars.put(variables[i].toString(), variables[i + 1]);
            }
        }
        CodeExecutor.captureStep(action, details, vars);
    }

    // Overloaded method for convenience, when no specific variables are needed
    public static void captureStep(String action, String details) {
        captureStep(action, details, new Object[0]);
    }

    // Overloaded method for convenience, when only action and a single variable state are provided
    public static void captureStep(String action, String details, String key, Object value) {
        Map<String, Object> vars = new HashMap<>();
        vars.put(key, value);
        CodeExecutor.captureStep(action, details, vars);
    }
} 