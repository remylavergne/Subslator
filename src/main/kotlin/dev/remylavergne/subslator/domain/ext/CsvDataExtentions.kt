package dev.remylavergne.subslator.domain.ext

import dev.remylavergne.subslator.domain.CsvData
import dev.remylavergne.subslator.domain.CsvState


fun List<CsvData>.state(): CsvState.List = when {
    this.isEmpty() -> CsvState.List.NoData
    this.size == 1 -> CsvState.List.DataAvailable
    this.size > 1 -> CsvState.List.MultipleData
    else -> throw Exception("Impossible case")
}

fun CsvData.state(toTranslate: String): CsvState.State = when {
    this.translatedText == null -> CsvState.State.MissingTranslation
    this.translatedText == toTranslate -> CsvState.State.AlreadyTranslated
    this.originalText != toTranslate -> CsvState.State.CorruptedKey
    else -> CsvState.State.CanBeTranslate
}
