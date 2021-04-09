package model;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.PersistenceHelpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * NoteBaseTest is a generic testing framework for the other Note types.
 */
public abstract class NoteBaseTest {
    protected NoteBase testNote;

    @BeforeEach
    public void setup() {
        Note.resetId();
    }

    @Test
    public void testEmptyConstructorOneNote() {
        Assertions.assertEquals("Unnamed 0", testNote.getTitle());
        Assertions.assertTrue(testNote.getText().isEmpty());
    }

    @Test
    public abstract void testEmptyConstructorTwoNotes();

    //EFFECTS: helper function for testEmptyConstructorTwoNotes
    protected void testEmptyConstructorTwoNotesHelper(NoteBase note) {
        assertEquals("Unnamed 0", testNote.getTitle());
        assertTrue(testNote.getText().isEmpty());

        assertEquals("Unnamed 1", note.getTitle());
    }

    @Test
    public abstract void testOneParameterConstructor();

    //EFFECTS: helper function for testOneParameterConstructor
    protected void testOneParameterConstructorHelper(NoteBase noteWithTitle) {
        assertEquals("My title", noteWithTitle.getTitle());
    }

    @Test
    public abstract void testEmptyConstructorMixedParams();

    //EFFECTS: helper function for testEmptyConstructorMixedParams
    protected void testEmptyConstructorMixedParamsHelper(NoteBase note1, NoteBase note2) {
        assertEquals("Unnamed 0", testNote.getTitle());
        assertTrue(testNote.getText().isEmpty());

        assertEquals("My title", note1.getTitle());

        assertEquals("Unnamed 1", note2.getTitle());
        assertTrue(note2.getText().isEmpty());
    }

    @Test
    public abstract void testOneParameterConstructorJsonNote();

    @Test
    public abstract void testResetId();

    @Test
    public abstract void testNoteToJson();

    //EFFECTS: tests that a JSONObject is the same as this's testNote
    protected void testNoteToJsonHelper(JSONObject jsonNote) {
        assertEquals(testNote.getNoteType(), NoteType.valueOf(jsonNote.getString(PersistenceHelpers.NOTE_TYPE)));
        assertEquals(testNote.getTitle(), jsonNote.getString(PersistenceHelpers.NOTE_NAME));
        assertEquals(testNote.getText(), jsonNote.getString(PersistenceHelpers.NOTE_DATA));
    }
}
