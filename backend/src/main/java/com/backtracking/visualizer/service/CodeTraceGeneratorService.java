package com.backtracking.visualizer.service;

import com.backtracking.visualizer.dto.TraceStep;
import com.backtracking.visualizer.model.BacktrackingNode;
import java.util.List;

public interface CodeTraceGeneratorService {
    List<TraceStep> generateTrace(String code) throws Exception;
    List<BacktrackingNode> analyzeCodeForStaticGraph(String code) throws Exception;
} 