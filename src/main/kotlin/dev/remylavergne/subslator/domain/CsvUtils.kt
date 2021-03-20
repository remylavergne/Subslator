package dev.remylavergne.subslator.domain

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.File
import java.io.FileReader

object CsvUtils {

    fun deserialize(file: File): List<CsvData> = readCsvFile(file.path)


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
