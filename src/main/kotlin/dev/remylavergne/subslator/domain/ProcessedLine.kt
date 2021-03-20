package dev.remylavergne.subslator.domain

import dev.remylavergne.subslator.common.OutputData

data class ProcessedLine(
    val line: String,
    val log: Log
)

fun List<ProcessedLine>.toFile(outputData: OutputData): List<ProcessedLine> {
    this.forEach { pl: ProcessedLine ->
        outputData.file.appendText(pl.line + "\n")
    }
    return this
}