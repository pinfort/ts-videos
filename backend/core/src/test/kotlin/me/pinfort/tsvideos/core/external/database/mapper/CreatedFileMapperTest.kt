package me.pinfort.tsvideos.core.external.database.mapper

import io.kotest.core.spec.style.Test
import me.pinfort.tsvideos.core.external.database.dto.CreatedFileDto
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.testcontainers.context.ImportTestcontainers
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import javax.sql.DataSource

@ImportTestcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@MybatisTest
@SpringJUnitConfig
class CreatedFileMapperTest {
    @Autowired
    private lateinit var dataSource: DataSource

    @Autowired
    private lateinit var createdFileMapper: CreatedFileMapper

    @BeforeEach
    fun setup() {
    }

    @Nested
    inner class FindTest {
        @Test
        fun single() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM created_file").execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO created_file(id,splitted_file_id,file,size,mime,encoding,status) VALUES(1,2,'test',3,'test2','test3','REGISTERED');
            """,
                ).execute()
            connection.commit()

            val actual = createdFileMapper.find(1)
            connection.close()

            Assertions.assertThat(actual).isNotNull
            Assertions.assertThat(actual).isEqualTo(
                CreatedFileDto(
                    id = 1,
                    splittedFileId = 2,
                    file = "test",
                    size = 3,
                    mime = "test2",
                    encoding = "test3",
                    status = CreatedFileDto.Status.REGISTERED,
                ),
            )
        }

        @Test
        fun none() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM created_file").execute()
            connection.commit()

            val actual = createdFileMapper.find(1)
            connection.close()

            Assertions.assertThat(actual).isNull()
        }
    }

    @Nested
    inner class SelectBySplittedFileIdTest {
        @Test
        fun single() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM created_file").execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO created_file(id,splitted_file_id,file,size,mime,encoding,status) VALUES(1,2,'test',3,'test2','test3','REGISTERED');
            """,
                ).execute()
            connection.commit()

            val actual = createdFileMapper.selectBySplittedFileId(2)
            connection.close()

            Assertions.assertThat(actual.size).isEqualTo(1)
            Assertions.assertThat(actual[0]).isEqualTo(
                CreatedFileDto(
                    id = 1,
                    splittedFileId = 2,
                    file = "test",
                    size = 3,
                    mime = "test2",
                    encoding = "test3",
                    status = CreatedFileDto.Status.REGISTERED,
                ),
            )
        }

        @Test
        fun multiple() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM created_file").execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO created_file(id,splitted_file_id,file,size,mime,encoding,status) VALUES(1,2,'test',3,'test2','test3','REGISTERED');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO created_file(id,splitted_file_id,file,size,mime,encoding,status) VALUES(2,2,'test4',4,'test5','test6','ENCODE_SUCCESS');
            """,
                ).execute()
            connection.commit()

            val actual = createdFileMapper.selectBySplittedFileId(2)
            connection.close()

            Assertions.assertThat(actual.size).isEqualTo(2)
            Assertions.assertThat(actual[0]).isEqualTo(
                CreatedFileDto(
                    id = 1,
                    splittedFileId = 2,
                    file = "test",
                    size = 3,
                    mime = "test2",
                    encoding = "test3",
                    status = CreatedFileDto.Status.REGISTERED,
                ),
            )
            Assertions.assertThat(actual[1]).isEqualTo(
                CreatedFileDto(
                    id = 2,
                    splittedFileId = 2,
                    file = "test4",
                    size = 4,
                    mime = "test5",
                    encoding = "test6",
                    status = CreatedFileDto.Status.ENCODE_SUCCESS,
                ),
            )
        }

        @Test
        fun none() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM created_file").execute()
            connection.commit()

            val actual = createdFileMapper.selectBySplittedFileId(1)
            connection.close()

            Assertions.assertThat(actual.size).isEqualTo(0)
        }
    }

    @Nested
    inner class SelectByExecutedFileIdTest {
        @Test
        fun single() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM created_file").execute()
            connection.prepareStatement("DELETE FROM splitted_file").execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO splitted_file(id,executed_file_id,file,size,duration,status) VALUES(1,5,'test6',6,7.0,'COMPRESS_SAVED');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO created_file(id,splitted_file_id,file,size,mime,encoding,status) VALUES(7,1,'test',3,'test2','test3','REGISTERED');
            """,
                ).execute()
            connection.commit()

            val actual = createdFileMapper.selectByExecutedFileId(5)
            connection.close()

            Assertions.assertThat(actual.size).isEqualTo(1)
            Assertions.assertThat(actual[0]).isEqualTo(
                CreatedFileDto(
                    id = 7,
                    splittedFileId = 1,
                    file = "test",
                    size = 3,
                    mime = "test2",
                    encoding = "test3",
                    status = CreatedFileDto.Status.REGISTERED,
                ),
            )
        }

        @Test
        fun multiple() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM created_file").execute()
            connection.prepareStatement("DELETE FROM splitted_file").execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO splitted_file(id,executed_file_id,file,size,duration,status) VALUES(1,5,'test6',6,7.0,'COMPRESS_SAVED');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO splitted_file(id,executed_file_id,file,size,duration,status) VALUES(2,5,'test7',6,7.0,'ENCODE_TASK_ADDED');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO splitted_file(id,executed_file_id,file,size,duration,status) VALUES(3,6,'test8',6,7.0,'COMPRESS_SAVED');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO created_file(id,splitted_file_id,file,size,mime,encoding,status) VALUES(7,2,'test',3,'test2','test3','REGISTERED');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO created_file(id,splitted_file_id,file,size,mime,encoding,status) VALUES(8,2,'test4',4,'test5','test6','ENCODE_SUCCESS');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO created_file(id,splitted_file_id,file,size,mime,encoding,status) VALUES(9,3,'test9',4,'test10','test11','ENCODE_SUCCESS');
            """,
                ).execute()
            connection.commit()

            val actual = createdFileMapper.selectByExecutedFileId(5)
            connection.close()

            Assertions.assertThat(actual.size).isEqualTo(2)
            Assertions.assertThat(actual[0]).isEqualTo(
                CreatedFileDto(
                    id = 7,
                    splittedFileId = 2,
                    file = "test",
                    size = 3,
                    mime = "test2",
                    encoding = "test3",
                    status = CreatedFileDto.Status.REGISTERED,
                ),
            )
            Assertions.assertThat(actual[1]).isEqualTo(
                CreatedFileDto(
                    id = 8,
                    splittedFileId = 2,
                    file = "test4",
                    size = 4,
                    mime = "test5",
                    encoding = "test6",
                    status = CreatedFileDto.Status.ENCODE_SUCCESS,
                ),
            )
        }

        @Test
        fun none() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM created_file").execute()
            connection.prepareStatement("DELETE FROM splitted_file").execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO splitted_file(id,executed_file_id,file,size,duration,status) VALUES(1,4,'test6',6,7.0,'COMPRESS_SAVED');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO created_file(id,splitted_file_id,file,size,mime,encoding,status) VALUES(7,1,'test',3,'test2','test3','REGISTERED');
            """,
                ).execute()
            connection.commit()

            val actual = createdFileMapper.selectByExecutedFileId(5)
            connection.close()

            Assertions.assertThat(actual.size).isEqualTo(0)
        }
    }

    @Nested
    inner class UpdateFileTest {
        @Test
        fun success() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM created_file").execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO created_file(id,splitted_file_id,file,size,mime,encoding,status) VALUES(7,1,'test',3,'test2','test3','REGISTERED');
            """,
                ).execute()
            connection.commit()

            createdFileMapper.updateFile(7, "test4")
            connection.commit()

            // TODO: Use AssertJ-DB
            val actual = createdFileMapper.find(7)
            Assertions.assertThat(actual?.file).isEqualTo("test4")
            connection.close()
        }

        @Test
        fun nothingHasUpdated() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM created_file").execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO created_file(id,splitted_file_id,file,size,mime,encoding,status) VALUES(7,1,'test',3,'test2','test3','REGISTERED');
            """,
                ).execute()
            connection.commit()

            createdFileMapper.updateFile(6, "test4")
            connection.commit()

            // TODO: Use AssertJ-DB
            val actual = createdFileMapper.find(7)
            Assertions.assertThat(actual?.file).isEqualTo("test")
            connection.close()
        }
    }

    @Nested
    inner class DeleteTest {
        @Test
        fun success() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM created_file").execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO created_file(id,splitted_file_id,file,size,mime,encoding,status) VALUES(7,1,'test',3,'test2','test3','REGISTERED');
            """,
                ).execute()
            connection.commit()

            createdFileMapper.delete(7)
            connection.commit()

            connection.prepareStatement("SELECT * FROM created_file").use { statement ->
                statement.executeQuery().use { resultSet ->
                    Assertions.assertThat(resultSet.fetchSize).isEqualTo(0)
                }
            }
            connection.close()
        }

        @Test
        fun nothingHasDeleted() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM created_file").execute()
            connection.commit()

            createdFileMapper.delete(7)
            connection.commit()

            connection.prepareStatement("SELECT * FROM created_file").use { statement ->
                statement.executeQuery().use { resultSet ->
                    Assertions.assertThat(resultSet.fetchSize).isEqualTo(0)
                }
            }
            connection.close()
        }
    }
}
