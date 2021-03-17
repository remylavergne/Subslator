import com.github.ajalt.clikt.core.BadParameterValue
import com.github.ajalt.clikt.core.PrintHelpMessage
import command.Json
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File

class JsonTest : FunSpec({
    test("Input file arg is mandatory") {
        val args = emptyList<String>()
        try {
            Json().parse(args)
        } catch (e: Exception) {
            e::class shouldBe PrintHelpMessage::class
        }
    }

    test("Input file arg is mandatory and should be exists") {
        val args = listOf<String>("-i", "input.json")
        try {
            Json().parse(args)
        } catch (e: Exception) {
            e::class shouldBe BadParameterValue::class
        }
    }

    test("Input JSON & CSV data file is mandatory") {
        val rawCsv: File = TestUtils.createCsv()
        val input: File = TestUtils.createInputJson()
        val args = listOf<String>("-i", input.path, "-d", rawCsv.path)

        Json().parse(args)
        // Clean
        rawCsv.delete()
        input.delete()
    }
})