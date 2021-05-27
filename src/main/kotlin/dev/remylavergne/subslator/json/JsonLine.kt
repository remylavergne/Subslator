package dev.remylavergne.subslator.json

import dev.remylavergne.subslator.exception.InvalidDataException

data class JsonLine(val raw: String) {
    private val keyValueRegex: Regex = "\"(.+)\"[ ]*:[ ]*\"(.+)\"[,]?\$".toRegex()
    private var find: MatchResult? = keyValueRegex.find(raw)
    val key: String
        get() = this.key()
    val value: String
        get() = this.value()
    val keyEqValue = try {
        key == value
    } catch (e: InvalidDataException) {
        false
    }

    fun containsKeyValue(): Boolean = find != null

    private fun key(): String =
        this.find?.groupValues?.get(1) ?: throw InvalidDataException("JsonLine has an invalid key")

    private fun value(): String =
        this.find?.groupValues?.get(2) ?: throw InvalidDataException("JsonLine has an invalid value")
}