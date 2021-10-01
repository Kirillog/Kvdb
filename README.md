User guide
---

## *Using*

For Linux/MacOS:
---

```#bash 
./dbm [OPTIONS] [FILE]
```

For example ```./dbm -color -fileIn src/test/files/commands.txt``` run database manager with colorized shell reading
commands from ```commands.txt```.<br>
All options should start with '-' otherwise dbm fall with error<br>

---

## Description

The **dbm** utility allows you to view and modify an existing DBM database or to create a new one in ``FILE``
If no file was stated then **dbm** used default junk.dbm.

After running the utility you can interact with **shell**, which waits user commands after ``dbm> ``

Then **shell** starts a loop, in which it reads commands from the standard input, executes them and prints the results
on the standard output. If the standard input is attached to a console, the program runs in interactive mode.

The program terminates when the quit command is given, or end-of-file is detected on its standard input.

Commands can also be specified in the file, after the ``-fileIn``. In this case, they will be interpreted
without attempting to read more commands from the standard input.

### Options:

+ ``-fileIn FILE`` redirect input stream to ``FILE``
+ ``-fileOut FILE`` redirect output stream to ``FILE``
+ ``-color`` output shell with color

### Shell commands

- **open** <ins>FILE</ins> <br> open the database <ins>FILE</ins>
- **close** <br> close the currently open database
- **quit** <br> close the database and quit the utility
- **store** <ins>KEY</ins> <ins>VALUE</ins> <br> store the <ins>VALUE</ins> with the given <ins>KEY</ins> in the database. 
If the <ins>KEY</ins> already exists, its data will be replaced.
- **fetch** <ins>KEY</ins> <br> fetch and display the record with the given <ins>KEY</ins>.
- **contains** <ins>KEY</ins> <br> return whether the database contains <ins>KEY</ins> or not
- **delete** <ins>KEY</ins> <br> delete record with the given <ins>KEY<ins>
- **list** <br> list the contents of the database
- **status** <br> print current program status
---

## Output

In runtime for each command print the result of command. For example, for ```status``` prints 

```
Database file: junk.gdbm
Database is open
```

After `close` command stores all changes to the database file.

Testing
---
You can find module tests in ```src/test/kotlin``` for all functions.

And there are test files in ```src/test/files``` to check the whole program. For example, you can
try ```./dbm -fileIn src/test/files/commands.txt test.dbm``` to check the whole program.