data class JSONLine(val jsonLine: String) {
    private val keyValueRegex: Regex = "\"(.+)\"[ ]*:[ ]*\"(.+)\"[,]?\$".toRegex()
    private var find: MatchResult? = null

    init {
        this.find = keyValueRegex.find(jsonLine)
    }

    fun containsKeyValue(): Boolean = find != null

    fun key(): String {
        return if (this.find != null) {
            this.find!!.groupValues[1]
        } else {
            ""
        }
    }

    fun value(): String {
        return if (this.find != null) {
            this.find!!.groupValues[2]
        } else {
            ""
        }
    }
}