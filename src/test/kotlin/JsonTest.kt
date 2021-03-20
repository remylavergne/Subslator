import com.github.ajalt.clikt.core.BadParameterValue
import com.github.ajalt.clikt.core.PrintHelpMessage
import dev.remylavergne.subslator.command.Json
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

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
})