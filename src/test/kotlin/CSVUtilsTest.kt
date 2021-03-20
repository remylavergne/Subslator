import dev.remylavergne.subslator.domain.CsvUtils
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File

class CSVUtilsTest : FunSpec({
    test("Deserialization") {
        // Create file locally
        val file = File("test-deserialization.csv")
        // Data
        val csvRawData = """
            json_key;original_text;original_text_translated
            JSON_KEY;Text into JSON;Le texte présent dans le JSON 
        """.trimIndent()
        // Write in file
        file.appendText(csvRawData)

        val deserialize = CsvUtils.deserialize(file)

        deserialize.size shouldBe 1
        deserialize.first().jsonKey shouldBe "JSON_KEY"

        // Clean mess
        file.delete()
    }
})