package me.pinfort.tsvideos.core.external.database.mapper

import me.pinfort.tsvideos.core.external.database.dto.ExecutedFileDto
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface ExecutedFileMapper {
    @Select(
        """
            SELECT
                id,
                file,
                drops,
                size,
                recorded_at,
                channel,
                title,
                channelName,
                duration,
                status
            FROM
                executed_file
            WHERE
                id = #{id}
        """,
    )
    fun find(id: Long): ExecutedFileDto?

    @Delete(
        """
            DELETE FROM
                executed_file
            WHERE
                id = #{id}
        """,
    )
    fun delete(id: Long)

    @Select(
        """
            SELECT
                id,
                file,
                drops,
                size,
                recorded_at,
                channel,
                title,
                channelName,
                duration,
                status
            FROM
                executed_file
            WHERE
                file = #{file}
        """,
    )
    fun selectByFile(file: String): List<ExecutedFileDto>
}
