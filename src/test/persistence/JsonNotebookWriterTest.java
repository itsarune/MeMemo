package persistence;

import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static model.TestUtils.*;

/**
 * Testing class for JsonNotebookWriter.
 */
public class JsonNotebookWriterTest {
    private CollectionOfNotes notebook;

    @BeforeEach
    public void setup() {
        notebook = new CollectionOfNotes("Writer Test");
    }

    @Test
    public void testEmptyNotebook() {
        JsonNotebookWriter emptyNotebookJson = new JsonNotebookWriter("./data/emptyNotebook.json");
        try {
            emptyNotebookJson.open();
            emptyNotebookJson.write(notebook);
            emptyNotebookJson.close();
        } catch (Exception e) {
            Assertions.fail("Found an unexpected exception.");
            e.printStackTrace();
        }

        JsonNotebookBuilder builtNotebook = new JsonNotebookBuilder("./data/emptyNotebook.json");
        try {
            CollectionOfNotes recoveredNotebook = builtNotebook.read();
            Assertions.assertNotNull(recoveredNotebook);
            Assertions.assertEquals(notebook.getNotebookName(), recoveredNotebook.getNotebookName());
            Assertions.assertEquals(0, recoveredNotebook.size());
        } catch (IOException e) {
            Assertions.fail("An exception occured when reading the file.");
            e.printStackTrace();
        }
    }

    @Test
    public void testExceptionInvalidFileName() {
        JsonNotebookWriter jsonFile = new JsonNotebookWriter("./data/broken!file:\n\0name?");
        Assertions.assertThrows(FileNotFoundException.class, () -> { jsonFile.open(); });
    }
    
    @Test
    public void testSingleNote() {
        JsonNotebookWriter testNotebookWriter = new JsonNotebookWriter("./data/testSingleNote.json");
        
        Note testNote = new Note();
        notebook.addNote(testNote);
        try {
            testNotebookWriter.open();
            testNotebookWriter.write(notebook);
            testNotebookWriter.close();
        } catch (FileNotFoundException e) {
            Assertions.fail("Unexpected exception");
        }

        JsonNotebookBuilder reader = new JsonNotebookBuilder("./data/testSingleNote.json");
        try {
            CollectionOfNotes recoveredNotes = reader.read();
            assertNotebook(notebook, recoveredNotes);
        } catch (IOException e) {
            Assertions.fail("Unexpected exception");
            e.printStackTrace();
        }
    }

    @Test
    public void testMultipleNotes() {
        String dir = "./data/testMultipleNotes.java";
        JsonNotebookWriter testNotebookWriter = new JsonNotebookWriter(dir);
        NoteBase[] testNotes = {initGeneralNote(), initTasklistNote(), initReminderNote() };
        for (NoteBase testNote : testNotes) {
            notebook.addNote(testNote);
        }

        //write notes to the file
        try {
            testNotebookWriter.open();
            testNotebookWriter.write(notebook);
            testNotebookWriter.close();
        } catch (FileNotFoundException e) {
            Assertions.fail("Unexpected exception. Can't open file.");
        }

        //read everything and assert
        JsonNotebookBuilder reader = new JsonNotebookBuilder(dir);
        try {
            CollectionOfNotes recovered = reader.read();
            assertNotebook(notebook, recovered);
        } catch (IOException e) {
            Assertions.fail("Unexpected exception");
            e.printStackTrace();
        }
    }
}
