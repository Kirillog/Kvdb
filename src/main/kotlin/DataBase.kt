import java.io.File
import java.lang.Exception
import java.lang.IndexOutOfBoundsException

/**
 * describes all database exceptions
 */
class DataBaseException(message: String) : Exception(message)

/**
 * describes database opened in [file]
 * @param hashTable replies on queries to database
 */
class DataBase(val file: File) {
    private val hashTable = HashMap<String, String>()

    init {
        if (!file.exists())
            file.createNewFile()
    }

    /**
     * read pairs (key, value) from file and storage to hashTable
     */
    fun open() {
        file.useLines {
            it.forEach { line ->
                try {
                    val (key, value) = line.split("->")
                    hashTable[key] = value
                }
                catch (err : IndexOutOfBoundsException) {
                    throw DataBaseException("Cannot open ${file.name}")
                }
            }
        }
    }

    fun contains(key: String): Boolean {
        return key in hashTable
    }

    fun store(key: String, value: String) {
        hashTable[key] = value
    }

    fun delete(key: String) {
        if (key !in hashTable)
            throw DataBaseException("Item not found")
        hashTable.remove(key)
    }

    fun fetch(key: String): String {
        return hashTable[key] ?: throw DataBaseException("No such item found")
    }

    /**
     * returns list of pairs (key, value) of dataBase
     */
    fun list(): List<String> {
        val list = mutableListOf<String>()
        hashTable.forEach { (key, value) ->
            list.add("$key->$value")
        }
        return list
    }

    fun clear() {
        hashTable.clear()
    }

    /**
     * rewrite all fields (key, value) to database file after changes
     */
    fun close() {
        file.bufferedWriter().use { out ->
            hashTable.forEach { (key, value) ->
                out.write("$key->$value\n")
            }
        }
        if (hashTable.isEmpty())
            file.delete()
        hashTable.clear()
    }
}