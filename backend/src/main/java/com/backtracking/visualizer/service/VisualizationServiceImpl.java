package com.backtracking.visualizer.service;

import com.backtracking.visualizer.model.BacktrackingNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisualizationServiceImpl implements VisualizationService {

    private final CodeAnalysisService codeAnalysisService;
    private final GraphVizService graphVizService;

    @Override
    public String generateVisualization(String code) throws Exception {
        // Analyze the code
        List<BacktrackingNode> nodes = codeAnalysisService.analyzeCode(code);
        
        // Generate visualization using GraphViz
        return graphVizService.generateVisualization(nodes);
    }
} 