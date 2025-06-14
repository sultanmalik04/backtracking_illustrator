package com.backtracking.visualizer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraceStep {
    private int step;
    private String function;
    private Map<String, Object> variables;
    private List<StackFrame> callStack;
    private int line;
    private String action;
    private String details;

    public TraceStep() {
        this.variables = new HashMap<>();
        this.callStack = new ArrayList<>();
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public List<StackFrame> getCallStack() {
        return callStack;
    }

    public void setCallStack(List<StackFrame> callStack) {
        this.callStack = callStack;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
} 