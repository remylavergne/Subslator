import java.io.File

data class JsonLogger(
    private val jsonLinesProcessed: List<ProcessedLine>,
    private val outputDir: File,
    private val outputFile: File,
) {

    val missingKeysLogs = File("${outputDir.path}/missing-keys-logs.md")
    val corruptedKeysLogs = File("${outputDir.path}/corrupted-keys-logs.md")
    val missingTranslationLogs = File("${outputDir.path}/missing-translations.md")

    fun generateLogFiles(): JsonLogger {
        jsonLinesProcessed.forEachIndexed { index: Int, data: ProcessedLine ->
            when (data.log) {
                is Log.CorruptedKey -> {
                    corruptedKeysLogs.appendText(
                        """
                            [Original text seems different for line ${index + 1}](vscode://file/${outputFile.path}:${index + 1})

                            ```json
                            ${data.line.trim()}
                            
                            // Match:${data.log.matchPercentage * 100}
                            // Key:${data.log.data.libelleId}
                            // Original text:${data.log.data.originalText}
                            // Translation:${data.log.data.translatedText}
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
                            // Original text: ${data.log.data.originalText}
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
        // TODO

        return this
    }


}