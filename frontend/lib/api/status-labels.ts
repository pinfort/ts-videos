import type { ProgramStatus } from "./types";

export function programStatusToJapanese(status: ProgramStatus): string {
  switch (status) {
    case "REGISTERED":
      return "準備中";
    case "COMPLETED":
      return "録画済み";
    case "ERROR":
      return "エラー";
    default:
      return "異常";
  }
}
