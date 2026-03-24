package me.pinfort.tsvideos.core.external.database.mapper

import io.kotest.core.spec.style.Test
import me.pinfort.tsvideos.core.external.database.dto.ProgramDto
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
class ProgramMapperTest {
    @Autowired
    private lateinit var dataSource: DataSource

    @Autowired
    private lateinit var programMapper: ProgramMapper

    @BeforeEach
    fun setup() {
    }

    @Nested
    inner class SelectByNameTest {
        @Test
        fun single() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM program").execute()
            connection.prepareStatement("DELETE FROM executed_file").execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO program(id,name,executed_file_id,status) VALUES(1,'test',1,'REGISTERED');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO program(id,name,executed_file_id,status) VALUES(2,'esta',2,'REGISTERED');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO program(id,name,executed_file_id,status) VALUES(3,'aest',3,'REGISTERED');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                    INSERT INTO executed_file(id,file,drops,`size`,recorded_at,channel,title,channelName,duration,status)
                    VALUES(1,'filepath',0,2,cast('2009-08-03 23:58:01' as datetime),'BSxx','myTitle','myChannel',3,'SPLITTED');
                    """.trimIndent(),
                ).execute()
            connection.commit()

            val actual = programMapper.selectByName("test")
            connection.close()

            Assertions.assertThat(actual.size).isEqualTo(1)

            Assertions.assertThat(actual[0]).isEqualTo(
                ProgramDto(
                    1,
                    "test",
                    1,
                    ProgramDto.Status.REGISTERED,
                    0,
                    2,
                    LocalDateTime.of(2009, 8, 3, 23, 58, 1),
                    "BSxx",
                    "myTitle",
                    "myChannel",
                    3.0,
                ),
            )
        }

        @Test
        fun multiple() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM program").execute()
            connection.prepareStatement("DELETE FROM executed_file").execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO program(id,name,executed_file_id,status) VALUES(1,'test',1,'REGISTERED');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO program(id,name,executed_file_id,status) VALUES(2,'atest',2,'REGISTERED');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO program(id,name,executed_file_id,status) VALUES(3,'testa',3,'REGISTERED');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                    INSERT INTO executed_file(id,file,drops,`size`,recorded_at,channel,title,channelName,duration,status)
                    VALUES(1,'filepath',0,2,cast('2009-08-03 23:58:01' as datetime),'BSxx','myTitle','myChannel',3,'SPLITTED');
                    """.trimIndent(),
                ).execute()
            connection
                .prepareStatement(
                    """
                    INSERT INTO executed_file(id,file,drops,`size`,recorded_at,channel,title,channelName,duration,status)
                    VALUES(2,'filepath2',100,2,cast('2009-08-03 23:58:01' as datetime),'BSxx','myTitle','myChannel',3,'SPLITTED');
                    """.trimIndent(),
                ).execute()
            connection.commit()

            val actual = programMapper.selectByName("test")
            connection.close()

            Assertions.assertThat(actual.size).isEqualTo(3)

            Assertions.assertThat(actual[0]).isEqualTo(
                ProgramDto(
                    1,
                    "test",
                    1,
                    ProgramDto.Status.REGISTERED,
                    0,
                    2,
                    LocalDateTime.of(2009, 8, 3, 23, 58, 1),
                    "BSxx",
                    "myTitle",
                    "myChannel",
                    3.0,
                ),
            )

            Assertions.assertThat(actual[1]).isEqualTo(
                ProgramDto(
                    2,
                    "atest",
                    2,
                    ProgramDto.Status.REGISTERED,
                    100,
                    2,
                    LocalDateTime.of(2009, 8, 3, 23, 58, 1),
                    "BSxx",
                    "myTitle",
                    "myChannel",
                    3.0,
                ),
            )

            Assertions.assertThat(actual[2]).isEqualTo(
                ProgramDto(
                    3,
                    "testa",
                    3,
                    ProgramDto.Status.REGISTERED,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                ),
            )
        }

        @Test
        fun none() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM program").execute()
            connection.prepareStatement("DELETE FROM executed_file").execute()
            connection.commit()

            val actual = programMapper.selectByName("test")
            connection.close()

            Assertions.assertThat(actual.size).isEqualTo(0)
        }
    }

    @Nested
    inner class FindTest {
        @Test
        fun single() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM program").execute()
            connection.prepareStatement("DELETE FROM executed_file").execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO program(id,name,executed_file_id,status) VALUES(1,'test',1,'REGISTERED');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO program(id,name,executed_file_id,status) VALUES(2,'esta',2,'REGISTERED');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO program(id,name,executed_file_id,status) VALUES(3,'aest',3,'REGISTERED');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                    INSERT INTO executed_file(id,file,drops,`size`,recorded_at,channel,title,channelName,duration,status)
                    VALUES(1,'filepath',0,2,cast('2009-08-03 23:58:01' as datetime),'BSxx','myTitle','myChannel',3,'SPLITTED');
                    """.trimIndent(),
                ).execute()
            connection.commit()

            val actual = programMapper.find(1)
            connection.close()

            Assertions.assertThat(actual).isEqualTo(
                ProgramDto(
                    1,
                    "test",
                    1,
                    ProgramDto.Status.REGISTERED,
                    0,
                    2,
                    LocalDateTime.of(2009, 8, 3, 23, 58, 1),
                    "BSxx",
                    "myTitle",
                    "myChannel",
                    3.0,
                ),
            )
        }

        @Test
        fun none() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM program").execute()
            connection.prepareStatement("DELETE FROM executed_file").execute()
            connection.commit()

            val actual = programMapper.find(1)
            connection.close()

            Assertions.assertThat(actual).isNull()
        }
    }

    @Nested
    inner class DeleteByIdTest {
        @Test
        fun one() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM program").execute()
            connection.prepareStatement("DELETE FROM executed_file").execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO program(id,name,executed_file_id,status) VALUES(1,'test',1,'REGISTERED');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO program(id,name,executed_file_id,status) VALUES(2,'esta',2,'REGISTERED');
            """,
                ).execute()
            connection
                .prepareStatement(
                    """
                INSERT INTO program(id,name,executed_file_id,status) VALUES(3,'aest',3,'REGISTERED');
            """,
                ).execute()
            connection.commit()

            programMapper.deleteById(1)

            Assertions.assertThat(programMapper.find(1)).isNull()
            connection.close()
        }

        @Test
        fun none() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM program").execute()
            connection.commit()

            programMapper.deleteById(1)

            Assertions.assertThat(programMapper.find(1)).isNull()
            connection.close()
        }
    }

    @Nested
    inner class FindByExecutedFileIdTest {
        @Test
        fun single() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM program").execute()
            connection.prepareStatement("DELETE FROM executed_file").execute()
            connection
                .prepareStatement(
                    """
            INSERT INTO program(id,name,executed_file_id,status) VALUES(1,'test',1,'REGISTERED');
        """,
                ).execute()
            connection
                .prepareStatement(
                    """
            INSERT INTO program(id,name,executed_file_id,status) VALUES(2,'esta',2,'REGISTERED');
        """,
                ).execute()
            connection
                .prepareStatement(
                    """
            INSERT INTO program(id,name,executed_file_id,status) VALUES(3,'aest',3,'REGISTERED');
        """,
                ).execute()
            connection
                .prepareStatement(
                    """
                    INSERT INTO executed_file(id,file,drops,`size`,recorded_at,channel,title,channelName,duration,status)
                    VALUES(1,'filepath',0,2,cast('2009-08-03 23:58:01' as datetime),'BSxx','myTitle','myChannel',3,'SPLITTED');
                    """.trimIndent(),
                ).execute()
            connection.commit()

            val actual = programMapper.findByExecutedFileId(1)
            connection.close()

            Assertions.assertThat(actual).isEqualTo(
                ProgramDto(
                    1,
                    "test",
                    1,
                    ProgramDto.Status.REGISTERED,
                    0,
                    2,
                    LocalDateTime.of(2009, 8, 3, 23, 58, 1),
                    "BSxx",
                    "myTitle",
                    "myChannel",
                    3.0,
                ),
            )
        }

        @Test
        fun none() {
            val connection = dataSource.connection
            connection.prepareStatement("DELETE FROM program").execute()
            connection.prepareStatement("DELETE FROM executed_file").execute()
            connection.commit()

            val actual = programMapper.findByExecutedFileId(1)
            connection.close()

            Assertions.assertThat(actual).isNull()
        }
    }
}
