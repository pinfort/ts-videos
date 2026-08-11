import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import ProgramNotFound from "./not-found";

describe("ProgramNotFound", () => {
  it("shows a not-found message with a link back to search", () => {
    render(<ProgramNotFound />);
    expect(screen.getByText("指定された番組が見つかりませんでした。")).toBeInTheDocument();
    expect(screen.getByText("検索に戻る")).toHaveAttribute("href", "/");
  });
});
