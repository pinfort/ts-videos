import { describe, expect, it } from "vitest";
import type { ProgramStatus } from "./types";
import { programStatusToJapanese } from "./status-labels";

describe("programStatusToJapanese", () => {
  it.each([
    ["REGISTERED", "準備中"],
    ["COMPLETED", "録画済み"],
    ["ERROR", "エラー"],
  ] as const)("maps %s to %s", (status, expected) => {
    expect(programStatusToJapanese(status)).toBe(expected);
  });

  it("falls back to 異常 for an unrecognized status", () => {
    expect(programStatusToJapanese("SOMETHING_NEW" as ProgramStatus)).toBe("異常");
  });
});
