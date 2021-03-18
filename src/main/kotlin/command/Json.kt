package command

import ProcessedLine
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import domain.CsvData
import domain.CsvState
import domain.CsvUtils
import domain.Log
import domain.ext.state
import json.JsonLine
import json.JsonLogger
import org.apache.commons.text.similarity.JaroWinklerSimilarity
import java.io.File
import kotlin.system.exitProcess

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
        val outputDir = File("${input!!.parent}/output-${input!!.nameWithoutExtension}")
        val outputFile = File("${outputDir.path}/translated-${input!!.name}")

        val newOutputDir = createOutputDir(outputDir)
        if (!newOutputDir) {
            promptToCleanupOutput(outputDir)
        }
        // Export + Process data
        val deserialized = CsvUtils.deserialize(sourceData!!)
        val jsonLines: List<String> = input!!.readLines()
        val jsonLinesProcessed: List<ProcessedLine> = translate(deserialized, jsonLines)
        // Logs
        generateTranslatedFile(jsonLinesProcessed, outputFile)
        JsonLogger(jsonLinesProcessed, outputDir, outputFile)
            .generateLogFiles()
            .generateFinalReport()
    }

    private fun createOutputDir(output: File): Boolean {
        return output.mkdirs()
    }

    private fun promptToCleanupOutput(outputDir: File) {
        val walk = outputDir.walkTopDown()
        val existingFiles = (walk.count() - 1) > 0
        if (existingFiles) {
            TermUi.echo("${walk.count() - 1} generated file(s) already exists.")
            val prompt =
                TermUi.prompt("To continue, these files will be overwrite, are you agree? (yes/no)", default = "no")

            if (prompt == "yes") {
                walk.forEach { file: File ->
                    if (file.name != outputDir.name) {
                        TermUi.echo("-> File \"${file.name}\" deleted...")
                        file.delete()
                    }
                }
                TermUi.echo("Output directory cleaned.\n")
            } else {
                exitProcess(1)
            }
        }
    }

    private fun translate(csvData: List<CsvData>, jsonLines: List<String>): List<ProcessedLine> {
        val processedLines: MutableList<ProcessedLine> = mutableListOf()

        jsonLines.forEach forEach@{ currentLine: String ->
            val jsonLine = JsonLine(currentLine)
            if (!jsonLine.containsKeyValue()) {
                processedLines.add(ProcessedLine(currentLine, Log.Done))
                return@forEach
            }

            val currentLineData = csvData.filter { it.libelleId?.trim() == jsonLine.key.trim() }

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
                        currentLine,
                        jsonLine.value,
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

    private fun generateTranslatedFile(jsonLinesTranslated: List<ProcessedLine>, outputFile: File) {
        jsonLinesTranslated.forEach { pl: ProcessedLine ->
            outputFile.appendText(pl.line + "\n")
        }
    }

    private fun findClosestTranslation(csvData: CsvData, line: String, valueToTranslate: String): ProcessedLine {
        val matchPercentage =
            JaroWinklerSimilarity().apply(csvData.originalText, valueToTranslate)

        return ProcessedLine(line, Log.CorruptedKey(csvData, matchPercentage))
    }

    private fun translateCurrentLine(line: String, valueToTranslate: String, valueTranslated: String): ProcessedLine {
        val currentLineTranslated = line.replace(valueToTranslate, valueTranslated)
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