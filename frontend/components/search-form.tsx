export function SearchForm({ defaultValue }: { defaultValue: string }) {
  return (
    <form action="/" method="get" className="flex gap-2">
      <input
        type="text"
        name="name"
        defaultValue={defaultValue}
        placeholder="番組名で検索"
        className="rounded border border-zinc-300 px-3 py-1.5 text-sm dark:border-zinc-700 dark:bg-zinc-900"
      />
      <button
        type="submit"
        className="rounded bg-zinc-900 px-4 py-1.5 text-sm text-white hover:bg-zinc-700 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-zinc-300"
      >
        検索
      </button>
    </form>
  );
}
