import dev.remylavergne.subslator.json.JsonLine
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class JsonLineTest : FunSpec({
    test("JSON line not valid -> no key / no value") {
        val jsonLine = JsonLine("{")

        jsonLine.containsKeyValue() shouldBe false
    }

    test("JSON line not valid -> no key") {
        val jsonLine = JsonLine("\"\": \"value\"")

        jsonLine.containsKeyValue() shouldBe false
    }

    test("JSON line not valid -> no value") {
        val jsonLine = JsonLine("\"MY_JSON_KEY\": {")

        jsonLine.containsKeyValue() shouldBe false
    }

    test("get key") {
        val jsonLine = JsonLine("\"MY_JSON_KEY\": \"MY_VALUE\"")

        jsonLine.containsKeyValue() shouldBe true
        jsonLine.key shouldBe "MY_JSON_KEY"
    }

    test("get value") {
        val jsonLine = JsonLine("\"MY_JSON_KEY\": \"MY_VALUE\"")

        jsonLine.containsKeyValue() shouldBe true
        jsonLine.value shouldBe "MY_VALUE"
    }
})