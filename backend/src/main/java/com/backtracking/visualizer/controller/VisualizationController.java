package com.backtracking.visualizer.controller;

import com.backtracking.visualizer.dto.VisualizationRequest;
import com.backtracking.visualizer.dto.VisualizationResponse;
import com.backtracking.visualizer.service.VisualizationService;
import com.backtracking.visualizer.service.PermutationTraceService;
import com.backtracking.visualizer.service.CodeTraceGeneratorService;
import com.backtracking.visualizer.dto.PermutationTraceRequest;
import com.backtracking.visualizer.dto.GenericTraceRequest;
import com.backtracking.visualizer.dto.TraceStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.backtracking.visualizer.util.CodeExecutor;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class VisualizationController {

    private final VisualizationService visualizationService;
    private final PermutationTraceService permutationTraceService;
    private final CodeTraceGeneratorService codeTraceGeneratorService;

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
    public ResponseEntity<?> tracePermutations(@RequestBody PermutationTraceRequest request) {
        try {
            List<TraceStep> trace = permutationTraceService.generatePermutationTrace(request.getNums());
            log.info("Successfully generated permutation trace" + trace);
            return ResponseEntity.ok(trace);
        } catch (Exception e) {
            log.error("Error generating permutation trace", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to generate permutation trace: " + e.getMessage()));
        }
    }

    @PostMapping("/trace/generic")
    public ResponseEntity<?> generateGenericTrace(@RequestBody Map<String, String> request) {
        try {
            String code = request.get("code");
            if (code == null || code.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "No code provided for generic tracing"));
            }

            // Execute the code and capture the trace
            List<TraceStep> trace = CodeExecutor.executeCode(code);
            return ResponseEntity.ok(trace);
        } catch (Exception e) {
            log.error("Error executing code for generic trace", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Error executing code: " + e.getMessage()));
        }
    }
}

record ErrorResponse(String message) {
}