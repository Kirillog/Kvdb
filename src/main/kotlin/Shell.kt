import java.io.File
import java.io.IOException
import java.lang.Thread.sleep
import java.util.*

class Shell(val defaultDataBaseName: String = "junk.dbm") {
    data class Command(val operation: Operation, val arguments: List<String>)

    enum class Operation(val args: Int) {
        CLOSE(0), STATUS(0), QUIT(0), OPEN(1), STORE(2), DELETE(1), FETCH(1), LIST(0);

        override fun toString(): String {
            return this.name.lowercase(Locale.getDefault())
        }
    }

    var dataBase = DataBase(File(defaultDataBaseName))
    var exit = false
    var open = false

    private fun quit() {
        exit = true
    }

    private fun open(dataBaseName: String) {
        dataBase = DataBase(File(dataBaseName))
        open = true
    }

    private fun close() {
        dataBase = DataBase(File(this.defaultDataBaseName))
        open = false
    }

    private fun status(): List<String> {
        val state = if (open)
            ""
        else
            " not"
        return listOf("Database file: ${dataBase.file.name}", "Database is$state open")
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

    private fun printName() {
        print("dbm> ")
    }

    private fun printList(list: List<String>) {
        list.forEach { println(it) }
    }

    private fun println(message: String) {
        kotlin.io.println(message)
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
                Operation.QUIT ->
                    quit()
            }
        } catch (err: DataBaseException) {
            System.err.println(err.message)
        }
    }
    fun clear(){
        File(defaultDataBaseName).delete()
    }
}


fun readFromShell(command: Utility) {
    val shell = Shell(command.dataBaseFileName)
    while (!shell.exit) {
        try {
            val command = shell.readCommand()
            shell.run(command)
        } catch (err: IOException) {
            System.err.println(err.message)
            sleep(100)
        }
    }
}