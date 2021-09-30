import java.io.File
import java.io.IOException
import java.util.*

class Shell(val utility: Utility) {
    data class Command(val operation: Operation, val arguments: List<String>)

    enum class Operation(val args: Int) {
        CLOSE(0), STATUS(0), QUIT(0), OPEN(1), STORE(2), DELETE(1), FETCH(1), LIST(0), CONTAINS(1);

        override fun toString(): String {
            return this.name.lowercase(Locale.getDefault())
        }
    }

    enum class Color(var code: String) {
        RESET("\u001B[0m"), RED("\u001B[31m"), GREEN("\u001B[32m"), BLUE("\u001B[34m"), PURPLE("\u001B[35m");

        override fun toString(): String {
            return this.code
        }
    }

    var dataBase = DataBase(File(utility.dataBaseFileName))
    var exit = false
    var open = false

    init {
        if (utility.writeToShell)
            System.setErr(System.out)
        if (!utility.color)
            Color.values().forEach { it.code = "" }
    }

    fun readCommand(): Command {
        var line: String?
        do {
            printName()
            line = readLine()?.trim()
        } while (line == "")

        var arguments = line?.split(" ") ?: return Command(Operation.QUIT, listOf())
        val stringOperation = arguments[0]
        arguments = arguments.subList(1, arguments.size)

        Operation.values().forEach { operation ->
            if (operation.toString() == stringOperation) {
                if (operation.args == arguments.size)
                    return Command(operation, arguments)
                else
                    throw IOException("Incorrect number of arguments for $stringOperation")
            }
        }
        throw IOException("Unknown command -- '$stringOperation'")
    }

    fun run(command: Command) {
        try {
            when (command.operation) {
                Operation.OPEN ->
                    open(command.arguments.first())
                Operation.CLOSE ->
                    close()
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

    private fun open(dataBaseName: String) {
        if (open)
            close()
        dataBase = DataBase(File(dataBaseName))
        dataBase.open()
        open = true
    }

    private fun close() {
        dataBase.close()
        dataBase = DataBase(File(utility.dataBaseFileName))
        open = false
    }

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
