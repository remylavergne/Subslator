package domain.ext

import domain.CsvData
import domain.CsvState


fun List<CsvData>.state(): CsvState.List = when {
    this.isEmpty() -> CsvState.List.NoData
    this.size == 1 -> CsvState.List.DataAvailable
    this.size > 1 -> CsvState.List.MultipleData
    else -> throw Exception("Impossible case")
}

fun CsvData.state(toTranslate: String): CsvState.State = when {
    this.translatedText == null -> domain.CsvState.State.MissingTranslation
    this.translatedText == toTranslate -> domain.CsvState.State.AlreadyTranslated
    this.originalText != toTranslate -> domain.CsvState.State.CorruptedKey
    else -> domain.CsvState.State.CanBeTranslate
}
