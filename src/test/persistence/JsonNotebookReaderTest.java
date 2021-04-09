package persistence;

import model.CollectionOfNotes;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;
import static model.TestUtils.*;

/**
 * Testing class for JsonNotebookBuilder
 */
public class JsonNotebookReaderTest {
    private CollectionOfNotes notebook;
    private JsonNotebookBuilder reader;
    private JsonNotebookWriter writer;

    @BeforeEach
    public void setup() {
        notebook = new CollectionOfNotes("readerTest");
    }

    @Test
    public void testBuilderNonExistentFile() {
        reader = new JsonNotebookBuilder("./data/non_existent_file.json");
        try {
            reader.read();
            fail("Exception expected.");
        } catch (IOException e) {
            ;
        }
    }

    @Test
    public void testBuilderEmptyNotebook() {
        reader = new JsonNotebookBuilder("./data/testBuilderEmptyNotebook.json");
        try {
            CollectionOfNotes received = reader.read();
            assertNotebook(notebook, received);
        } catch (IOException e) {
            fail("Unexpected exception.");
            e.printStackTrace();
        }
    }

    @Test
    public void testBuilderSingleNote() {
        String src = "./data/testBuilderSingleNote.json";
        writer = new JsonNotebookWriter(src);

        NoteBase note = initGeneralNote();
        notebook.addNote(note);
        try {
            writer.open();
            writer.write(notebook);
            writer.close();
        } catch (FileNotFoundException e) {
            fail("Unable to setup test - Error with Writer");
        }

        reader = new JsonNotebookBuilder(src);
        try {
            CollectionOfNotes received = reader.read();
            assertNotebook(notebook, received);
        } catch (IOException e) {
            fail("Unexpected exception. Can't find file.");
            e.printStackTrace();
        }
    }

    @Test
    public void testBuilderMultipleNotes() {
        String src = "./data/testBuilderFilledNotebook.json";
        NoteBase [] notes = { initTasklistNote(), initGeneralNote(), initReminderNote()};
        for (NoteBase note : notes) {
            notebook.addNote(note);
        }
        try {
            writer = new JsonNotebookWriter(src);
            writer.open();
            writer.write(notebook);
            writer.close();
        } catch (FileNotFoundException e) {
            fail("Unable to setup test - problem with Writer");
        }

        reader = new JsonNotebookBuilder(src);
        try {
            CollectionOfNotes received = reader.read();
            assertNotebook(notebook, received);
        } catch (IOException e) {
            fail("Unexpected exception. Can't find file.");
            e.printStackTrace();
        }
    }
}
