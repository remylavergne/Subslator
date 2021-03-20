import dev.remylavergne.subslator.domain.CsvData
import dev.remylavergne.subslator.domain.CsvState
import dev.remylavergne.subslator.domain.ext.state
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CsvDataTest : FunSpec({
    test("ListState -> Empty list") {
        val csvData: List<CsvData> = emptyList()
        val state = csvData.state()
        state shouldBe CsvState.List.NoData
    }

    test("ListState -> Translation data available") {
        val csvData: List<CsvData> = listOf(CsvData("", "", ""))
        val state = csvData.state()
        state shouldBe CsvState.List.DataAvailable
    }

    test("ListState -> Multiple translation data found -> duplicate keys") {
        val csvData: List<CsvData> =
            listOf(
                CsvData("", "", ""),
                CsvData("", "", ""),
            )
        val state = csvData.state()
        state shouldBe CsvState.List.MultipleData
    }

    test("CsvData State -> JSON value to translate exists in CSV but doesn't match") {
        val data: CsvData = CsvData("", "", "Original text to translate")

        val state = data.state("Original text to translate !")

        state shouldBe CsvState.State.CorruptedKey
    }

    test("CsvData State -> JSON value matches Original value but translation is missing") {
        val data: CsvData = CsvData("", "", null)

        val state = data.state("Original text to translate")

        state shouldBe CsvState.State.MissingTranslation
    }

    test("CsvData State -> JSON value is already translated") {
        val data: CsvData = CsvData("", "", "Texte original à traduire")

        val state = data.state("Texte original à traduire")

        state shouldBe CsvState.State.AlreadyTranslated
    }

    test("CsvData State -> All conditions allowed to translate JSON value") {
        val data: CsvData = CsvData("", "Original text to translate", "Texte original à traduire")

        val state = data.state("Original text to translate")

        state shouldBe CsvState.State.CanBeTranslate
    }
})