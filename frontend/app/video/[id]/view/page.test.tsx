import { render } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";

const { notFound } = vi.hoisted(() => ({
  notFound: vi.fn(() => {
    throw new Error("NEXT_NOT_FOUND");
  }),
}));
vi.mock("next/navigation", () => ({ notFound }));

import VideoViewPage from "./page";

describe("VideoViewPage", () => {
  it("calls notFound for a non-numeric id", async () => {
    await expect(VideoViewPage({ params: Promise.resolve({ id: "abc" }) })).rejects.toThrow(
      "NEXT_NOT_FOUND",
    );
  });

  it("renders a video element pointing at the stream URL", async () => {
    const jsx = await VideoViewPage({ params: Promise.resolve({ id: "42" }) });
    const { container } = render(jsx);

    const video = container.querySelector("video");
    expect(video).toHaveAttribute("src", "http://localhost:8080/api/v1/video/42/stream");
  });
});
