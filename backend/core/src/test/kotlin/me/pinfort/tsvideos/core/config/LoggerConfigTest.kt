package me.pinfort.tsvideos.core.config

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.slf4j.Logger
import org.springframework.context.annotation.AnnotationConfigApplicationContext

class FirstLoggerHolder(
    val logger: Logger,
)

class SecondLoggerHolder(
    val logger: Logger,
)

class LoggerConfigTest :
    ExpectSpec({
        context("logger") {
            expect("names the logger after each injection target") {
                val context = AnnotationConfigApplicationContext()
                context.register(
                    LoggerConfig::class.java,
                    FirstLoggerHolder::class.java,
                    SecondLoggerHolder::class.java,
                )
                context.refresh()

                // シングルトンだと2つ目以降が1つ目のLoggerを使い回してしまう
                context.getBean(FirstLoggerHolder::class.java).logger.name shouldBe FirstLoggerHolder::class.java.name
                context.getBean(SecondLoggerHolder::class.java).logger.name shouldBe SecondLoggerHolder::class.java.name

                context.close()
            }
        }
    })
