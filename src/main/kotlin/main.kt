import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import command.CSV
import org.apache.commons.text.similarity.JaroWinklerSimilarity
import java.io.File
import kotlin.system.measureTimeMillis

val missingKeysLogs: File = File("output/missing-keys-logs.md")
val corruptedKeysLogs: File = File("output/corrupted-keys-logs.md")
val missingTranslationLogs: File = File("output/missing-translations.md")

// TODO: Si la ligne est déjà traduite -> pas erreur


class JsonTranslator : CliktCommand(help = "Translate quickly your JSON file", invokeWithoutSubcommand = true) {
    private val input by option("-i", "--input").file(mustExist = true, canBeDir = false)
        .help(help = "The input JSON file to translate path.")
    private val sourceData by option("-d", "--data").file(mustExist = true, canBeDir = false)
        .help(help = "The CSV file who contains translate informations.")
    private val outputDir by option("-o", "--out-dir").file(canBeDir = true)
        .help(help = "The CSV file who contains translate informations.").default(File("output"))

    override fun run() {
        input?.let { inputFile: File ->
            val outputFile = File("${outputDir.path}/translated-${inputFile.name}")
            createOutputDir(outputDir)
            val deserialized = CSV().deserialize(sourceData!!)

            val jsonLines: List<String> = inputFile.readLines()
            val jsonLinesProcessed: List<ProcessedLines> = translate(deserialized, jsonLines)
            require(jsonLines.size == jsonLinesProcessed.size)

            generateTranslatedFile(jsonLinesProcessed, outputFile)
            generateLogs(jsonLinesProcessed, outputFile)
        }
    }

    private fun createOutputDir(output: File) {
        val newlyCreated = output.mkdirs()

        if (!newlyCreated) {
            val walk = output.walkTopDown()
            TermUi.echo("Generated files: ${walk.count() - 1}")
            walk.forEach { file: File ->
                if (file.name != output.name) {
                    TermUi.echo("File \"${file.name}\" deleted.")
                    file.delete()
                }
            }
            TermUi.echo("Output directorie(s) cleaned.\n")
        } else {
            TermUi.echo("Output directorie(s) created.\n")
        }
    }

    private fun translate(csvData: List<CsvData>, jsonLines: List<String>): List<ProcessedLines> {
        val processedLines: MutableList<ProcessedLines> = mutableListOf()

        val time = measureTimeMillis {
            jsonLines.forEach forEach@{ currentLine: String ->
                // val matchValues: MatchResult? = keyValueRegex.find(currentLine)
                val jsonLine = JSONLine(currentLine)

                if (!jsonLine.containsKeyValue()) {
                    processedLines.add(ProcessedLines(currentLine, Log.Done))
                    return@forEach
                }

                val currentLineData = csvData.filter { it.libelleId == jsonLine.key() }

                when {
                    currentLineData.isEmpty() -> {
                        processedLines.add(ProcessedLines(currentLine, Log.MissingKey))
                    }
                    currentLineData.size == 1 -> when {
                        currentLineData.first().originalText != jsonLine.value() -> {
                            processedLines.add(ProcessedLines(currentLine, Log.CorruptedKey))

                        }
                        currentLineData.first().translatedText == null -> {
                            processedLines.add(ProcessedLines(currentLine, Log.MissingKey))
                        }
                        else -> {
                            val currentLineTranslated = currentLine.replace(
                                jsonLine.value(),
                                currentLineData.first().translatedText!!
                            )
                            processedLines.add(ProcessedLines(currentLineTranslated, Log.Done))
                        }
                    }
                    currentLineData.size > 1 -> {
                        val excelData: CsvData? = currentLineData.find { it.originalText == jsonLine.value() }
                        if (excelData?.translatedText != null) {
                            val currentLineTranslated = currentLine.replace(
                                jsonLine.value(),
                                excelData.translatedText
                            )
                            processedLines.add(ProcessedLines(currentLineTranslated, Log.Done))
                        } else {
                            var highestPercentage = 0.0
                            var bestMatch: CsvData? = null
                            currentLineData.forEach { csvData: CsvData ->
                                val compare = JaroWinklerSimilarity().apply(jsonLine.value(), csvData.originalText)
                                if (compare > highestPercentage) {
                                    highestPercentage = compare
                                    bestMatch = csvData
                                }
                            }

                            if (bestMatch != null) {
                                processedLines.add(
                                    ProcessedLines(
                                        currentLine,
                                        Log.PossibleMatching(bestMatch!!, highestPercentage.toString())
                                    )
                                )
                            } else {
                                processedLines.add(ProcessedLines(currentLine, Log.MissingValue))
                            }
                        }
                    }
                    else -> processedLines.add(ProcessedLines(currentLine, Log.Done))
                }
            }
        }

        TermUi.echo(
            """  
                ----------------
                Translation Summary
                ----------------
                o ${csvData.size} translations available
                o ${0} lines processed
                o 0 lines translated automatically
                o 0 inconsistent lines (~${0} %)
                o Operation took $time ms
                
                
            """.trimIndent()
        )

        return processedLines
    }

    private fun generateTranslatedFile(jsonLinesTranslated: List<ProcessedLines>, outputFile: File) {
        jsonLinesTranslated.forEach { pl: ProcessedLines ->
            outputFile.appendText(pl.line + "\n")
        }
    }


    private fun generateLogs(jsonLinesProcessed: List<ProcessedLines>, outputFile: File) {
        // TODO: Trying to get application path

        jsonLinesProcessed.forEachIndexed { index: Int, data: ProcessedLines ->
            when (data.log) {
                Log.CorruptedKey -> {
                    // TODO: Better analyse
                    corruptedKeysLogs.appendText(
                        """
                            [Original text seems different for line ${index + 1}](vscode://file/${outputFile.path}:${index + 1})

                            ```json
                            ${data.line.trim()}
                            ```

                        """.trimIndent()
                    )
                }
                Log.Done -> {

                }
                Log.MissingKey -> {
                    missingKeysLogs.appendText(
                        """
                            [Key missing for line ${index + 1}](vscode://file/${outputFile.path}:${index + 1})
                 
                            ```json
                            ${data.line.trim()}
                             ```
                 
                        """.trimIndent()
                    )
                }
                Log.MissingValue -> {
                    missingTranslationLogs.appendText(
                        """
                        [Value missing for line ${index + 1}](vscode://file/${outputFile.path}:${index + 1})
                        
                        ```json
                        ${data.line.trim()}
                        ```
                    """.trimIndent()
                    )
                }
                is Log.PossibleMatching -> {
                    corruptedKeysLogs.appendText(
                        """
                            [Best translation match for line ${index + 1}](vscode://file/${outputFile.path}:${index + 1})

                            ```json
                            ${data.line.trim()}
                            
                            // Match: ${data.log.matchPercentage}
                            // Key: ${data.log.data.libelleId}
                            // Original text: ${data.log.data.originalText}
                            // Translation: ${data.log.data.translatedText}
                            ```

                        """.trimIndent()
                    )
                }
            }
        }
    }
}

fun main(args: Array<String>) = JsonTranslator().main(args)

// fun getErrorPercentage(): Int = (failed * 100) / (succeed + failed)

/*
fun printResult() {
    println(
        """
    ----------------------------------------------------------------------
                                   REPORT
    ----------------------------------------------------------------------

    o ${excelData.size} translations available
    o ${succeed + failed} lines processed
    o $succeed lines translated automatically
    o $failed inconsistent lines (~${getErrorPercentage()} %)

    ----------------------------------------------------------------------

""".trimIndent()
    )
}

 */
