package dev.remylavergne.subslator

import com.github.ajalt.clikt.core.subcommands
import dev.remylavergne.subslator.command.Json
import dev.remylavergne.subslator.command.Zip


fun main(args: Array<String>) =
    Subslator().subcommands(Json(), Zip()).main(args)
