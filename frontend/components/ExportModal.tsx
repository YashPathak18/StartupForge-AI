'use client';
import { Download, X } from 'lucide-react';

export default function ExportModal({ onClose, data }: { onClose: () => void, data: any }) {
  const handleExportMarkdown = () => {
    const content = JSON.stringify(data, null, 2); // Simple fallback
    const blob = new Blob([content], { type: 'text/markdown' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'blueprint.md';
    a.click();
    URL.revokeObjectURL(url);
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-background/80 backdrop-blur-sm z-50 flex items-center justify-center">
      <div className="bg-card border border-border rounded-xl shadow-lg w-full max-w-md p-6 relative">
        <button onClick={onClose} className="absolute right-4 top-4 text-muted-foreground hover:text-foreground">
          <X className="h-5 w-5" />
        </button>
        <h2 className="text-xl font-semibold mb-4">Export Blueprint</h2>
        <p className="text-sm text-muted-foreground mb-6">Download your generated startup blueprint in markdown format.</p>
        <button
          onClick={handleExportMarkdown}
          className="w-full flex items-center justify-center gap-2 bg-primary text-primary-foreground py-2 px-4 rounded-md hover:bg-primary/90 transition-colors"
        >
          <Download className="h-4 w-4" />
          Download Markdown
        </button>
      </div>
    </div>
  );
}
