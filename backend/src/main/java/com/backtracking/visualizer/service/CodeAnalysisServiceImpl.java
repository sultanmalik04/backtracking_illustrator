package com.backtracking.visualizer.service;

import com.backtracking.visualizer.model.BacktrackingNode;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CodeAnalysisServiceImpl implements CodeAnalysisService {
    private static final int MAX_DEPTH = 10; // Maximum recursion depth
    private Set<String> processedNodes = new HashSet<>();

    @Override
    public List<BacktrackingNode> analyzeCode(String code) throws Exception {
        List<BacktrackingNode> nodes = new ArrayList<>();
        processedNodes.clear();
        CompilationUnit cu = StaticJavaParser.parse(code);
        
        // Find the main backtracking method
        Optional<MethodDeclaration> backtrackingMethod = cu.findAll(MethodDeclaration.class).stream()
                .filter(method -> isBacktrackingMethod(method))
                .findFirst();
        
        if (backtrackingMethod.isPresent()) {
            MethodDeclaration method = backtrackingMethod.get();
            String methodName = method.getNameAsString();
            
            // Create start node
            BacktrackingNode startNode = BacktrackingNode.createStartNode();
            startNode.getChildren().add(methodName + "_0");
            nodes.add(startNode);
            
            // Analyze method body
            analyzeMethodBody(method, methodName, 0, nodes);
            
            // Create end node
            BacktrackingNode endNode = BacktrackingNode.createEndNode();
            nodes.add(endNode);
        } else {
            throw new Exception("No backtracking method found in the code");
        }
        
        return nodes;
    }
    
    private boolean isBacktrackingMethod(MethodDeclaration method) {
        // Check for recursive call
        boolean hasRecursiveCall = method.findAll(MethodCallExpr.class).stream()
                .anyMatch(call -> call.getNameAsString().equals(method.getNameAsString()));

        // Check for any loop
        boolean hasLoop = !method.findAll(ForStmt.class).isEmpty()
                        || !method.findAll(WhileStmt.class).isEmpty()
                        || !method.findAll(ForEachStmt.class).isEmpty();

        // Consider as backtracking if it has both a loop and a recursive call
        return hasRecursiveCall && hasLoop;
    }
    
    private void analyzeMethodBody(MethodDeclaration method, String methodName, int depth, List<BacktrackingNode> nodes) {
        if (depth >= MAX_DEPTH) {
            return;
        }

        String nodeId = methodName + "_" + depth;
        if (processedNodes.contains(nodeId)) {
            return;
        }
        processedNodes.add(nodeId);
        
        // Create node for this recursion level
        BacktrackingNode node = BacktrackingNode.builder()
                .id(nodeId)
                .label("Recursion " + depth)
                .type("decision")
                .parameters(new ArrayList<>())
                .returnValues(new ArrayList<>())
                .children(new ArrayList<>())
                .build();
        
        // Add parameters
        method.getParameters().forEach(param -> 
            node.getParameters().add(param.getTypeAsString() + " " + param.getNameAsString())
        );
        
        // Analyze method body
        method.getBody().ifPresent(body -> {
            // Check for return statements
            body.findAll(ReturnStmt.class).forEach(returnStmt -> {
                if (returnStmt.getExpression().isPresent()) {
                    String returnValue = returnStmt.getExpression().get().toString();
                    node.getReturnValues().add(returnValue);
                }
            });
            
            // Check for recursive calls
            body.findAll(MethodCallExpr.class).forEach(call -> {
                if (call.getNameAsString().equals(methodName)) {
                    String childId = methodName + "_" + (depth + 1);
                    if (!processedNodes.contains(childId)) {
                        node.getChildren().add(childId);
                        analyzeMethodBody(method, methodName, depth + 1, nodes);
                    }
                }
            });
            
            // Check for backtracking conditions
            body.findAll(IfStmt.class).forEach(ifStmt -> {
                if (ifStmt.getThenStmt().toString().contains("return")) {
                    node.getReturnValues().add("backtrack");
                }
            });
        });
        
        nodes.add(node);
    }
} 