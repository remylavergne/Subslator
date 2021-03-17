package json

import ProcessedLine
import com.github.ajalt.clikt.output.TermUi.echo
import domain.Log
import java.io.File

data class JsonLogger(
    private val jsonLinesProcessed: List<ProcessedLine>,
    private val outputDir: File,
    private val outputFile: File,
) {
    private val missingKeysLogs = File("${outputDir.path}/missing-keys-logs.md")
    private val corruptedKeysLogs = File("${outputDir.path}/corrupted-keys-logs.md")
    private val missingTranslationLogs = File("${outputDir.path}/missing-translations.md")

    fun generateLogFiles(): JsonLogger {
        jsonLinesProcessed.forEachIndexed { index: Int, data: ProcessedLine ->
            when (data.log) {
                is Log.CorruptedKey -> {
                    corruptedKeysLogs.appendText(
                        """
                            [Original text seems different for line ${index + 1}](vscode://file/${outputFile.path}:${index + 1})

                            ```json
                            ${data.line.trim()}
                            
                            // Match: ${data.log.matchPercentage * 100}
                            // Key: ${data.log.data.libelleId}
                            // Original: ${data.log.data.originalText}
                            // Translation: ${data.log.data.translatedText}
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
                            
                            // Match: ${data.log.matchPercentage * 100}
                            // Key: ${data.log.data.libelleId}
                            // Original: ${data.log.data.originalText}
                            // Translation: ${data.log.data.translatedText}
                            ```

                        """.trimIndent()
                    )
                }
                Log.AlreadyTranslated -> {
                }
            }
        }

        return this
    }

    fun generateFinalReport(): JsonLogger {
        echo(
            """
    ----------------
    Final Statistics
    ----------------

    o ${jsonLinesProcessed.size} lines processed
    o ${jsonLinesProcessed.filter { it.log == Log.Done || it.log == Log.AlreadyTranslated }.size} lines translated automatically
    o ${jsonLinesProcessed.filter { it.log == Log.MissingValue }.size} translation(s) missing
    o ${jsonLinesProcessed.filter { it.log == Log.MissingKey }.size} key(s) missing
    o ${jsonLinesProcessed.filter { it.log is Log.CorruptedKey }.size} original texts corrupted. Manual edition required.

""".trimIndent()
        )

        return this
    }
}