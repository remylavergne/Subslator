data class JSONLine(val jsonLine: String) {
    private val keyValueRegex: Regex = "\"(.+)\"[ ]*:[ ]*\"(.+)\"[,]?\$".toRegex()
    private var find: MatchResult? = null
    val key: String
        get() = this.key()
    val value: String
        get() = this.value()

    init {
        this.find = keyValueRegex.find(jsonLine)
    }

    fun containsKeyValue(): Boolean = find != null

    private fun key(): String {
        return if (this.find != null) {
            this.find!!.groupValues[1]
        } else {
            ""
        }
    }

    private fun value(): String {
        return if (this.find != null) {
            this.find!!.groupValues[2]
        } else {
            ""
        }
    }
}