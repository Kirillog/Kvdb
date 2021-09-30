import java.io.IOException
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val utility = parseArguments(args)
    if (utility.exit)
        exitProcess(0)
    val shell = Shell(utility)
    while (!shell.exit) {
        try {
            val command = shell.readCommand()
            shell.run(command)
        } catch (err: IOException) {
            shell.printError(err.message)
        }
    }
}
