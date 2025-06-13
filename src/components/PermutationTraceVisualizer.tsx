import React, { useState } from 'react';

interface TraceStep {
  step: number;
  function: string;
  variables: { [key: string]: any };
  line: number;
  action: string;
  details: string;
}

interface PermutationTraceVisualizerProps {
  trace: TraceStep[];
}

const PermutationTraceVisualizer: React.FC<PermutationTraceVisualizerProps> = ({ trace }) => {
  const [current, setCurrent] = useState(0);

  if (!trace || trace.length === 0) {
    return <div className="text-gray-500">No trace to display.</div>;
  }

  const step = trace[current];
  const nums = step.variables.nums || [];

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
        <div className="flex items-center space-x-2">
          {Array.isArray(nums) && nums.map((num: number, idx: number) => (
            <div key={idx} className="w-10 h-10 flex items-center justify-center bg-blue-100 border border-blue-400 rounded text-lg font-mono">
              {num}
            </div>
          ))}
        </div>
      </div>
      <div className="text-sm text-gray-500">Function: {step.function} | Start: {step.variables.start} | i: {step.variables.i !== undefined ? step.variables.i : '-'}</div>
    </div>
  );
};

export default PermutationTraceVisualizer; 