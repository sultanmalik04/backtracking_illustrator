package com.backtracking.visualizer.service;

import com.backtracking.visualizer.model.BacktrackingNode;
import java.util.List;

public interface CodeAnalysisService {
    List<BacktrackingNode> analyzeCode(String code) throws Exception;
} 