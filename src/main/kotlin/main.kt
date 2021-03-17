import com.github.ajalt.clikt.core.subcommands
import command.JsonTranslator
import command.Zip


fun main(args: Array<String>) =
    JsonTranslator().subcommands(Zip()).main(args) // TODO: Extract JsonTranslator as a Subcommand
