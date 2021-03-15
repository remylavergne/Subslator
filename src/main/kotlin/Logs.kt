sealed class Log {
    object Done : Log()
    object MissingKey : Log()
    object MissingValue : Log()
    object CorruptedKey : Log()
    data class PossibleMatching(val data: CsvData, val matchPercentage: String) : Log()
}
