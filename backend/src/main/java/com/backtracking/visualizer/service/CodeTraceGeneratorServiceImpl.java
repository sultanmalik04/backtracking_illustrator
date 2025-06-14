package com.backtracking.visualizer.service;

import com.backtracking.visualizer.dto.StackFrame;
import com.backtracking.visualizer.dto.TraceStep;
import com.backtracking.visualizer.model.BacktrackingNode;
import com.backtracking.visualizer.util.Visualizer;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CodeTraceGeneratorServiceImpl implements CodeTraceGeneratorService {

    private List<TraceStep> trace;
    private AtomicInteger stepCounter;
    private LinkedList<StackFrame> callStack;

    // --- For Generic Trace Generation (using Visualizer.captureStep) ---
    @Override
    public List<TraceStep> generateTrace(String code) throws Exception {
        trace = new ArrayList<>();
        stepCounter = new AtomicInteger(1);
        callStack = new LinkedList<>(); // Reset call stack for each new trace

        CompilationUnit cu = StaticJavaParser.parse(code);

        // Find the main method that *might* contain Visualizer calls
        // Or we can just visit the whole compilation unit
        new VisualizerCallVisitor().visit(cu, null);

        if (trace.isEmpty()) {
            throw new Exception("No Visualizer.captureStep calls found in the provided code. Please annotate your code with Visualizer.captureStep(action, details, variables) calls.");
        }

        return trace;
    }

    // Simplified backtracking method detection (as agreed previously)
    private boolean isBacktrackingMethod(MethodDeclaration method) {
        boolean hasRecursiveCall = method.findAll(MethodCallExpr.class).stream()
                .anyMatch(call -> call.getNameAsString().equals(method.getNameAsString()));
        boolean hasLoop = !method.findAll(com.github.javaparser.ast.stmt.ForStmt.class).isEmpty()
                || !method.findAll(com.github.javaparser.ast.stmt.WhileStmt.class).isEmpty()
                || !method.findAll(com.github.javaparser.ast.stmt.ForEachStmt.class).isEmpty();
        return hasRecursiveCall && hasLoop;
    }

    // This visitor will traverse the AST and find calls to Visualizer.captureStep
    private class VisualizerCallVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);

            if (n.getScope().isPresent() && n.getScope().get().isNameExpr() &&
                n.getScope().get().asNameExpr().getNameAsString().equals("Visualizer") &&
                n.getNameAsString().equals("captureStep")) {

                String action = "unknown";
                String details = "";
                Map<String, Object> variables = new HashMap<>();

                // Extract action and details (first two arguments)
                if (n.getArguments().size() >= 1 && n.getArgument(0).isStringLiteralExpr()) {
                    action = n.getArgument(0).asStringLiteralExpr().asString();
                }
                if (n.getArguments().size() >= 2 && n.getArgument(1).isStringLiteralExpr()) {
                    details = n.getArgument(1).asStringLiteralExpr().asString();
                }

                // Handle variable map (if provided as the third argument)
                if (n.getArguments().size() >= 3 && n.getArgument(2).isMethodCallExpr()) {
                    MethodCallExpr mapCreation = n.getArgument(2).asMethodCallExpr();
                    if (mapCreation.getScope().isPresent() && mapCreation.getScope().get().isNameExpr() &&
                        // Check if it's Map.of (or similar static map creation)
                        (mapCreation.getScope().get().asNameExpr().getNameAsString().equals("Map") ||
                         mapCreation.getScope().get().asNameExpr().getNameAsString().equals("Collections")) &&
                        (mapCreation.getNameAsString().equals("of") || mapCreation.getNameAsString().equals("singletonMap")) &&
                        mapCreation.getArguments().size() % 2 == 0) { // Should have key-value pairs

                        for (int i = 0; i < mapCreation.getArguments().size(); i += 2) {
                            Expression keyExpr = mapCreation.getArgument(i);
                            Expression valueExpr = mapCreation.getArgument(i + 1);
                            if (keyExpr.isStringLiteralExpr()) {
                                String varName = keyExpr.asStringLiteralExpr().asString();
                                Object varValue = extractValue(valueExpr);
                                variables.put(varName, varValue);
                            }
                        }
                    }
                }
                // Handle single variable overload (if provided as the third and fourth arguments)
                else if (n.getArguments().size() == 4) {
                    if (n.getArgument(2).isStringLiteralExpr()) {
                        String varName = n.getArgument(2).asStringLiteralExpr().asString();
                        Object varValue = extractValue(n.getArgument(3));
                        variables.put(varName, varValue);
                    }
                }

                // Infer current function context for the call stack
                MethodDeclaration currentMethod = n.findAncestor(MethodDeclaration.class).orElse(null);
                String functionName = currentMethod != null ? currentMethod.getNameAsString() : "global";

                // Simple conceptual call stack for static analysis:
                // We're not doing live execution, so we just add the current method as a frame.
                // More advanced would require tracking method entry/exit through AST traversal
                // to build a true stack, but that adds significant complexity.
                List<StackFrame> currentCallStack = new ArrayList<>();
                if (currentMethod != null) {
                    // For static analysis, we can't get runtime parameter values directly.
                    // We can list parameter names, but their *values* are runtime-dependent.
                    Map<String, Object> params = new HashMap<>();
                    currentMethod.getParameters().forEach(param -> params.put(param.getNameAsString(), "<unknown>"));
                    
                    StackFrame frame = new StackFrame();
                    frame.setFunctionName(functionName);
                    frame.setParameters(params);
                    frame.setLineNumber(n.getBegin().map(pos -> pos.line).orElse(-1));
                    currentCallStack.add(frame);
                }

                trace.add(new TraceStep(
                    stepCounter.getAndIncrement(),
                    functionName,
                    variables,
                    currentCallStack,
                    n.getBegin().map(pos -> pos.line).orElse(-1),
                    action,
                    details
                ));
            }
        }

        // Helper to extract basic literal values from expressions
        private Object extractValue(Expression expr) {
            if (expr.isStringLiteralExpr()) {
                return expr.asStringLiteralExpr().asString();
            } else if (expr.isIntegerLiteralExpr()) {
                return expr.asIntegerLiteralExpr().asInt();
            } else if (expr.isBooleanLiteralExpr()) {
                return expr.asBooleanLiteralExpr().getValue();
            } else if (expr.isDoubleLiteralExpr()) {
                return expr.asDoubleLiteralExpr().asDouble();
            } else if (expr.isLongLiteralExpr()) {
                return expr.asLongLiteralExpr().asLong();
            } else if (expr.isCharLiteralExpr()) {
                return expr.asCharLiteralExpr().asChar();
            } else if (expr.isNullLiteralExpr()) {
                return null;
            } else if (expr.isNameExpr()) {
                return expr.asNameExpr().getNameAsString();
            } else if (expr.isArrayInitializerExpr()) {
                List<Object> arrayValues = new ArrayList<>();
                for (Expression element : expr.asArrayInitializerExpr().getValues()) {
                    arrayValues.add(extractValue(element));
                }
                return arrayValues;
            } else if (expr.isObjectCreationExpr()) {
                // For simple objects, just return the class name or a string representation
                ObjectCreationExpr oce = expr.asObjectCreationExpr();
                return oce.getType().asString() + (oce.getArguments().isEmpty() ? "" : "(...)");
            }
            // Fallback for complex expressions or unsupported types
            return expr.toString();
        }
    }

    // --- For Static Graph Generation (using the old logic, adapted) ---
    // This method is required by CodeTraceGeneratorService and will replace
    // the old CodeAnalysisService.analyzeCode.
    @Override
    public List<BacktrackingNode> analyzeCodeForStaticGraph(String code) throws Exception {
        List<BacktrackingNode> nodes = new ArrayList<>();
        CompilationUnit cu = StaticJavaParser.parse(code);

        Optional<MethodDeclaration> backtrackingMethod = cu.findAll(MethodDeclaration.class).stream()
                .filter(this::isBacktrackingMethod)
                .findFirst();

        if (backtrackingMethod.isPresent()) {
            MethodDeclaration method = backtrackingMethod.get();
            String methodName = method.getNameAsString();

            BacktrackingNode startNode = BacktrackingNode.createStartNode();
            startNode.getChildren().add(methodName + "_0");
            nodes.add(startNode);

            // This part simulates a basic analysis for the static graph
            // You might want to refine this to better represent a static call graph
            analyzeMethodBodyForStaticGraph(method, methodName, 0, nodes, new HashSet<>());

            BacktrackingNode endNode = BacktrackingNode.createEndNode();
            nodes.add(endNode);
        } else {
            throw new Exception("No backtracking method found in the code for static graph analysis. Consider adding Visualizer.captureStep calls for dynamic tracing.");
        }
        return nodes;
    }

    private static final int MAX_STATIC_GRAPH_DEPTH = 5; // Limit depth for static graph to prevent explosion

    private void analyzeMethodBodyForStaticGraph(MethodDeclaration method, String methodName, int depth, List<BacktrackingNode> nodes, Set<String> processedNodesForStaticGraph) {
        if (depth >= MAX_STATIC_GRAPH_DEPTH) {
            return;
        }

        String nodeId = methodName + "_" + depth;
        if (processedNodesForStaticGraph.contains(nodeId)) {
            return;
        }
        processedNodesForStaticGraph.add(nodeId);

        BacktrackingNode node = BacktrackingNode.builder()
                .id(nodeId)
                .label("Recursion " + depth)
                .type("decision")
                .parameters(new ArrayList<>())
                .returnValues(new ArrayList<>())
                .children(new ArrayList<>())
                .build();

        method.getParameters().forEach(param ->
                node.getParameters().add(param.getTypeAsString() + " " + param.getNameAsString())
        );

        method.getBody().ifPresent(body -> {
            body.findAll(ReturnStmt.class).forEach(returnStmt -> {
                if (returnStmt.getExpression().isPresent()) {
                    String returnValue = returnStmt.getExpression().get().toString();
                    node.getReturnValues().add(returnValue);
                }
            });

            body.findAll(MethodCallExpr.class).forEach(call -> {
                if (call.getNameAsString().equals(methodName)) {
                    String childId = methodName + "_" + (depth + 1);
                    if (!processedNodesForStaticGraph.contains(childId)) {
                        node.getChildren().add(childId);
                        // Recursively analyze child method calls
                        analyzeMethodBodyForStaticGraph(method, methodName, depth + 1, nodes, processedNodesForStaticGraph);
                    }
                }
            });

            body.findAll(IfStmt.class).forEach(ifStmt -> {
                if (ifStmt.getThenStmt().toString().contains("return")) {
                    node.getReturnValues().add("backtrack");
                }
            });
        });
        nodes.add(node);
    }
}