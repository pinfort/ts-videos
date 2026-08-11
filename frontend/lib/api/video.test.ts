import { afterEach, describe, expect, it } from "vitest";
import { getVideoStreamUrl } from "./video";

describe("getVideoStreamUrl", () => {
  const originalEnv = process.env.NEXT_PUBLIC_API_BASE_URL;

  afterEach(() => {
    if (originalEnv === undefined) {
      delete process.env.NEXT_PUBLIC_API_BASE_URL;
    } else {
      process.env.NEXT_PUBLIC_API_BASE_URL = originalEnv;
    }
  });

  it("builds the stream URL from the configured API base", () => {
    process.env.NEXT_PUBLIC_API_BASE_URL = "https://api.example.com";
    expect(getVideoStreamUrl(7)).toBe("https://api.example.com/api/v1/video/7/stream");
  });

  it("falls back to localhost when unset", () => {
    delete process.env.NEXT_PUBLIC_API_BASE_URL;
    expect(getVideoStreamUrl(7)).toBe("http://localhost:8080/api/v1/video/7/stream");
  });
});
