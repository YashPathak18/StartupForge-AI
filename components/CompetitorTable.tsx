import { Competitor } from '@/lib/types';

export default function CompetitorTable({ competitors }: { competitors: Competitor[] }) {
  if (!competitors || competitors.length === 0) return null;
  return (
    <div className="overflow-x-auto rounded-lg border border-border">
      <table className="w-full text-sm text-left">
        <thead className="text-xs uppercase bg-muted/50 border-b border-border">
          <tr>
            <th className="px-6 py-3">Competitor</th>
            <th className="px-6 py-3">Strengths</th>
            <th className="px-6 py-3">Weaknesses</th>
          </tr>
        </thead>
        <tbody>
          {competitors.map((c, i) => (
            <tr key={i} className="border-b border-border last:border-0 hover:bg-muted/20">
              <td className="px-6 py-4 font-medium">{c.name}</td>
              <td className="px-6 py-4">
                <ul className="list-disc list-inside">
                  {c.strengths.map((s, j) => <li key={j}>{s}</li>)}
                </ul>
              </td>
              <td className="px-6 py-4">
                <ul className="list-disc list-inside">
                  {c.weaknesses.map((w, j) => <li key={j}>{w}</li>)}
                </ul>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
