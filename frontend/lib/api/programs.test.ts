import { beforeEach, describe, expect, it, vi } from "vitest";
import { ApiError } from "./client";

const { fetchJson } = vi.hoisted(() => ({ fetchJson: vi.fn() }));

vi.mock("./client", async (importOriginal) => {
  const actual = await importOriginal<typeof import("./client")>();
  return { ...actual, fetchJson };
});

import { getProgramDetail, getPrograms } from "./programs";

describe("getPrograms", () => {
  beforeEach(() => {
    fetchJson.mockReset();
  });

  it("omits unset filters from the query string", async () => {
    fetchJson.mockResolvedValue({ programs: [] });
    await getPrograms({});
    expect(fetchJson).toHaveBeenCalledWith("/api/v1/programs?");
  });

  it("includes name, limit, and offset when provided", async () => {
    fetchJson.mockResolvedValue({ programs: [] });
    await getPrograms({ name: "anime", limit: 20, offset: 40 });
    expect(fetchJson).toHaveBeenCalledWith("/api/v1/programs?name=anime&limit=20&offset=40");
  });
});

describe("getProgramDetail", () => {
  beforeEach(() => {
    fetchJson.mockReset();
  });

  it("returns the detail on success", async () => {
    const detail = { program: { id: 1 }, videoFiles: [] };
    fetchJson.mockResolvedValue(detail);
    await expect(getProgramDetail(1)).resolves.toEqual(detail);
  });

  it("returns null when the program is not found", async () => {
    fetchJson.mockRejectedValue(new ApiError(404, "not found"));
    await expect(getProgramDetail(1)).resolves.toBeNull();
  });

  it("rethrows errors other than a 404", async () => {
    fetchJson.mockRejectedValue(new ApiError(500, "server error"));
    await expect(getProgramDetail(1)).rejects.toBeInstanceOf(ApiError);
  });
});
