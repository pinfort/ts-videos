import { Pager } from "@/components/pager";
import { ProgramsTable } from "@/components/programs-table";
import { SearchForm } from "@/components/search-form";
import { getPrograms } from "@/lib/api/programs";

export default async function HomePage({
  searchParams,
}: {
  searchParams: Promise<{ name?: string; limit?: string; offset?: string }>;
}) {
  const { name = "", limit = "10", offset = "0" } = await searchParams;
  // Clamp to the ranges the backend actually accepts (limit: 1-100, offset: >=0)
  // so a hand-edited URL can't send an out-of-range value and hit its 400.
  const limitNum = Math.min(Math.max(Number(limit) || 10, 1), 100);
  const offsetNum = Math.max(Number(offset) || 0, 0);
  // Fetch one extra row to detect whether a next page exists without relying
  // on a total count from the API (SearchResponse doesn't provide one). Can't
  // do this when limitNum is already at the backend's max, so fall back to
  // the less precise length===limit heuristic in that one case.
  const fetchLimit = Math.min(limitNum + 1, 100);
  const { programs: fetched } = await getPrograms({
    name,
    limit: fetchLimit,
    offset: offsetNum,
  });
  const hasNextPage =
    fetchLimit > limitNum ? fetched.length > limitNum : fetched.length === limitNum;
  const programs = fetched.length > limitNum ? fetched.slice(0, limitNum) : fetched;

  return (
    <div className="mx-auto flex w-full max-w-4xl flex-1 flex-col gap-6 px-6 py-10">
      <header>
        <SearchForm defaultValue={name} />
      </header>
      <main>
        <ProgramsTable programs={programs} />
      </main>
      <footer>
        <Pager name={name} limit={limitNum} offset={offsetNum} hasNextPage={hasNextPage} />
      </footer>
    </div>
  );
}
