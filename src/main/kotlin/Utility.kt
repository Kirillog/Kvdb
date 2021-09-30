import java.io.File
import java.io.IOException

data class Utility(
    var dataBaseFileName: String = "junk.dbm",
    var color: Boolean = false,
    var shell: Boolean = true,
    var exit: Boolean = false,
    var commandFileName: String = ""
)

fun parseArguments(args: Array<String>): Utility {
    val command = Utility()
    try {
        args.forEach { argument ->
            when {
                argument.startsWith("-file=") -> {
                    command.shell = false
                    command.commandFileName = argument.drop(6)
                }
                argument == "-color" ->
                    command.color = true
                !argument.startsWith("-") ->
                    command.dataBaseFileName = argument
                else ->
                    throw IOException("Invalid option -- $argument")
            }
        }
        when {
            !command.shell && !File(command.commandFileName).isFile ->
                throw IOException("No such file or directory: '${command.commandFileName}'")
            command.dataBaseFileName == "junk.dbm" ->
                File(command.dataBaseFileName).createNewFile()
            !File(command.dataBaseFileName).isFile ->
                throw IOException("No such file or directory: '${command.dataBaseFileName}'")
        }
    } catch (error: IOException) {
        System.err.println(error.message)
        command.exit = true
    }
    return command
}