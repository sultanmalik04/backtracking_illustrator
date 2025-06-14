'use client';

import React, { useState } from 'react';
import CodeMirror from '@uiw/react-codemirror';
import { java } from '@codemirror/lang-java';
import GenericTraceVisualizer from '../../components/GenericTraceVisualizer';

interface StackFrame {
  functionName: string;
  parameters: { [key: string]: any };
  localVariables: { [key: string]: any };
}

interface TraceStep {
  step: number;
  function: string;
  variables: { [key: string]: any };
  callStack: StackFrame[];
  line: number;
  action: string;
  details: string;
}

const GenericTraceDemoPage: React.FC = () => {
  const [code, setCode] = useState<string>(`import com.backtracking.visualizer.util.Visualizer;
import java.util.*;

public class NQueensSolver {
    public static List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        char[][] board = new char[n][n];
        for (char[] row : board) {
            Arrays.fill(row, '.');
        }
        Visualizer.captureStep("Initializing board", "n", n, "board", board);
        backtrack(board, 0, result);
        return result;
    }

    private static void backtrack(char[][] board, int row, List<List<String>> result) {
        Visualizer.captureStep("Starting backtrack", "row", row, "board", board);
        
        if (row == board.length) {
            Visualizer.captureStep("Found solution", "row", row);
            List<String> solution = new ArrayList<>();
            for (char[] r : board) {
                solution.add(new String(r));
            }
            result.add(solution);
            return;
        }

        for (int col = 0; col < board.length; col++) {
            Visualizer.captureStep("Trying position", "row", row, "col", col);
            
            if (isValid(board, row, col)) {
                Visualizer.captureStep("Valid position found", "row", row, "col", col);
                board[row][col] = 'Q';
                backtrack(board, row + 1, result);
                board[row][col] = '.';
                Visualizer.captureStep("Backtracking", "row", row, "col", col);
            } else {
                Visualizer.captureStep("Invalid position", "row", row, "col", col);
            }
        }
    }

    private static boolean isValid(char[][] board, int row, int col) {
        Visualizer.captureStep("Checking validity", "row", row, "col", col);
        
        // Check column
        for (int i = 0; i < row; i++) {
            if (board[i][col] == 'Q') {
                Visualizer.captureStep("Invalid: Queen in same column", "row", row, "col", col);
                return false;
            }
        }

        // Check upper-left diagonal
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (board[i][j] == 'Q') {
                Visualizer.captureStep("Invalid: Queen in upper-left diagonal", "row", row, "col", col);
                return false;
            }
        }

        // Check upper-right diagonal
        for (int i = row - 1, j = col + 1; i >= 0 && j < board.length; i--, j++) {
            if (board[i][j] == 'Q') {
                Visualizer.captureStep("Invalid: Queen in upper-right diagonal", "row", row, "col", col);
                return false;
            }
        }

        Visualizer.captureStep("Position is valid", "row", row, "col", col);
        return true;
    }

    public static void main(String[] args) {
        int n = 4; // Solve for 4x4 board
        List<List<String>> solutions = solveNQueens(n);
        System.out.println("Found " + solutions.size() + " solutions for " + n + "-queens problem");
    }
}`);
  const [traceData, setTraceData] = useState<TraceStep[] | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const handleCodeChange = (value: string) => {
    setCode(value);
  };

  const handleSubmit = async () => {
    setLoading(true);
    setError(null);
    setTraceData(null); // Clear previous trace
    try {
      const requestBody = { code: code }; // Changed from javaCode to code
      console.log('Sending request with body:', requestBody); // Debug log

      const response = await fetch('http://localhost:8080/api/trace/generic', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestBody),
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error('Error response:', errorText); // Debug log
        throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
      }

      const data: TraceStep[] = await response.json();
      console.log('Received trace data:', data); // Debug log
      setTraceData(data);
    } catch (e: any) {
      setError(`Failed to fetch trace: ${e.message}`);
      console.error('Failed to fetch trace:', e);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-3xl font-bold mb-6 text-center">Generic Backtracking Trace Visualizer</h1>

      <div className="flex flex-col md:flex-row gap-6">
        <div className="md:w-1/2">
          <h2 className="text-xl font-semibold mb-3">Paste your annotated Java code:</h2>
          <div className="border rounded-lg overflow-hidden mb-4">
            <CodeMirror
              value={code}
              height="400px"
              extensions={[java()]}
              onChange={handleCodeChange}
              theme="light"
            />
          </div>
          <button
            onClick={handleSubmit}
            disabled={loading}
            className="w-full bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline transition duration-200"
          >
            {loading ? 'Generating Trace...' : 'Generate Trace'}
          </button>
          {error && <p className="text-red-500 mt-2 text-center">{error}</p>}
        </div>

        <div className="md:w-1/2">
          <h2 className="text-xl font-semibold mb-3">Visualization:</h2>
          <div className="bg-gray-50 p-4 rounded-lg shadow-inner min-h-[400px] flex items-center justify-center">
            {traceData ? (
              <GenericTraceVisualizer trace={traceData} />
            ) : (
              <p className="text-gray-500">Submit code to see the trace visualization.</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default GenericTraceDemoPage; 