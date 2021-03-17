package command

import CsvData
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.github.ajalt.clikt.output.TermUi.echo
import java.io.File
import java.io.FileReader
import kotlin.system.measureTimeMillis

class CSVUtils {

    companion object {

        fun deserialize(file: File): List<CsvData> {
            var readCsvFile: List<CsvData>
            val time = measureTimeMillis {
                readCsvFile = readCsvFile<CsvData>(file.path)
            }
            echo(
                """
                ---------------------
                Deserialized CSV Data
                ---------------------
                o ${readCsvFile.size} elements extracted
                o Operation took $time ms
                
            """.trimIndent()
            )
            return readCsvFile
        }

        private inline fun <reified T> readCsvFile(fileName: String): List<T> {
            FileReader(fileName).use { reader ->
                return CsvMapper()
                    .readerFor(T::class.java)
                    .with(CsvSchema.emptySchema().withHeader().withColumnSeparator(';'))
                    .readValues<T>(reader)
                    .readAll()
                    .toList()
            }
        }
    }
}
