import com.github.ajalt.clikt.output.TermUi
import java.io.File

object RuntimeConfig {
    private val applicationPath: File = File("config/app-path.txt")
    private val path: String
        get() {
            return if (applicationPath.exists()) {
                val readLines = applicationPath.readLines()
                readLines.first()
            } else {
                ""
            }
        }

    init {
        val result = File("config").mkdir()

        println()
    }

    fun getCurrentPath(): String {
        val fileExists = this.applicationPath.exists()
        if (!fileExists) {
            try {
                ProcessBuilder().command("pwd")
                    .redirectOutput(applicationPath)
                    .start()
                    .waitFor()
            } catch (e: Exception) {
                TermUi.echo("Error to retrieve your application path")
            }
        }

        return path
    }
}