import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import java.io.*
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DataBaseTest {
    private val dataBase = DataBase(File("test.dbm"))

    private fun assertEqual(list1: List<String>, list2: List<String>) {
        list1.forEach { assertContains(list2, it) }
        list2.forEach { assertContains(list1, it) }
    }

    @AfterAll
    fun setDown() {
        dataBase.file.delete()
    }

    @Nested
    inner class ShellOperationTest {
        @BeforeEach
        fun clear() {
            dataBase.clear()
        }

        @Test
        fun fetchEmptyDataBase() {
            val exception = assertThrows<DataBaseException> { dataBase.fetch("key") }
            assertEquals("No such item found", exception.message)
        }

        @Test
        fun containsTest() {
            dataBase.store("key", "value")
            assertTrue(dataBase.contains("key"))
        }

        @Test
        fun notContainsTest() {
            assertFalse(dataBase.contains("key"))
        }

        @Test
        fun storeTest() {
            dataBase.store("key", "value")
            assertEquals("value", dataBase.fetch("key"))
        }

        @Test
        fun reStoreTest() {
            dataBase.store("key", "oldValue")
            dataBase.store("key", "newValue")
            assertEquals("newValue", dataBase.fetch("key"))
        }

        @Test
        fun deleteTest() {
            dataBase.store("key", "value")
            dataBase.delete("key")
            assertFalse(dataBase.contains("key"))
        }

        @Test
        fun listTest() {
            dataBase.store("key1", "value1")
            dataBase.store("key2", "value2")
            assertContentEquals(listOf("key1 value1", "key2 value2"), dataBase.list())
        }

    }

    @Nested
    inner class ReadAndWriteTest {
        @Test
        fun openTest() {
            val dataBase1 = DataBase(File("src/test/files/openTestFile.dbm"))
            dataBase1.open()
            assertContentEquals(listOf("key1 value1", "key3 value3"), dataBase1.list())
        }

        @Test
        fun closeTest() {
            dataBase.store("key", "value")
            dataBase.store("key1", "value1")
            dataBase.store("key2", "value2")
            dataBase.delete("key1")
            dataBase.close()
            assertEqual(
                """
                key->value
                key2->value2
                """.trimIndent().split("\n"), File("test.dbm").readText().trimIndent().split("\n")
            )
        }
    }


}