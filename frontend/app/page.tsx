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
  const limitNum = Number(limit) || 10;
  const offsetNum = Number(offset) || 0;
  // Fetch one extra row to detect whether a next page exists without relying
  // on a total count from the API (SearchResponse doesn't provide one).
  const { programs: fetched } = await getPrograms({
    name,
    limit: limitNum + 1,
    offset: offsetNum,
  });
  const hasNextPage = fetched.length > limitNum;
  const programs = hasNextPage ? fetched.slice(0, limitNum) : fetched;

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
