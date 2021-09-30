import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import java.io.*
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TestInput {
    private val testFile1 = File("test1.txt")
    private val testDataBase = File("test.dbm")
    private val standardErr = System.err
    private val stream = ByteArrayOutputStream()

    @BeforeAll
    fun setUp() {
        System.setErr(PrintStream(stream))
        testFile1.createNewFile()
        testDataBase.createNewFile()
    }

    @AfterAll
    fun tearDown() {
        System.setErr(standardErr)
        testFile1.delete()
        testDataBase.delete()
    }

    @Nested
    inner class NamesOfFilesTest {
        @BeforeEach
        fun reset() {
            stream.reset()
        }

        @Test
        fun wrongDataBaseName() {
            parseArguments(arrayOf("junk.gdbm"))
            assertEquals("No such file or directory: 'junk.gdbm'", stream.toString().trim())
        }

        @Test
        fun wrongInputFileName() {
            parseArguments(arrayOf("-file= test1.txt"))
            assertEquals("No such file or directory: ' test1.txt'", stream.toString().trim())
        }
    }

    @Nested
    inner class ParsingArgumentsTest {
        @BeforeEach
        fun reset() {
            stream.reset()
        }

        @Test
        fun defaultDataBaseTest() {
            val command = parseArguments(arrayOf())
            assertTrue(command.shell)
            assertEquals("junk.dbm", command.dataBaseFileName)
        }

        @Test
        fun dataBaseFromFileTest() {
            val command = parseArguments(arrayOf("test.dbm"))
            assertTrue(command.shell)
            assertEquals("test.dbm", command.dataBaseFileName)
        }

        @Test
        fun commandsFromFileTest() {
            val command = parseArguments(arrayOf("-file=test1.txt"))
            assertFalse(command.shell)
            assertEquals("test1.txt", command.commandFileName)
        }

        @Test
        fun coloredOutputTest() {
            val command = parseArguments(arrayOf("-color"))
            assertTrue(command.color)
        }

        @Test
        fun invalidOptionTest() {
            parseArguments(arrayOf("-file"))
            assertEquals("Invalid option -- -file", stream.toString().trim())
        }

        @Test
        fun notExistingOptionTest() {
            parseArguments(arrayOf("-colorful"))
            assertEquals("Invalid option -- -colorful", stream.toString().trim())
        }
    }
}
