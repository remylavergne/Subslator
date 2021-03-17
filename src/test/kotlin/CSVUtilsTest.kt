import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File

class CSVUtilsTest : FunSpec({
    test("Deserialization") {
        // Create file locally
        val file = File("test-deserialization.csv")
        // Data
        val csvRawData = """
            ID-libelle;Texte;Libellé à traduire;Traduction
            JSON_KEY;Text into JSON;Text into JSON;Le texte présent dans le JSON 
        """.trimIndent()
        // Write in file
        file.appendText(csvRawData)

        val deserialize = CSVUtils.deserialize(file)

        deserialize.size shouldBe 1
        deserialize.first().libelleId shouldBe "JSON_KEY"
    }
})