package model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.PersistenceHelpers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static model.TestUtils.*;

/**
 * A testing class for CollectionOfNotes.
 */
public class CollectionOfNotesTest {
    private CollectionOfNotes notebook;

    @BeforeEach
    public void setup() {
        notebook = new CollectionOfNotes("test motebook");
    }

    @Test
    public void testAddNoteOneNote() {
        Note testNote = new Note();
        String taskName = "A";
        String taskDescription = "testing!";
        testNote.changeTitle(taskName);
        testNote.addContent(taskDescription);

        notebook.addNote(testNote);

        NoteBase receivedNote = notebook.getNote(taskName);
        assertEquals(taskName, receivedNote.getTitle());
        assertEquals(taskDescription, receivedNote.getText());
    }

    @Test
    public void testAddNoteMultipleNotes() {
        ArrayList<Note> testNotes = addManyNotesToNotebook();

        Collection<NoteBase> allNotes = notebook.getAllNotes();
        assertEquals(6, allNotes.size());

        for (Note note : testNotes) {
            assertTrue(allNotes.contains(note));
        }
    }

    //EFFECTS: adds many notes to this notebook
    private ArrayList<Note> addManyNotesToNotebook() {
        ArrayList<Note> notes = new ArrayList<>();
        for (int i = 0; i <= 5; ++i) {
            Note note = new Note();
            setupNote(note, String.valueOf(i), String.valueOf(i+10));
            notebook.addNote(note);
            notes.add(note);
        }
        return notes;
    }

    @Test
    public void testUpdateNote() {
        NoteBase testNote = new TasklistNote();
        setupNote(testNote, "hello", "world");
        notebook.addNote(testNote);

        notebook.updateNote("hello", "updated");
        assertEquals("updated", testNote.getText());
    }

    @Test
    public void testUpdateNoteDoesNothing() {
        ArrayList<Note> testNotes = addManyNotesToNotebook();
        notebook.updateNote("hello", "world");
        List<NoteBase> allNotes = notebook.getAllNotes();
        for (NoteBase note : allNotes) {
            assertNotEquals("hello", note.getTitle());
            assertNotEquals("world", note.getText());
        }
    }

    @Test
    public void testGetNoteAtIndexStringParameter() {
        Note note1 = new Note("aloha");
        Note note2 = new Note("gutentag");
        Note note3 = new Note("bonjour");
        notebook.addNote(note1);
        notebook.addNote(note2);
        notebook.addNote(note3);

        assertEquals(note1, notebook.getNote("aloha"));
        assertEquals(note3, notebook.getNote("bonjour"));

        assertNull(notebook.getNote("hi [british]"));
    }

    @Test
    public void testGetNoteAtIndexIndexParameter() {
        NoteBase [] testNotes = { initGeneralNote(), initTasklistNote(), initReminderNote() };
        for (NoteBase testNote : testNotes) {
            notebook.addNote(testNote);
        }
        for (int i = 0; i < 3; ++i) {
            TestUtils.assertNote(testNotes[i], notebook.getNote(i));
        }
    }

    @Test
    public void testGetNoteAtIndexOutOfBounds() {
        NoteBase [] testNotes = { initGeneralNote(), initTasklistNote(), initReminderNote() };
        for (NoteBase testNote : testNotes) {
            notebook.addNote(testNote);
        }
        assertNull(notebook.getNote(-1));
        assertNull(notebook.getNote(3));
        assertNull(notebook.getNote(4));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(notebook.isEmpty());
        notebook.addNote(new Note());
        assertFalse(notebook.isEmpty());
    }

    private void setupNote(NoteBase note, String name, String description) {
        note.changeTitle(name);
        note.addContent(description);
    }

    @Test
    public void testReplaceNote() {
        Note note1 = new Note();
        notebook.addNote(note1);
        ReminderNote note2 = new ReminderNote();
        notebook.replaceNote(note1, note2);
        assertFalse(notebook.contains(note1));
        assertTrue(notebook.contains(note2));
    }

    @Test
    public void testReplaceNoteMultipleNotes() {
        ArrayList<Note> notes = addManyNotesToNotebook();

        ReminderNote otherNote = new ReminderNote();
        notebook.replaceNote(notes.get(3), otherNote);

        assertEquals(6, notebook.size());
        int i = 0;
        for (; i < 3; ++i) {
            assertTrue(notebook.contains(notes.get(i)));
        }
        assertFalse(notebook.contains(notes.get(i)));
        assertTrue(notebook.contains(otherNote));
        for (++i; i < notes.size(); ++i) {
            assertTrue(notebook.contains(notes.get(i)));
        }
    }

    @Test
    public void testContainsElement() {
        Note note1 = new Note();
        notebook.addNote(note1);

        assertTrue(notebook.contains(note1));
    }

    @Test
    public void testContainsFalse() {
        Note note1 = new Note();
        assertFalse(notebook.contains(note1));
    }

    @Test
    public void testSizeOneElement() {
        notebook.addNote(new TasklistNote());
        assertEquals(1, notebook.size());
    }

    @Test
    public void testSizeMultipleElements() {
        ArrayList<Note> testNotes = addManyNotesToNotebook();
        assertEquals(testNotes.size(), notebook.size());
    }

    @Test
    public void testNotebookToJsonEmptyNotebook() {
        JSONObject notebookJson = notebook.toJson();
        assertEquals(notebook.getNotebookName(), notebookJson.getString(PersistenceHelpers.NOTEBOOK_NAME));
        JSONArray notesArray = notebookJson.getJSONArray(PersistenceHelpers.NOTES);
        assertEquals(0, notesArray.length());
    }

    @Test
    public void testNotebookToJsonOneNoteNotebook() {
        ReminderNote rn = new ReminderNote("rem note");
        notebook.addNote(rn);
        JSONObject notebookJson = notebook.toJson();
        assertEquals(notebook.getNotebookName(), notebookJson.getString(PersistenceHelpers.NOTEBOOK_NAME));
        JSONArray notesArray = notebookJson.getJSONArray(PersistenceHelpers.NOTES);
        assertEquals(1, notesArray.length());
        assertNote(rn, notesArray.getJSONObject(0));
    }

    @Test
    public void testNotebookToJsonMultipleNoteNotebook() {
        ArrayList<Note> testNotes = addManyNotesToNotebook();
        JSONObject notebookJson = notebook.toJson();
        assertEquals(notebook.getNotebookName(), notebookJson.getString(PersistenceHelpers.NOTEBOOK_NAME));
        JSONArray notesArray = notebookJson.getJSONArray(PersistenceHelpers.NOTES);
        for (int i = 0; i < testNotes.size(); ++i) {
            assertNote(testNotes.get(i), notesArray.getJSONObject(i));
        }
    }

    @Test
    public void testGetAllNoteNames() {
        Collection<Note> testNotes = addManyNotesToNotebook();
        Collection<String> noteNames = notebook.getAllNoteNames();
        assertEquals(testNotes.size(), noteNames.size());
        for (int i = 0; i <= 5; ++i) {
            assertTrue(noteNames.contains(String.valueOf(i)));
        }
    }

    @Test
    public void testGetIndex() {
        List<Note> testNotes = addManyNotesToNotebook();
        for (NoteBase note : testNotes) {
            int index = notebook.getIndex(note);
            assertEquals(note, notebook.getNote(index));
        }
    }

    @Test
    public void testChangeNotebookName() {
        notebook.changeNotebookName("test notebook 2");
        assertEquals("test notebook 2", notebook.getNotebookName());
    }

    //EFFECTS: checks whether a note and a given JSONObject represents that note
    private void assertNote(NoteBase note, JSONObject jsonNote) {
        NoteType type = NoteType.valueOf(jsonNote.getString(PersistenceHelpers.NOTE_TYPE));
        assertEquals(note.getNoteType(), type);
        assertEquals(note.getTitle(), jsonNote.getString(PersistenceHelpers.NOTE_NAME));
        assertEquals(note.getText(), jsonNote.getString(PersistenceHelpers.NOTE_DATA));
        switch (type) {
            case TASKLIST_NOTE :
                JSONArray subtaskArray = jsonNote.getJSONArray(PersistenceHelpers.TASKLIST_SUBTASKS);
                assertEquals(((TasklistNote) note).numberOfSubtasks(), subtaskArray.length());
                for (int i = 0; i < subtaskArray.length(); ++i) {
                    assertEquals(((TasklistNote) note).getSubtask(i).getTask(),
                            subtaskArray.getJSONObject(i).getString(PersistenceHelpers.SUBTASK));
                }
            case REMINDER_NOTE :
                try {
                    assertEquals(((ReminderNote) note).getReminder(),
                            LocalDateTime.parse(jsonNote.getString(PersistenceHelpers.REMINDER_NOTE_REMINDER)));
                } catch (JSONException ignored) {

                }
                break;
        }
    }
}
