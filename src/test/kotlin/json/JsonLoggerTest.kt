package json

import RuntimeConfig
import io.kotest.core.spec.style.FunSpec

class JsonLoggerTest : FunSpec({
    test("Get current application path") {
        val runtimeConfig = RuntimeConfig.getCurrentPath()

        println(runtimeConfig)
    }
}) {


}