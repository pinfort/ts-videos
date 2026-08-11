package me.pinfort.tsvideos.manager.api

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.test.context.SpringBootTest

// Regression guard for the class of bug fixed by MyBatisConfig (a bean-wiring mistake,
// e.g. a missing @MapperScan, throws during context startup and fails this test).
// manager:api isn't otherwise built/run by CI (see jar.backend.yaml), so nothing else
// here would catch that.
@SpringBootTest
class ApiApplicationTest :
    FunSpec({
        test("application context loads") {}
    }) {
    override val extensions = listOf(SpringExtension())
}
