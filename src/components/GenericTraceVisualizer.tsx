import React, { useState } from 'react';
import type ReactType from 'react'; // Explicitly import React as a type

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

interface GenericTraceVisualizerProps {
  trace: TraceStep[];
}

const GenericTraceVisualizer: React.FC<GenericTraceVisualizerProps> = ({ trace }) => {
  const [current, setCurrent] = useState(0);

  if (!trace || trace.length === 0) {
    return <div className="text-gray-500">No trace to display.</div>;
  }

  const step = trace[current];

  // Helper to render complex objects/arrays from the trace
  const renderValue = (value: any): React.ReactElement => {
    if (Array.isArray(value)) {
      return (
        <span className="font-mono">[{value.map((v, i) => <React.Fragment key={i}>{renderValue(v)}{i < value.length - 1 ? ', ' : ''}</React.Fragment>)}]</span>
      );
    } else if (typeof value === 'object' && value !== null) {
      if (Object.keys(value).length === 0) return <span className="font-mono">&#123;&#125;</span>; // Empty object
      return (
        <span className="font-mono">{
          Object.entries(value).map(([k, v]: [string, any], i: number) => (
            <React.Fragment key={k}>{k}: {renderValue(v)}{i < Object.keys(value).length - 1 ? ', ' : ''}</React.Fragment>
          ))
        }</span>
      );
    } else if (typeof value === 'string') {
      return <span className="font-mono">"{value}"</span>; // Quote strings
    } else {
      return <span className="font-mono">{String(value)}</span>;
    }
  };

  return (
    <div className="p-4 bg-white rounded-lg shadow-md">
      <div className="mb-4 flex items-center justify-between">
        <button onClick={() => setCurrent(0)} disabled={current === 0} className="px-2 py-1 mr-2 bg-gray-200 rounded disabled:opacity-50">⏮️</button>
        <button onClick={() => setCurrent((c) => Math.max(0, c - 1))} disabled={current === 0} className="px-2 py-1 mr-2 bg-gray-200 rounded disabled:opacity-50">◀️</button>
        <span className="font-semibold">Step {step.step} / {trace.length}</span>
        <button onClick={() => setCurrent((c) => Math.min(trace.length - 1, c + 1))} disabled={current === trace.length - 1} className="px-2 py-1 ml-2 bg-gray-200 rounded disabled:opacity-50">▶️</button>
        <button onClick={() => setCurrent(trace.length - 1)} disabled={current === trace.length - 1} className="px-2 py-1 ml-2 bg-gray-200 rounded disabled:opacity-50">⏭️</button>
      </div>
      <div className="mb-4">
        <div className="text-lg font-bold mb-2">Action: <span className="text-blue-600">{step.action}</span></div>
        <div className="text-gray-700 mb-2">{step.details}</div>
        
        {/* Display generic variables */}
        {Object.keys(step.variables).length > 0 && (
          <div className="mt-4">
            <h3 className="font-semibold text-gray-700">Variables:</h3>
            <div className="grid grid-cols-2 gap-2 text-sm max-h-40 overflow-y-auto">
              {Object.entries(step.variables).map(([key, value]) => (
                <div key={key} className="bg-gray-100 p-2 rounded">
                  <span className="font-mono text-purple-700">{key}</span>: {renderValue(value)}
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Display Call Stack */}
        {step.callStack && step.callStack.length > 0 && (
          <div className="mt-4">
            <h3 className="font-semibold text-gray-700">Call Stack:</h3>
            <div className="space-y-1 max-h-40 overflow-y-auto">
              {step.callStack.map((frame, index) => (
                <div key={index} className="bg-gray-100 p-2 rounded text-sm">
                  <span className="font-mono text-green-700">{frame.functionName}</span>(
                  {Object.entries(frame.parameters).map(([pName, pVal], i) => (
                    <React.Fragment key={pName}>{pName}: {renderValue(pVal)}{i < Object.keys(frame.parameters).length - 1 ? ', ' : ''}</React.Fragment>
                  ))})
                  {Object.keys(frame.localVariables).length > 0 && (
                    <span className="ml-2 text-xs text-gray-500"> [Local Vars: {renderValue(frame.localVariables)}]</span>
                  )}
                </div>
              ))}
            </div>
          </div>
        )}

      </div>
      <div className="text-sm text-gray-500">Current Line: {step.line !== -1 ? step.line : 'N/A'}</div>
    </div>
  );
};

export default GenericTraceVisualizer; 