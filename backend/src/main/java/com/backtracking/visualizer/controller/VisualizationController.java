package com.backtracking.visualizer.controller;

import com.backtracking.visualizer.dto.VisualizationRequest;
import com.backtracking.visualizer.dto.VisualizationResponse;
import com.backtracking.visualizer.service.VisualizationService;
import com.backtracking.visualizer.service.PermutationTraceService;
import com.backtracking.visualizer.dto.PermutationTraceRequest;
import com.backtracking.visualizer.dto.TraceStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class VisualizationController {

    private final VisualizationService visualizationService;
    private final PermutationTraceService permutationTraceService;

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

    @PostMapping("/trace/permutations")
    public ResponseEntity<List<TraceStep>> tracePermutations(@RequestBody PermutationTraceRequest request) {
        try {
            List<TraceStep> trace = permutationTraceService.generatePermutationTrace(request.getNums());
            log.info("Successfully generated permutation trace" + trace);
            return ResponseEntity.ok(trace);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

record ErrorResponse(String message) {
}