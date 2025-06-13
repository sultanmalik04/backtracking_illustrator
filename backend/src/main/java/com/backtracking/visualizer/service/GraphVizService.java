package com.backtracking.visualizer.service;

import com.backtracking.visualizer.model.BacktrackingNode;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import static guru.nidi.graphviz.model.Factory.*;

@Service
public class GraphVizService {

    public String generateVisualization(List<BacktrackingNode> nodes) {
        MutableGraph graph = mutGraph("backtracking").setDirected(true);
        
        // Add nodes with styling
        for (BacktrackingNode node : nodes) {
            MutableNode graphNode = mutNode(node.getId())
                .add(Label.html(createNodeLabel(node)))
                .add(Style.FILLED)
                .add(Shape.RECTANGLE)
                .add(getNodeColor(node.getType()));
            
            graph.add(graphNode);
        }
        
        // Add edges
        for (BacktrackingNode node : nodes) {
            for (String childId : node.getChildren()) {
                graph.add(mutNode(node.getId()).addLink(mutNode(childId)));
            }
        }
        
        // Generate SVG
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Graphviz.fromGraph(graph)
                .width(1200)
                .height(800)
                .render(Format.SVG)
                .toOutputStream(outputStream);
            
            return "data:image/svg+xml;base64," + 
                   Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate visualization", e);
        }
    }
    
    private String createNodeLabel(BacktrackingNode node) {
        StringBuilder label = new StringBuilder();
        label.append("<table border='0' cellborder='1' cellspacing='0' cellpadding='4'>");
        
        // Node title
        label.append("<tr><td colspan='2'><b>").append(node.getLabel()).append("</b></td></tr>");
        
        // Parameters
        if (!node.getParameters().isEmpty()) {
            label.append("<tr><td colspan='2'><b>Parameters:</b></td></tr>");
            for (String param : node.getParameters()) {
                label.append("<tr><td colspan='2'>").append(param).append("</td></tr>");
            }
        }
        
        // Return values
        if (!node.getReturnValues().isEmpty()) {
            label.append("<tr><td colspan='2'><b>Returns:</b></td></tr>");
            for (String ret : node.getReturnValues()) {
                label.append("<tr><td colspan='2'>").append(ret).append("</td></tr>");
            }
        }
        
        label.append("</table>");
        return label.toString();
    }
    
    private Color getNodeColor(String type) {
        switch (type.toLowerCase()) {
            case "start":
                return Color.rgb("4CAF50"); // Green
            case "end":
                return Color.rgb("F44336"); // Red
            case "backtrack":
                return Color.rgb("FFC107"); // Amber
            case "decision":
                return Color.rgb("2196F3"); // Blue
            default:
                return Color.rgb("E0E0E0"); // Light Gray
        }
    }
} 