import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import java.io.*
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ShellTest {
    private val testDataBase = File("test.dbm")
    private val standardOut = System.out
    private val streamOut = ByteArrayOutputStream()
    val shell = Shell(Utility())

    private fun writeInput(data: String) {
        System.setIn(ByteArrayInputStream(data.toByteArray()))
    }

    @BeforeAll
    fun setUp() {
        testDataBase.createNewFile()
        System.setOut(PrintStream(streamOut))
    }

    @AfterAll
    fun tearDown() {
        testDataBase.delete()
        shell.clear()
        System.setOut(standardOut)
    }

    @Nested
    inner class ReadCommandTest {
        @BeforeEach
        fun reset() {
            streamOut.reset()
        }

        @Test
        fun statusTest() {
            writeInput("status")
            assertEquals(Command(Operation.STATUS, listOf()), shell.readCommand())
        }

        @Test
        fun openCommandTest() {
            writeInput("open test.dbm")
            assertEquals(Command(Operation.OPEN, listOf("test.dbm")), shell.readCommand())
        }

        @Test
        fun storeCommandTest() {
            writeInput("store key value")
            assertEquals(Command(Operation.STORE, listOf("key", "value")), shell.readCommand())
        }

        @Test
        fun storeKeyWithSpace() {
            writeInput("store \"key 1\" value")
            assertEquals(Command(Operation.STORE, listOf("key 1", "value")), shell.readCommand())
        }

        @Test
        fun storeValueWithSpaceTest() {
            writeInput("store key \"value 1\"")
            assertEquals(Command(Operation.STORE, listOf("key", "value 1")), shell.readCommand())
        }

        @Test
        fun storeKeyAndValueWithSpaceTest() {
            writeInput("store \"key 1\" \"value 1\"")
            assertEquals(Command(Operation.STORE, listOf("key 1", "value 1")), shell.readCommand())
        }

        @Test
        fun fetchEmptyKeyTest() {
            writeInput("fetch \"\"")
            val exception = assertThrows<IOException> { shell.readCommand() }
            assertEquals("Cannot interpreter the empty strings", exception.message)
        }

        @Test
        fun deleteCommandTest() {
            writeInput("delete key")
            assertEquals(Command(Operation.DELETE, listOf("key")), shell.readCommand())
        }

        @Test
        fun emptyInputTest() {
            shell.readCommand()
            assertEquals("dbm> ", streamOut.toString())
        }

        @Test
        fun emptyLinesTest() {
            writeInput("\n\n\n")
            shell.readCommand()
            assertEquals("dbm> dbm> dbm> dbm> ", streamOut.toString())
        }

        @Test
        fun missingOperandFetchTest() {
            writeInput("fetch")
            val exception = assertThrows<IOException> { shell.readCommand() }
            assertEquals("Incorrect number of arguments for fetch", exception.message)
        }

        @Test
        fun missingOperandOpenTest() {
            writeInput("open")
            val exception = assertThrows<IOException> { shell.readCommand() }
            assertEquals("Incorrect number of arguments for open", exception.message)
        }

        @Test
        fun missingOperandDeleteTest() {
            writeInput("delete")
            val exception = assertThrows<IOException> { shell.readCommand() }
            assertEquals("Incorrect number of arguments for delete", exception.message)
        }

        @Test
        fun missingOperandContainsTest() {
            writeInput("contains")
            val exception = assertThrows<IOException> { shell.readCommand() }
            assertEquals("Incorrect number of arguments for contains", exception.message)
        }

        @Test
        fun missingOperandRemoveTest() {
            writeInput("remove")
            val exception = assertThrows<IOException> { shell.readCommand() }
            assertEquals("Incorrect number of arguments for remove", exception.message)
        }

        @Test
        fun missingOperandStoreTest() {
            writeInput("store key")
            val exception = assertThrows<IOException> { shell.readCommand() }
            assertEquals("Incorrect number of arguments for store", exception.message)
        }

        @Test
        fun missingOperandsStoreTest() {
            writeInput("store")
            val exception = assertThrows<IOException> { shell.readCommand() }
            assertEquals("Incorrect number of arguments for store", exception.message)
        }

        @Test
        fun extraOperandTest() {
            writeInput("close file.dbm")
            val exception = assertThrows<IOException> { shell.readCommand() }
            assertEquals("Incorrect number of arguments for close", exception.message)
        }

        @Test
        fun invalidCommandTest() {
            writeInput("get key")
            val exception = assertThrows<IOException> { shell.readCommand() }
            assertEquals("Unknown command -- 'get'", exception.message)
        }
    }

    @Nested
    inner class RunTest {
        @BeforeEach
        fun reset() {
            streamOut.reset()
        }

        @Test
        fun defaultStatusTest() {
            shell.run(Command(Operation.STATUS, listOf()))
            assertEquals(
                """
                Database file: junk.dbm
                Database is not open
                """.trimIndent(), streamOut.toString().trimIndent()
            )
        }

        @Test
        fun runOpenCommandTest() {
            shell.run(Command(Operation.OPEN, listOf("test.dbm")))
            assertEquals(testDataBase, shell.dataBase.file)
            assertTrue(shell.open)
        }

        @Test
        fun runCloseCommandTest() {
            shell.run(Command(Operation.OPEN, listOf("test.dbm")))
            shell.run(Command(Operation.CLOSE, listOf()))
            assertEquals(shell.utility.dataBaseFileName, shell.dataBase.file.name)
            assertFalse(shell.open)
        }

        @Test
        fun runStatusTest() {
            shell.run(Command(Operation.OPEN, listOf("test.dbm")))
            shell.run(Command(Operation.STATUS, listOf()))
            assertEquals(
                """
                Database file: test.dbm
                Database is open
                """.trimIndent(), streamOut.toString().trimIndent()
            )
        }

        @Test
        fun runRemoveCommandTest() {
            File("test1.dbm").createNewFile()
            shell.run(Command(Operation.REMOVE, listOf("test1.dbm")))
            assertEquals(shell.dataBase.file.name, "junk.dbm")
            assertFalse(shell.open)
        }

        @Test
        fun runRemoveCommandForNonExistingDataBase(){
            val exception = assertThrows<IOException> { shell.run(Command(Operation.REMOVE, listOf("test1.dbm"))) }
            assertEquals("There is no database with name 'test1.dbm'", exception.message)
        }
    }
}