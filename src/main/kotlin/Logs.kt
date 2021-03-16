sealed class Log {
    object Done : Log()
    object AlreadyTranslated : Log()
    object MissingKey : Log()
    object MissingValue : Log()
    data class CorruptedKey(val data: CsvData, val matchPercentage: Double) : Log()
    data class PossibleMatching(val data: CsvData, val matchPercentage: Double) : Log()
}
