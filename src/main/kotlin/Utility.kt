import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.PrintStream

data class Utility(
    var dataBaseFileName: String = "junk.dbm",
    var color: Boolean = false,
    var readFromShell: Boolean = true,
    var writeToShell: Boolean = true,
    var exit: Boolean = false,
)

fun parseArguments(args: Array<String>): Utility {
    val command = Utility()
    var inputFileName = ""
    var outputFileName = ""
    try {
        args.forEach { argument ->
            when {
                argument == "-fileIn" ->
                    command.readFromShell = false
                !command.readFromShell && inputFileName == "" ->
                    inputFileName = argument
                argument == "-fileOut" ->
                    command.writeToShell = false
                !command.writeToShell && outputFileName == "" ->
                    outputFileName = argument
                argument == "-color" ->
                    command.color = true
                !argument.startsWith("-") ->
                    command.dataBaseFileName = argument
                else ->
                    throw IOException("Invalid option -- $argument")
            }
        }
        if (!command.readFromShell && !File(inputFileName).isFile)
            throw IOException("No such file or directory: '$inputFileName'")
        if (!command.readFromShell)
            System.setIn(FileInputStream(File(inputFileName)))
        if (!command.writeToShell)
            System.setOut(PrintStream(File(outputFileName)))
    } catch (error: IOException) {
        System.err.println(error.message)
        command.exit = true
    }
    return command
}