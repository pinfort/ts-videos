export function getApiBaseUrl(): string {
  return process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
}

export class ApiError extends Error {
  constructor(
    public readonly status: number,
    message: string,
  ) {
    super(message);
    this.name = "ApiError";
  }
}

export async function fetchJson<T>(path: string, init?: RequestInit): Promise<T> {
  const url = new URL(path, getApiBaseUrl());
  const res = await fetch(url.toString(), {
    cache: "no-store",
    ...init,
  });
  if (!res.ok) {
    throw new ApiError(res.status, `Request to ${url.toString()} failed with ${res.status}`);
  }
  return res.json() as Promise<T>;
}
