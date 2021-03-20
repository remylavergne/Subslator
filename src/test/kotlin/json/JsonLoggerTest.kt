package json

import dev.remylavergne.subslator.RuntimeConfig
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe

class JsonLoggerTest : FunSpec({
    test("Get current application path") {
        val runtimeConfig = RuntimeConfig.path
        runtimeConfig shouldNotBe ""
    }
})