'use client';

import { useState } from 'react';
import CodeEditor from '../components/CodeEditor';
import FileUpload from '../components/FileUpload';
import Visualization from '../components/Visualization';

export default function Home() {
  const [code, setCode] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [visualizationUrl, setVisualizationUrl] = useState<string>('');

  const handleVisualize = async () => {
    if (!code.trim()) {
      alert('Please enter or upload Java code first');
      return;
    }

    setIsLoading(true);
    try {
      const response = await fetch('/api/visualize', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ code }),
      });

      if (!response.ok) {
        throw new Error('Failed to generate visualization');
      }

      const data = await response.json();
      setVisualizationUrl(data.visualizationUrl);
    } catch (error) {
      console.error('Error:', error);
      alert('Failed to generate visualization. Please check your code and try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <main className="min-h-screen p-8 bg-gray-50">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-4xl font-bold text-center mb-8 text-gray-800">
          Backtracking Algorithm Visualizer
        </h1>
        
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <div className="space-y-6">
            <div className="bg-white rounded-lg shadow-lg p-6">
              <h2 className="text-2xl font-semibold mb-4 text-gray-700">Input Java Code</h2>
              <CodeEditor value={code} onChange={setCode} />
              <FileUpload onFileUpload={setCode} />
            </div>
            
            <button
              onClick={handleVisualize}
              disabled={isLoading}
              className="w-full py-3 px-6 bg-blue-600 text-white rounded-lg font-semibold hover:bg-blue-700 transition-colors disabled:bg-blue-400"
            >
              {isLoading ? 'Generating Visualization...' : 'Visualize'}
            </button>
          </div>

          <div className="bg-white rounded-lg shadow-lg p-6">
            <h2 className="text-2xl font-semibold mb-4 text-gray-700">Visualization</h2>
            <Visualization url={visualizationUrl} isLoading={isLoading} />
          </div>
        </div>
      </div>
    </main>
  );
} 