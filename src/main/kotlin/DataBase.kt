import java.io.File
import java.lang.Exception

class DataBaseException(message: String) : Exception(message)

class DataBase(val file: File) {
    private val hashTable = HashMap<String, String>()

    init {
        if (!file.exists())
            file.createNewFile()
    }

    fun open() {
        file.useLines {
            it.forEach { line ->
                val (key, value) = line.split(" ")
                hashTable[key] = value
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

    fun list(): List<String> {
        val list = mutableListOf<String>()
        hashTable.forEach { (key, value) ->
            list.add("$key $value")
        }
        return list
    }

    fun clear() {
        hashTable.clear()
    }

    fun close() {
        file.bufferedWriter().use { out ->
            hashTable.forEach { (key, value) ->
                out.write("$key $value\n")
            }
        }
        if (hashTable.isEmpty())
            file.delete()
        hashTable.clear()
    }
}