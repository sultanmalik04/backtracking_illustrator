package com.backtracking.visualizer.controller;

import com.backtracking.visualizer.dto.VisualizationRequest;
import com.backtracking.visualizer.dto.VisualizationResponse;
import com.backtracking.visualizer.service.VisualizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/visualize")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class VisualizationController {

    private final VisualizationService visualizationService;

    @PostMapping
    public ResponseEntity<?> visualize(@RequestBody VisualizationRequest request) {
        try {
            log.info("Received visualization request");
            if (request.getCode() == null || request.getCode().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("No code provided"));
            }

            String visualizationUrl = visualizationService.generateVisualization(request.getCode());
            log.info("Successfully generated visualization" + visualizationUrl);
            return ResponseEntity.ok(new VisualizationResponse(visualizationUrl));
        } catch (Exception e) {
            log.error("Error generating visualization", e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Failed to generate visualization: " + e.getMessage()));
        }
    }
}

record ErrorResponse(String message) {
}