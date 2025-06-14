package com.backtracking.visualizer.service;

import com.backtracking.visualizer.model.BacktrackingNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisualizationServiceImpl implements VisualizationService {

    private final CodeTraceGeneratorService codeTraceGeneratorService;
    private final GraphVizService graphVizService;

    @Override
    public String generateVisualization(String code) throws Exception {
        // Analyze the code for static graph generation using the new service
        List<BacktrackingNode> nodes = codeTraceGeneratorService.analyzeCodeForStaticGraph(code);
        
        // Generate visualization using GraphViz
        return graphVizService.generateVisualization(nodes);
    }
} 