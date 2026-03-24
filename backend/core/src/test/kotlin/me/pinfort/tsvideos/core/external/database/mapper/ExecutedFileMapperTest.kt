package me.pinfort.tsvideos.core.external.database.mapper

import io.kotest.core.spec.style.Test
import me.pinfort.tsvideos.core.external.database.dto.ExecutedFileDto
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.testcontainers.context.ImportTestcontainers
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import java.time.LocalDateTime
import javax.sql.DataSource

@ImportTestcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@MybatisTest
@SpringJUnitConfig
class ExecutedFileMapperTest {
    @Autowired
    private lateinit var dataSource: DataSource

    @Autowired
    private lateinit var executedFileMapper: ExecutedFileMapper

    @BeforeEach
    fun setup() {
    }

    @Nested
    inner class FindTest {
        @Test
        fun single() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM executed_file").execute()

            connection
                .prepareStatement(
                    """
                    INSERT INTO executed_file(id,file,drops,`size`,recorded_at,channel,title,channelName,duration,status)
                    VALUES(1,'filepath',0,2,cast('2009-08-03 23:58:01' as datetime),'BSxx','myTitle','myChannel',3,'SPLITTED');
                    """.trimIndent(),
                ).execute()
            connection.commit()

            val actual = executedFileMapper.find(1)
            connection.close()

            Assertions.assertThat(actual).isEqualTo(
                ExecutedFileDto(
                    id = 1,
                    file = "filepath",
                    drops = 0,
                    size = 2,
                    recordedAt = LocalDateTime.of(2009, 8, 3, 23, 58, 1),
                    channel = "BSxx",
                    title = "myTitle",
                    channelName = "myChannel",
                    duration = 3.0,
                    status = ExecutedFileDto.Status.SPLITTED,
                ),
            )
        }

        @Test
        fun none() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM executed_file").execute()
            connection.commit()

            val actual = executedFileMapper.find(1)
            connection.close()

            Assertions.assertThat(actual).isNull()
        }
    }

    @Nested
    inner class DeleteTest {
        @Test
        fun success() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM executed_file").execute()

            connection
                .prepareStatement(
                    """
                    INSERT INTO executed_file(id,file,drops,`size`,recorded_at,channel,title,channelName,duration,status)
                    VALUES(1,'filepath',0,2,cast('2009-08-03 23:58:01' as datetime),'BSxx','myTitle','myChannel',3,'SPLITTED');
                    """.trimIndent(),
                ).execute()
            connection.commit()

            executedFileMapper.delete(1)
            connection.commit()

            connection.prepareStatement("SELECT * FROM executed_file").use { statement ->
                statement.executeQuery().use { resultSet ->
                    Assertions.assertThat(resultSet.fetchSize).isEqualTo(0)
                }
            }
            connection.close()
        }

        @Test
        fun nothingHasDeleted() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM executed_file").execute()
            connection.commit()

            executedFileMapper.delete(1)
            connection.commit()

            connection.prepareStatement("SELECT * FROM executed_file").use { statement ->
                statement.executeQuery().use { resultSet ->
                    Assertions.assertThat(resultSet.fetchSize).isEqualTo(0)
                }
            }
            connection.close()
        }
    }

    @Nested
    inner class SelectByFileTest {
        @Test
        fun success() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM executed_file").execute()

            connection
                .prepareStatement(
                    """
                    INSERT INTO executed_file(id,file,drops,`size`,recorded_at,channel,title,channelName,duration,status)
                    VALUES(1,'filepath',0,2,cast('2009-08-03 23:58:01' as datetime),'BSxx','myTitle','myChannel',3,'SPLITTED');
                    """.trimIndent(),
                ).execute()
            connection.commit()

            val actual = executedFileMapper.selectByFile("filepath")
            connection.close()

            Assertions.assertThat(actual.size).isEqualTo(1)
            Assertions.assertThat(actual[0]).isEqualTo(
                ExecutedFileDto(
                    id = 1,
                    file = "filepath",
                    drops = 0,
                    size = 2,
                    recordedAt = LocalDateTime.of(2009, 8, 3, 23, 58, 1),
                    channel = "BSxx",
                    title = "myTitle",
                    channelName = "myChannel",
                    duration = 3.0,
                    status = ExecutedFileDto.Status.SPLITTED,
                ),
            )
        }

        @Test
        fun none() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM executed_file").execute()
            connection.commit()

            val actual = executedFileMapper.selectByFile("filepath")
            connection.close()

            Assertions.assertThat(actual.size).isEqualTo(0)
        }
    }
}
