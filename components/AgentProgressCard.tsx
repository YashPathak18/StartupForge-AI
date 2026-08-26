import { CheckCircle2, Circle, Loader2 } from 'lucide-react';

export default function AgentProgressCard({ name, status }: { name: string, status: string }) {
  return (
    <div className={`p-4 rounded-xl border transition-all ${status === 'IN_PROGRESS' ? 'border-primary shadow-[0_0_15px_rgba(var(--primary),0.2)]' : 'border-border'}`}>
      <div className="flex items-center justify-between">
        <h3 className="font-semibold text-lg">{name}</h3>
        {status === 'COMPLETED' && <CheckCircle2 className="h-5 w-5 text-emerald-500" />}
        {status === 'IN_PROGRESS' && <Loader2 className="h-5 w-5 text-primary animate-spin" />}
        {status === 'PENDING' && <Circle className="h-5 w-5 text-muted-foreground" />}
      </div>
      <p className="text-sm text-muted-foreground mt-2 capitalize">{status.toLowerCase().replace('_', ' ')}</p>
    </div>
  );
}
