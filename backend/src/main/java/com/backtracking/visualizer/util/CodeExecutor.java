package com.backtracking.visualizer.util;

import com.backtracking.visualizer.dto.StackFrame;
import com.backtracking.visualizer.dto.TraceStep;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import javax.tools.*;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CodeExecutor {
    private static final List<TraceStep> traceSteps = new ArrayList<>();
    private static int currentStep = 0;

    public static void captureStep(String action, String details, Object... variables) {
        TraceStep step = new TraceStep();
        step.setStep(++currentStep);
        step.setAction(action);
        step.setDetails(details);
        step.setLine(Thread.currentThread().getStackTrace()[2].getLineNumber());
        step.setFunction(Thread.currentThread().getStackTrace()[2].getMethodName());
        
        // Capture variables
        Map<String, Object> vars = new HashMap<>();
        for (int i = 0; i < variables.length; i += 2) {
            if (i + 1 < variables.length) {
                vars.put(variables[i].toString(), variables[i + 1]);
            }
        }
        step.setVariables(vars);

        // Capture call stack
        List<StackFrame> callStack = new ArrayList<>();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stackTrace.length; i++) {
            StackTraceElement element = stackTrace[i];
            String className = element.getClassName();
            System.out.println("Processing stack element: " + className + "." + element.getMethodName() + "(" + element.getFileName() + ":" + element.getLineNumber() + ")");

            // Filter out internal framework classes (java, javax, jdk, springframework, our own executor/visualizer)
            if (className.startsWith("java.") ||
                className.startsWith("javax.") ||
                className.startsWith("jdk.") ||
                className.startsWith("org.springframework.") ||
                className.startsWith("com.backtracking.visualizer.util.CodeExecutor") ||
                className.startsWith("com.backtracking.visualizer.util.Visualizer") ||
                className.startsWith("com.backtracking.visualizer.controller.VisualizationController") ||
                className.startsWith("sun.") ||
                className.startsWith("com.sun.") ||
                className.startsWith("org.apache.tomcat.") ||
                className.startsWith("org.apache.catalina.") ||
                className.startsWith("org.apache.coyote.") ||
                className.startsWith("jakarta.")
            ) {
                continue;
            }

            StackFrame frame = new StackFrame();
            frame.setFunctionName(element.getMethodName());
            frame.setLineNumber(element.getLineNumber());
            callStack.add(frame);
        }
        step.setCallStack(callStack);

        traceSteps.add(step);
    }

    public static List<TraceStep> executeCode(String code) throws Exception {
        // Reset state
        traceSteps.clear();
        currentStep = 0;

        Path tempDir = null; // Declare outside for finally block access
        Path sourceFile = null;
        Path classFile = null;
        StandardJavaFileManager fileManager = null; // Declare outside for finally block access

        try {
            // Parse the code to get the class name
            CompilationUnit cu = StaticJavaParser.parse(code);
            Optional<ClassOrInterfaceDeclaration> classDecl = cu.findFirst(ClassOrInterfaceDeclaration.class);
            if (!classDecl.isPresent()) {
                throw new Exception("No class declaration found in the code");
            }
            String className = classDecl.get().getNameAsString();

            // Create a temporary directory for compilation
            try {
                tempDir = Files.createTempDirectory("java_code");
            } catch (IOException e) {
                throw new Exception("Failed to create temporary directory: " + e.getMessage(), e);
            }
            
            // Write the code to a file
            sourceFile = tempDir.resolve(className + ".java");
            Files.write(sourceFile, code.getBytes());

            // Compile the code
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            fileManager = compiler.getStandardFileManager(diagnostics, null, null);
            
            Iterable<? extends JavaFileObject> compilationUnits = 
                fileManager.getJavaFileObjectsFromFiles(List.of(sourceFile.toFile()));
            
            boolean success = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits).call();
            
            if (!success) {
                StringBuilder errorMsg = new StringBuilder("Compilation failed:\n");
                diagnostics.getDiagnostics().forEach(diagnostic -> 
                    errorMsg.append(diagnostic.getMessage(null)).append("\n")
                );
                throw new Exception(errorMsg.toString());
            }

            // Log if the .class file exists after compilation
            classFile = tempDir.resolve(className + ".class");
            if (Files.exists(classFile)) {
                System.out.println("Compiled .class file found at: " + classFile.toAbsolutePath());
            } else {
                System.err.println("Compiled .class file NOT found at: " + classFile.toAbsolutePath());
            }

            // Load and execute the compiled class
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{tempDir.toUri().toURL()});
            System.out.println("Attempting to load class: " + className);
            Class<?> cls = Class.forName(className, true, classLoader);
            System.out.println("Class loaded successfully: " + className);
            
            // Execute the main method
            System.out.println("Attempting to find main method.");
            cls.getMethod("main", String[].class).invoke(null, (Object) new String[0]);
            System.out.println("Main method executed successfully.");

            return new ArrayList<>(traceSteps);

        } catch (Exception e) {
            System.err.println("Caught exception type: " + e.getClass().getName());
            System.err.println("Caught exception message: " + e.getMessage());
            System.err.println("Caught exception toString(): " + e.toString());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush(); // Ensure all content is written to StringWriter
            String stackTrace = sw.toString();

            String errorMessage = "";
            if (e.getMessage() != null && !e.getMessage().trim().isEmpty()) {
                errorMessage += "Details: " + e.getMessage() + "\n";
            }
            if (stackTrace != null && !stackTrace.trim().isEmpty()) {
                errorMessage += "Stack Trace:\n" + stackTrace;
            } else {
                errorMessage += "Unknown error during code execution (stack trace empty). Class: " + e.getClass().getName();
            }
            
            throw new Exception("Error executing code: " + errorMessage, e);
        } finally {
            // Clean up
            try {
                if (fileManager != null) {
                    fileManager.close();
                }
                if (sourceFile != null) {
                    Files.deleteIfExists(sourceFile);
                }
                if (classFile != null) {
                    Files.deleteIfExists(classFile);
                }
                if (tempDir != null) {
                    Files.deleteIfExists(tempDir);
                }
            } catch (IOException e) {
                System.err.println("Error during cleanup: " + e.getMessage());
            }
        }
    }
} 