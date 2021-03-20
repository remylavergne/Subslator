package dev.remylavergne.subslator

import com.github.ajalt.clikt.core.NoOpCliktCommand

class Subslator : NoOpCliktCommand(
    help = "An easy CLI to replace sentences in your files.",
    invokeWithoutSubcommand = true,
    printHelpOnEmptyArgs = true
)