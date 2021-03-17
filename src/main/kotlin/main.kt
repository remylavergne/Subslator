import com.github.ajalt.clikt.core.subcommands
import command.Json
import command.Translator
import command.Zip


fun main(args: Array<String>) =
    Translator().subcommands(Json(), Zip()).main(args) // TODO: Extract JsonTranslator as a Subcommand
