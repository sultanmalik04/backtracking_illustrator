'use client';
import React, { useState } from 'react';
import PermutationTraceVisualizer from '../../components/PermutationTraceVisualizer';

interface TraceStep {
  step: number;
  function: string;
  variables: { [key: string]: any };
  line: number;
  action: string;
  details: string;
}

const PermutationTraceDemo: React.FC = () => {
  const [input, setInput] = useState('1,2,3');
  const [trace, setTrace] = useState<TraceStep[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleFetchTrace = async () => {
    setLoading(true);
    setError('');
    setTrace([]);
    try {
      const nums = input.split(',').map((s) => parseInt(s.trim(), 10)).filter((n) => !isNaN(n));
      const response = await fetch('http://localhost:8080/api/trace/permutations', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nums }),
      });
      if (!response.ok) throw new Error('Failed to fetch trace');
      const data = await response.json();
      setTrace(data);
    } catch (err: any) {
      setError(err.message || 'Unknown error');
    } finally {
      setLoading(false);
    }
  };

  const handleTryExample = () => {
    setInput('1,2,3');
    setTimeout(() => {
      handleFetchTrace();
    }, 0);
  };

  return (
    <main className="min-h-screen p-8 bg-gray-50">
      <div className="max-w-2xl mx-auto">
        <h1 className="text-3xl font-bold mb-6 text-center">Permutation Trace Visualizer Demo</h1>
        <div className="mb-4 flex items-center space-x-2">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            className="border border-gray-300 rounded px-3 py-2 w-full"
            placeholder="Enter numbers, e.g. 1,2,3"
          />
          <button
            onClick={handleFetchTrace}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
            disabled={loading}
          >
            {loading ? 'Loading...' : 'Visualize'}
          </button>
          <button
            onClick={handleTryExample}
            className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
            disabled={loading}
          >
            Try Example
          </button>
        </div>
        {error && <div className="text-red-600 mb-4">{error}</div>}
        {trace.length > 0 && <PermutationTraceVisualizer trace={trace} />}
      </div>
    </main>
  );
};

export default PermutationTraceDemo; 