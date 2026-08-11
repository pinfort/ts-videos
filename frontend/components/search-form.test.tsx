import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { SearchForm } from "./search-form";

describe("SearchForm", () => {
  it("pre-fills the search input with the current query", () => {
    render(<SearchForm defaultValue="anime" />);
    expect(screen.getByPlaceholderText("番組名で検索")).toHaveValue("anime");
  });

  it("submits as a GET request to the search page", () => {
    render(<SearchForm defaultValue="" />);
    const form = screen.getByRole("button", { name: "検索" }).closest("form");
    expect(form).toHaveAttribute("action", "/");
    expect(form).toHaveAttribute("method", "get");
  });
});
