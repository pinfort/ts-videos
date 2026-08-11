import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import type { Program } from "@/lib/api/types";
import { ProgramsTable } from "./programs-table";

function makeProgram(overrides: Partial<Program> = {}): Program {
  return {
    id: 1,
    name: "Sample Program",
    executedFileId: 10,
    status: "COMPLETED",
    drops: 3,
    size: 1000,
    recordedAt: "2024-01-01T00:00:00Z",
    channel: "1",
    title: "Sample Program",
    channelName: "Channel 1",
    duration: 1800,
    ...overrides,
  };
}

describe("ProgramsTable", () => {
  it("renders each program's id, name, status label, and drop count", () => {
    render(<ProgramsTable programs={[makeProgram()]} />);

    expect(screen.getByText("1")).toBeInTheDocument();
    expect(screen.getByText("Sample Program")).toHaveAttribute("href", "/programs/1");
    expect(screen.getByText("録画済み")).toBeInTheDocument();
    expect(screen.getByText("3")).toBeInTheDocument();
  });

  it("renders one row per program", () => {
    render(<ProgramsTable programs={[makeProgram({ id: 1 }), makeProgram({ id: 2, name: "Second" })]} />);
    expect(screen.getAllByRole("row")).toHaveLength(3); // header + 2 data rows
  });
});
