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

    @Test
    fun wrongInputFileName() {
        parseArguments(arrayOf("-fileIn", "test2.txt"))
        assertEquals("No such file or directory: 'test2.txt'", stream.toString().trim())
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
            assertTrue(command.readFromShell)
            assertEquals("junk.dbm", command.dataBaseFileName)
        }

        @Test
        fun dataBaseFromFileTest() {
            val command = parseArguments(arrayOf("test.dbm"))
            assertTrue(command.readFromShell)
            assertEquals("test.dbm", command.dataBaseFileName)
        }

        @Test
        fun commandsFromFileTest() {
            val command = parseArguments(arrayOf("-fileIn", "test1.txt"))
            assertFalse(command.readFromShell)
        }

        @Test
        fun outToFileTest() {
            val command = parseArguments(arrayOf("-fileOut", "test3.txt"))
            assertFalse(command.writeToShell)
            File("test3.txt").delete()
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
