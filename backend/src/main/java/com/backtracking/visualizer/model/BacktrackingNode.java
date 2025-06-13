package com.backtracking.visualizer.model;

import lombok.Data;
import lombok.Builder;
import java.util.List;
import java.util.ArrayList;

@Data
@Builder
public class BacktrackingNode {
    private String id;
    private String label;
    private String type; // "start", "decision", "backtrack", "end"
    private List<String> parameters;
    private List<String> returnValues;
    private List<String> children;
    
    public static BacktrackingNode createStartNode() {
        return BacktrackingNode.builder()
                .id("start")
                .label("Start")
                .type("start")
                .parameters(new ArrayList<>())
                .returnValues(new ArrayList<>())
                .children(new ArrayList<>())
                .build();
    }
    
    public static BacktrackingNode createEndNode() {
        return BacktrackingNode.builder()
                .id("end")
                .label("End")
                .type("end")
                .parameters(new ArrayList<>())
                .returnValues(new ArrayList<>())
                .children(new ArrayList<>())
                .build();
    }
} 