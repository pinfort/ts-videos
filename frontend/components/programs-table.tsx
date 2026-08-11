import Link from "next/link";
import { programStatusToJapanese } from "@/lib/api/status-labels";
import type { Program } from "@/lib/api/types";

export function ProgramsTable({ programs }: { programs: Program[] }) {
  return (
    <table className="min-w-full divide-y divide-zinc-200 text-sm dark:divide-zinc-800">
      <thead>
        <tr>
          <th className="px-4 py-2 text-left font-medium">番号</th>
          <th className="px-4 py-2 text-left font-medium">ファイル名</th>
          <th className="px-4 py-2 text-left font-medium">executedFileId</th>
          <th className="px-4 py-2 text-left font-medium">状態</th>
          <th className="px-4 py-2 text-left font-medium">ドロップ数</th>
        </tr>
      </thead>
      <tbody className="divide-y divide-zinc-200 dark:divide-zinc-800">
        {programs.map((program) => (
          <tr key={program.id}>
            <td className="px-4 py-2">{program.id}</td>
            <td className="px-4 py-2">
              <Link
                href={`/programs/${program.id}`}
                className="text-blue-600 hover:underline dark:text-blue-400"
              >
                {program.name}
              </Link>
            </td>
            <td className="px-4 py-2">{program.executedFileId}</td>
            <td className="px-4 py-2">{programStatusToJapanese(program.status)}</td>
            <td className="px-4 py-2">{program.drops}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
