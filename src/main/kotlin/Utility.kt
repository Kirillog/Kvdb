import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.PrintStream

const val standardFileName = "junk.dbm"

data class Utility(
    var dataBaseFileName: String = standardFileName,
    var color: Boolean = false,
    var readFromShell: Boolean = true,
    var writeToShell: Boolean = true,
    var exit: Boolean = false,
)

/**
 * parse program arguments from [args]
 * @return data class Utility that stores options and database file name
 */

fun parseArguments(args: Array<String>): Utility {
    val utility = Utility()
    var inputFileName = ""
    var outputFileName = ""
    try {
        args.forEach { argument ->
            when {
                argument == "-fileIn" ->
                    utility.readFromShell = false
                !utility.readFromShell && inputFileName == "" ->
                    inputFileName = argument
                argument == "-fileOut" ->
                    utility.writeToShell = false
                !utility.writeToShell && outputFileName == "" ->
                    outputFileName = argument
                argument == "-color" ->
                    utility.color = true
                !argument.startsWith("-") ->
                    utility.dataBaseFileName = argument
                else ->
                    throw IOException("Invalid option -- $argument")
            }
        }
        if (!utility.readFromShell && !File(inputFileName).isFile)
            throw IOException("No such file or directory: '$inputFileName'")
        if (!utility.readFromShell)
            System.setIn(FileInputStream(File(inputFileName)))
        if (!utility.writeToShell)
            System.setOut(PrintStream(File(outputFileName)))
    } catch (error: IOException) {
        System.err.println(error.message)
        utility.exit = true
    }
    return utility
}