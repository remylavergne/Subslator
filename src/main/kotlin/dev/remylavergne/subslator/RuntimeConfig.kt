package dev.remylavergne.subslator

import com.github.ajalt.clikt.output.TermUi
import java.io.BufferedReader

object RuntimeConfig {
    val path: String = ""
        get() {
            return if (field.isEmpty()) {
                try {
                    val process = ProcessBuilder().command("pwd")
                    val inputStream = process.start().inputStream
                    inputStream.bufferedReader().use(BufferedReader::readText).trim()
                } catch (e: Exception) {
                    TermUi.echo("Error to retrieve your application path")
                    ""
                }
            } else {
                field
            }
        }
}