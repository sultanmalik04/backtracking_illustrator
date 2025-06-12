package com.backtracking.visualizer.controller;

import com.backtracking.visualizer.dto.VisualizationRequest;
import com.backtracking.visualizer.dto.VisualizationResponse;
import com.backtracking.visualizer.service.VisualizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/visualize")
@RequiredArgsConstructor
public class VisualizationController {

    private final VisualizationService visualizationService;

    @PostMapping
    public ResponseEntity<VisualizationResponse> visualize(@RequestBody VisualizationRequest request) {
        try {
            String visualizationUrl = visualizationService.generateVisualization(request.getCode());
            return ResponseEntity.ok(new VisualizationResponse(visualizationUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 