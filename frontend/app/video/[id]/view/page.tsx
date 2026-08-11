import { notFound } from "next/navigation";
import { getVideoStreamUrl } from "@/lib/api/video";

export default async function VideoViewPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const fileId = Number(id);
  if (Number.isNaN(fileId)) notFound();

  const src = getVideoStreamUrl(fileId);

  return (
    <div className="mx-auto flex w-full max-w-4xl flex-1 flex-col items-center justify-center gap-4 px-6 py-10">
      <video controls preload="auto" width={256} src={src}>
        Download the <a href={src}>MP4</a> video.
      </video>
    </div>
  );
}
