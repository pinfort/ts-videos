import Link from "next/link";

function buildHref(name: string, limit: number, offset: number): string {
  const params = new URLSearchParams();
  if (name) params.set("name", name);
  params.set("limit", String(limit));
  params.set("offset", String(offset));
  return `/?${params.toString()}`;
}

export function Pager({
  name,
  limit,
  offset,
  hasNextPage,
}: {
  name: string;
  limit: number;
  offset: number;
  hasNextPage: boolean;
}) {
  const currentPage = offset === 0 ? 1 : Math.ceil(offset / limit) + 1;
  const backwardHref = buildHref(name, limit, Math.max(offset - limit, 0));
  const forwardHref = buildHref(name, limit, offset + limit);

  return (
    <div className="flex items-center justify-center gap-4 py-4 text-sm">
      {offset > 0 ? (
        <Link href={backwardHref} className="text-blue-600 hover:underline dark:text-blue-400">
          &lt;&lt;
        </Link>
      ) : (
        <span className="text-zinc-400">&lt;&lt;</span>
      )}
      <span>{currentPage}</span>
      {hasNextPage ? (
        <Link href={forwardHref} className="text-blue-600 hover:underline dark:text-blue-400">
          &gt;&gt;
        </Link>
      ) : (
        <span className="text-zinc-400">&gt;&gt;</span>
      )}
    </div>
  );
}
