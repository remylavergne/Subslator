package dev.remylavergne.subslator.domain

sealed class CsvState {
    sealed class List : CsvState() {
        object NoData : List()
        object DataAvailable : List()
        object MultipleData : List()
    }

    sealed class State : CsvState() {
        object CorruptedKey : State()
        object MissingTranslation : State()
        object AlreadyTranslated : State()
        object CanBeTranslate : State()
    }
}