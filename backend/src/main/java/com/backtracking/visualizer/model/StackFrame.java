package com.backtracking.visualizer.model;

import java.util.HashMap;
import java.util.Map;

public class StackFrame {
    private String functionName;
    private Map<String, Object> parameters;
    private Map<String, Object> localVariables;
    private int lineNumber;

    public StackFrame() {
        this.parameters = new HashMap<>();
        this.localVariables = new HashMap<>();
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Map<String, Object> getLocalVariables() {
        return localVariables;
    }

    public void setLocalVariables(Map<String, Object> localVariables) {
        this.localVariables = localVariables;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
} 