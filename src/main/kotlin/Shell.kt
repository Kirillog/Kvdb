import java.io.File
import java.io.IOException
import java.util.*
import kotlin.text.StringBuilder

/**
 * describes interaction between user and program,
 * @property utility the program that shell wraps
 * @property dataBase the current dataBase which user are working
 * @property open reflects whether open [dataBase] or not
 * @property exit true when user write quit command
 */

class Shell(val utility: Utility) {

    val defaultFile = File(standardFileName)
    var dataBase = DataBase(defaultFile)
    var exit = false
    var open = false

    init {
        if (utility.writeToShell)
        // merge err and out stream to avoid interrupts in output
            System.setErr(System.out)
        if (!utility.color || !utility.writeToShell)
        // set print uncolored
            Color.values().forEach { it.code = "" }
        try {
            if (utility.dataBaseFileName != defaultFile.name)
                open(utility.dataBaseFileName)
        } catch (err: DataBaseException) {
            printError(err.message)
        }

    }

    /**
     * adding [argument] to [arguments] if [argument] not empty
     */

    private fun addToArgs(argument: StringBuilder, arguments: MutableList<String>) {
        if (argument.isNotEmpty())
            arguments.add(argument.toString())
        argument.clear()
    }

    /**
     * split [line] of arguments to list of these arguments,
     * where each argument can be escaped by quotes
     */

    fun split(line: String): List<String> {
        val argument = StringBuilder()
        val arguments = mutableListOf<String>()
        var openQuote = false
        line.forEach { char ->
            when {
                openQuote && char == '"' -> {
                    if (argument.isEmpty())
                        throw IOException("Cannot interpreter the empty strings")
                    addToArgs(argument, arguments)
                    openQuote = false
                }
                char == '"' ->
                    openQuote = true
                char == ' ' && !openQuote ->
                    addToArgs(argument, arguments)
                else ->
                    argument.append(char)
            }
        }
        addToArgs(argument, arguments)
        return arguments
    }

    /**
     * reads command from standard input and returns it
     */

    fun readCommand(): Command {
        var line: String?
        do {
            printName()
            line = readLine()?.trim()
        } while (line == "")
        // split "operation args" to listOf("operation", "args") if line isn't null
        val lineArguments = line?.split(" ", limit = 2) ?: return Command(Operation.QUIT, listOf())
        val stringOperation = lineArguments[0]
        // if command has at least one argument, split "args" to args
        val arguments = if (lineArguments.size == 2)
            split(lineArguments[1])
        else
            listOf()
        // check if operation is available and has correct number of arguments
        try {
            val operation = Operation.valueOf(stringOperation.uppercase())
            if (arguments.size != operation.args)
                throw IOException("Incorrect number of arguments for $stringOperation")
            return Command(operation, arguments)
        } catch (exc: IllegalArgumentException) {
            throw IOException("Unknown command -- '$stringOperation'")
        }
    }

    /**
     * try to run [command] on database
     * if error occurred catch DataBaseException and prints message
     */
    fun run(command: Command) {
        try {
            when (command.operation) {
                Operation.OPEN ->
                    open(command.arguments.first())
                Operation.CLOSE ->
                    close()
                Operation.REMOVE ->
                    remove(command.arguments.first())
                Operation.STATUS ->
                    printList(status())
                Operation.STORE ->
                    dataBase.store(command.arguments[0], command.arguments[1])
                Operation.DELETE ->
                    dataBase.delete(command.arguments.first())
                Operation.FETCH ->
                    println(dataBase.fetch(command.arguments.first()))
                Operation.LIST ->
                    printList(dataBase.list())
                Operation.CONTAINS ->
                    println(dataBase.contains(command.arguments.first()).toString())
                Operation.QUIT ->
                    quit()
            }
        } catch (err: DataBaseException) {
            printError(err.message)
        }
    }

    private fun quit() {
        exit = true
        dataBase.close()
    }

    /**
     * closes opened dataBase and
     * opens dataBase in [dataBaseName] file
     */

    private fun open(dataBaseName: String) {
        if (open)
            close()
        dataBase = DataBase(File(dataBaseName))
        dataBase.open()
        open = true
    }

    private fun close() {
        dataBase.close()
        dataBase = DataBase(defaultFile)
        open = false
    }

    private fun remove(fileName: String) {
        if (fileName == dataBase.file.name) {
            open = false
            dataBase = DataBase(defaultFile)
        }
        if (File(fileName).isFile) {
            File(fileName).delete()
        } else
            throw IOException("There is no database with name '$fileName'")
    }

    /**
     * returns status of database:
     * name of file and opened file or not
     */

    private fun status(): List<String> {
        val state = if (open)
            ""
        else
            " not"
        return listOf("Database file: ${Color.PURPLE}${dataBase.file.name}${Color.RESET}", "Database is$state open")
    }

    private fun printName() {
        if (utility.readFromShell)
            print("${Color.BLUE}dbm> ${Color.RESET}")
    }

    private fun printList(list: List<String>) {
        list.forEach { println(it) }
    }

    fun println(message: String) {
        kotlin.io.println("${Color.GREEN}$message${Color.RESET}")
    }

    fun printError(message: String?) {
        if (message != null)
            System.err.println("${Color.RED}$message${Color.RESET}")
    }

    fun clear() {
        File(utility.dataBaseFileName).delete()
    }
}

/**
 * stores [operation] to dataBase or Shell with [arguments]
 */
data class Command(val operation: Operation, val arguments: List<String>)

enum class Operation(val args: Int) {
    CLOSE(0), STATUS(0), QUIT(0), OPEN(1), STORE(2), DELETE(1), FETCH(1), LIST(0), CONTAINS(1), REMOVE(1);
}

enum class Color(var code: String) {
    RESET("\u001B[0m"), RED("\u001B[31m"), GREEN("\u001B[32m"), BLUE("\u001B[34m"), PURPLE("\u001B[35m");

    override fun toString(): String {
        return this.code
    }
}
