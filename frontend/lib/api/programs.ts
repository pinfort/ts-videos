import { ApiError, fetchJson } from "./client";
import type { ProgramDetailResponse, SearchProgramsResponse } from "./types";

export async function getPrograms(params: {
  name?: string;
  limit?: number;
  offset?: number;
}): Promise<SearchProgramsResponse> {
  const search = new URLSearchParams();
  if (params.name) search.set("name", params.name);
  if (params.limit !== undefined) search.set("limit", String(params.limit));
  if (params.offset !== undefined) search.set("offset", String(params.offset));
  return fetchJson<SearchProgramsResponse>(`/api/v1/programs?${search.toString()}`);
}

export async function getProgramDetail(id: number): Promise<ProgramDetailResponse | null> {
  try {
    return await fetchJson<ProgramDetailResponse>(`/api/v1/programs/${id}`);
  } catch (err) {
    if (err instanceof ApiError && err.status === 404) return null;
    throw err;
  }
}
