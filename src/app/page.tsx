'use client';

import { useState } from 'react';
import CodeEditor from '../components/CodeEditor';
import FileUpload from '../components/FileUpload';
import Visualization from '../components/Visualization';
import { useRouter } from 'next/navigation';
import Link from 'next/link';

const API_URL = 'http://localhost:8080/api/visualize';

export default function Home() {
  const [code, setCode] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [visualizationUrl, setVisualizationUrl] = useState<string>('');
  const [error, setError] = useState<string>('');
  const router = useRouter();

  const handleVisualize = async () => {
    if (!code.trim()) {
      setError('Please enter or upload Java code first');
      return;
    }

    setIsLoading(true);
    setError('');
    try {
      const response = await fetch(API_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ code }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to generate visualization');
      }

      const data = await response.json();
      if (data.visualizationUrl) {
        setVisualizationUrl(data.visualizationUrl);
      } else {
        throw new Error('No visualization URL in response');
      }
    } catch (error) {
      console.error('Error:', error);
      setError(error instanceof Error ? error.message : 'Failed to generate visualization');
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
        <div className="flex justify-center mb-8">
          <Link href="/permutation-trace-demo" className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition duration-200">
            Try Permutation Example
          </Link>
        </div>
        
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <div className="space-y-6">
            <div className="bg-white rounded-lg shadow-lg p-6">
              <h2 className="text-2xl font-semibold mb-4 text-gray-700">Input Java Code</h2>
              <CodeEditor value={code} onChange={setCode} />
              <FileUpload onFileUpload={setCode} />
            </div>
            
            {error && (
              <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
                {error}
              </div>
            )}
            
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

        <div className="w-full text-center mt-8">
          <Link href="/generic-trace-demo" className="px-6 py-3 bg-green-500 text-white rounded-lg text-xl font-semibold hover:bg-green-600 transition duration-200 shadow-lg">
            Try Generic Trace Demo
          </Link>
        </div>
      </div>
    </main>
  );
} 