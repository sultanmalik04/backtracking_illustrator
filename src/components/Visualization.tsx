'use client';

interface VisualizationProps {
  url: string;
  isLoading: boolean;
}

const Visualization: React.FC<VisualizationProps> = ({ url, isLoading }) => {
  if (isLoading) {
    return (
      <div className="h-[400px] flex items-center justify-center bg-gray-50 rounded-lg">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Generating visualization...</p>
        </div>
      </div>
    );
  }

  if (!url) {
    return (
      <div className="h-[400px] flex items-center justify-center bg-gray-50 rounded-lg">
        <p className="text-gray-500">Visualization will appear here</p>
      </div>
    );
  }

  return (
    <div className="h-[400px] flex items-center justify-center bg-gray-50 rounded-lg overflow-hidden">
      <img
        src={url}
        alt="Backtracking visualization"
        className="max-w-full max-h-full object-contain"
      />
    </div>
  );
};

export default Visualization; 