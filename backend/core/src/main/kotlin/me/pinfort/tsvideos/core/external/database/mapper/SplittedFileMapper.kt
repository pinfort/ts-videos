package me.pinfort.tsvideos.core.external.database.mapper

import me.pinfort.tsvideos.core.external.database.dto.SplittedFileDto
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface SplittedFileMapper {
    @Select(
        """
        SELECT
            id,
            executed_file_id,
            file,
            size,
            duration,
            status
        FROM
            splitted_file
        WHERE
            executed_file_id = #{executedFileId}
    """,
    )
    fun selectByExecutedFileId(executedFileId: Long): List<SplittedFileDto>

    @Delete(
        """
        DELETE FROM
            splitted_file
        WHERE
            id = #{id}
    """,
    )
    fun delete(id: Long)
}
