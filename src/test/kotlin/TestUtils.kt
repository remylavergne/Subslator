import java.io.File

object TestUtils {

    fun createCsv(name: String = "data.csv"): File {
        // Create file locally
        val file = File(name)
        // Data
        val csvRawData = """
            ID-libelle;Texte;Libellé à traduire;Traduction
            JSON_KEY;Text into JSON;Text into JSON;Le texte présent dans le JSON 
        """.trimIndent()
        // Write in file
        file.appendText(csvRawData)

        return file
    }

    fun createInputJson(name: String = "input.json"): File {
        // Create file locally
        val file = File(name)
        // Data
        val csvRawData = """
            {
                "FIRST_KEY": "FIRST VALUE",
                "SECOND_KEY": "SECOND VALUE",
                "NESTED": {
                   "FIRST_NESTED_KEY": "FIRST NESTED VALUE"
                }
             }
        """.trimIndent()
        // Write in file
        file.appendText(csvRawData)

        return file
    }

    fun createOutputDir(path: String = "output"): File {
        return File(path)
    }

    fun createOutputFile(name: String = "translated-file.json"): File {
        return File(name)
    }
}