import { getApiBaseUrl } from "./client";

export function getVideoStreamUrl(fileId: number): string {
  return `${getApiBaseUrl()}/api/v1/video/${fileId}/stream`;
}
