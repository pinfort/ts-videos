import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { Pager } from "./pager";

describe("Pager", () => {
  it("disables the back link on the first page", () => {
    render(<Pager name="" limit={20} offset={0} hasNextPage={true} />);
    expect(screen.getByText("<<")).not.toHaveAttribute("href");
    expect(screen.getByText(">>")).toHaveAttribute("href", "/?limit=20&offset=20");
  });

  it("disables the forward link on the last page", () => {
    render(<Pager name="" limit={20} offset={20} hasNextPage={false} />);
    expect(screen.getByText("<<")).toHaveAttribute("href", "/?limit=20&offset=0");
    expect(screen.getByText(">>")).not.toHaveAttribute("href");
  });

  it("preserves the search name across pages and computes the current page number", () => {
    render(<Pager name="anime" limit={10} offset={30} hasNextPage={true} />);
    expect(screen.getByText("4")).toBeInTheDocument();
    expect(screen.getByText(">>")).toHaveAttribute("href", "/?name=anime&limit=10&offset=40");
  });
});
