package dev.remylavergne.subslator.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import dev.remylavergne.subslator.common.OutputData
import dev.remylavergne.subslator.domain.*
import dev.remylavergne.subslator.domain.ext.state
import dev.remylavergne.subslator.json.JsonLine
import dev.remylavergne.subslator.json.JsonLogger
import org.apache.commons.text.similarity.JaroWinklerSimilarity

class Json : CliktCommand(
    help = "Translate / Replace quickly your JSON values.",
    invokeWithoutSubcommand = true,
    printHelpOnEmptyArgs = true
) {
    private val input by option("-i", "--input").file(mustExist = true, canBeDir = false)
        .help(help = "The input JSON file to translate")
    private val sourceData by option("-d", "--data").file(mustExist = true, canBeDir = false)
        .help(help = "The CSV file which contains translate values")

    override fun run() {
        val outputData = OutputData(input!!)
        // Export + Process data
        val deserialized = CsvUtils.deserialize(sourceData!!)
        val jsonLines: List<String> = input!!.readLines()
        val jsonLinesProcessed: List<ProcessedLine> = translate(deserialized, jsonLines).toFile(outputData)
        // Logs
        JsonLogger(jsonLinesProcessed, outputData)
            .generateLogFiles()
            .generateFinalReport()
    }

    private fun translate(csvData: List<CsvData>, jsonLines: List<String>): List<ProcessedLine> {
        val processedLines: MutableList<ProcessedLine> = mutableListOf()

        jsonLines.forEach forEach@{ currentLine: String ->
            val jsonLine = JsonLine(currentLine)
            if (!jsonLine.containsKeyValue()) {
                processedLines.add(ProcessedLine(currentLine, Log.Done))
                return@forEach
            }

            val currentLineData = csvData.filter { it.jsonKey?.trim() == jsonLine.key.trim() }

            val processedLine: ProcessedLine = when (currentLineData.state()) {
                CsvState.List.NoData -> ProcessedLine(currentLine, Log.MissingKey)
                CsvState.List.DataAvailable -> when (currentLineData.first().state(jsonLine.value)) {
                    CsvState.State.CorruptedKey -> findClosestTranslation(
                        currentLineData.first(),
                        currentLine,
                        jsonLine.value
                    )
                    CsvState.State.MissingTranslation -> ProcessedLine(currentLine, Log.MissingKey)
                    CsvState.State.AlreadyTranslated -> ProcessedLine(currentLine, Log.AlreadyTranslated)
                    CsvState.State.CanBeTranslate -> translateCurrentLine(
                        jsonLine,
                        currentLineData.first().translatedText!!
                    )
                }
                CsvState.List.MultipleData -> translateOrFindClosestTranslation(
                    currentLineData,
                    currentLine,
                    jsonLine.value
                )
            }
            processedLines.add(processedLine)
        }

        require(jsonLines.size == processedLines.size)
        return processedLines
    }

    private fun findClosestTranslation(csvData: CsvData, line: String, valueToTranslate: String): ProcessedLine {
        val matchPercentage =
            JaroWinklerSimilarity().apply(csvData.originalText, valueToTranslate)

        return ProcessedLine(line, Log.CorruptedKey(csvData, matchPercentage))
    }

    private fun translateCurrentLine(jsonLine: JsonLine, valueTranslated: String): ProcessedLine {

        val currentLineTranslated = if (jsonLine.keyEqValue) {
            val keyAndValue = jsonLine.raw.split(":")
            "${keyAndValue[0]}:${keyAndValue[1].replace(jsonLine.value, valueTranslated)}"
        } else {
            jsonLine.raw.replace(jsonLine.value, valueTranslated)
        }

        return ProcessedLine(currentLineTranslated, Log.Done)
    }

    private fun translateOrFindClosestTranslation(
        csvData: List<CsvData>,
        line: String,
        valueToTranslate: String,
    ): ProcessedLine {
        val excelData: CsvData? = csvData.find { it.originalText == valueToTranslate }
        return if (excelData?.translatedText != null) {
            val currentLineTranslated = line.replace(
                valueToTranslate,
                excelData.translatedText
            )
            ProcessedLine(currentLineTranslated, Log.Done)
        } else {
            var highestPercentage = 0.0
            var bestMatch: CsvData? = null
            csvData.forEach { currentCsvData: CsvData ->
                val compare = JaroWinklerSimilarity().apply(valueToTranslate, currentCsvData.originalText)
                if (compare > highestPercentage) {
                    highestPercentage = compare
                    bestMatch = currentCsvData
                }
            }

            return if (bestMatch != null) {
                ProcessedLine(line, Log.PossibleMatching(bestMatch!!, highestPercentage))
            } else {
                ProcessedLine(line, Log.MissingValue)
            }
        }
    }
}