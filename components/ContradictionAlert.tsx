import { Contradiction } from '@/lib/types';
import { AlertTriangle } from 'lucide-react';

export default function ContradictionAlert({ contradiction }: { contradiction: Contradiction }) {
  return (
    <div className="bg-destructive/10 border-l-4 border-destructive p-4 rounded-r-lg mb-4">
      <div className="flex items-start">
        <AlertTriangle className="h-5 w-5 text-destructive mr-3 mt-0.5 shrink-0" />
        <div>
          <h4 className="font-semibold text-destructive mb-1">
            Conflict between {contradiction.domain1} & {contradiction.domain2}
          </h4>
          <p className="text-sm text-foreground/80">{contradiction.description}</p>
        </div>
      </div>
    </div>
  );
}
