package dev.remylavergne.subslator.common

import com.github.ajalt.clikt.output.TermUi
import java.io.File
import kotlin.system.exitProcess

/**
 * [OutputData] handles output directory, and files contained.
 *
 * @param input Le fichier à modifier
 */
data class OutputData(
    private val input: File,
) {
    var directory: File = File("${input.parent}/output-${input.nameWithoutExtension}")
    var file: File = File("${directory.path}/translated-${input.name}")

    init {
        this.generateOutputDir()
    }

    fun delete() {
        this.directory.walkTopDown().forEach { file: File -> file.delete() }
    }

    private fun generateOutputDir() {
        val newOutputDir = this.directory.mkdirs()
        if (!newOutputDir) {
            promptToCleanupOutput(directory)
        }

    }

    private fun promptToCleanupOutput(outputDir: File) {
        val walk = outputDir.walkTopDown()
        val existingFiles = (walk.count() - 1) > 0
        if (existingFiles) {
            TermUi.echo("${walk.count() - 1} generated file(s) already exists.")
            val prompt =
                TermUi.prompt("To continue, these files will be deleted, are you agree? (y/n)", default = "n")

            if (prompt == "y" || prompt == "yes") {
                walk.forEach { file: File ->
                    if (file.name != outputDir.name) {
                        TermUi.echo("-> File \"${file.name}\" deleted...")
                        file.delete()
                    }
                }
                TermUi.echo("Output directory cleaned.\n")
            } else {
                TermUi.echo("Program stopped...")
                exitProcess(1)
            }
        }
    }
}

