export default function StrategyScoreGauge({ score }: { score: number }) {
  const getColor = (s: number) => {
    if (s >= 80) return 'text-emerald-500';
    if (s >= 50) return 'text-yellow-500';
    return 'text-destructive';
  };

  return (
    <div className="flex flex-col items-center justify-center p-6 bg-card border border-border rounded-xl h-full">
      <div className={`text-5xl font-bold ${getColor(score)}`}>{score}</div>
      <div className="text-sm text-muted-foreground mt-2 font-medium uppercase tracking-wider">Health Score</div>
    </div>
  );
}
