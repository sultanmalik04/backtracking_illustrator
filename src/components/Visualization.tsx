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

  // Extract the base64 data from the URL if it's wrapped in a CSS background-image
  const getSvgData = (url: string) => {
    if (url.includes('background-image: url(')) {
      const match = url.match(/url\("(.*)"\)/);
      return match ? match[1] : url;
    }
    return url;
  };

  const svgData = getSvgData(url);
  console.log(svgData);

  return (
    <div className="h-[400px] flex items-center justify-center bg-gray-50 rounded-lg overflow-hidden">
      {svgData.startsWith('data:image/svg+xml') ? (
        <div 
          className="max-w-full max-h-full"
          dangerouslySetInnerHTML={{ 
            __html: decodeURIComponent(atob(svgData.split(',')[1]))
          }}
        />
      ) : (
        <img
          src={svgData}
          alt="Backtracking visualization"
          className="max-w-full max-h-full object-contain"
        />
      )}
    </div>
  );
};

export default Visualization; 