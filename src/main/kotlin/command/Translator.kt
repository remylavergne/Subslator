package command

import com.github.ajalt.clikt.core.NoOpCliktCommand

class Translator : NoOpCliktCommand(
    help = "An easy CLI to replace sentences in your files.",
    invokeWithoutSubcommand = true,
    printHelpOnEmptyArgs = true
) {

}