import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val command = parseArguments(args)
    when {
        command.exit ->
            exitProcess(0)
        command.shell ->
            readFromShell(command)
        else ->
            readFromFile(command)
    }
}
