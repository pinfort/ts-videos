import { afterEach, describe, expect, it, vi } from "vitest";
import { ApiError, fetchJson, getApiBaseUrl } from "./client";

describe("getApiBaseUrl", () => {
  const originalEnv = process.env.NEXT_PUBLIC_API_BASE_URL;

  afterEach(() => {
    if (originalEnv === undefined) {
      delete process.env.NEXT_PUBLIC_API_BASE_URL;
    } else {
      process.env.NEXT_PUBLIC_API_BASE_URL = originalEnv;
    }
  });

  it("falls back to localhost when unset", () => {
    delete process.env.NEXT_PUBLIC_API_BASE_URL;
    expect(getApiBaseUrl()).toBe("http://localhost:8080");
  });

  it("uses the configured base URL", () => {
    process.env.NEXT_PUBLIC_API_BASE_URL = "https://api.example.com";
    expect(getApiBaseUrl()).toBe("https://api.example.com");
  });
});

describe("fetchJson", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("resolves with the parsed JSON body on success", async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ hello: "world" }),
    });
    vi.stubGlobal("fetch", fetchMock);

    await expect(fetchJson("/programs")).resolves.toEqual({ hello: "world" });
    expect(fetchMock).toHaveBeenCalledWith(
      "http://localhost:8080/programs",
      expect.objectContaining({ cache: "no-store" }),
    );
  });

  it("throws an ApiError with the response status when the request fails", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue({
        ok: false,
        status: 404,
        json: () => Promise.resolve({}),
      }),
    );

    await expect(fetchJson("/programs/1")).rejects.toBeInstanceOf(ApiError);
  });

  it("propagates the error when fetch itself rejects", async () => {
    const networkError = new TypeError("Failed to fetch");
    vi.stubGlobal("fetch", vi.fn().mockRejectedValue(networkError));

    await expect(fetchJson("/programs")).rejects.toBe(networkError);
  });
});
