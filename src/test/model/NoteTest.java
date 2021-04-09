package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static model.TestUtils.assertNote;

/**
 * A testing class for Note.
 */
class NoteTest extends NoteBaseTest {
    private Note genNoteTestNote;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        testNote = new Note();
        genNoteTestNote = (Note) testNote;
    }

    @Override
    @Test
    public void testEmptyConstructorTwoNotes() {
        NoteBase testNote2 = new Note();
        testEmptyConstructorTwoNotesHelper(testNote2);
    }

    @Override
    @Test
    public void testOneParameterConstructor() {
        NoteBase noteWithGivenTitle = new Note("My title");
        testOneParameterConstructorHelper(noteWithGivenTitle);
    }

    //EFFECTS: helper function for testEmptyConstructorMixedParams
    @Override
    @Test
    public void testEmptyConstructorMixedParams() {
        NoteBase noteWithGivenTitle = new Note("My title");
        NoteBase anotherNote = new Note();
        testEmptyConstructorMixedParamsHelper(noteWithGivenTitle, anotherNote);
    }

    //EFFECTS: helper function for testOneParameterConstructor
    @Override
    public void testOneParameterConstructorJsonNote() {
        Note note = new Note();
        note.addContent("desc1");
        testNote = new Note(note.toJson());
        assertNote(note, testNote);
    }

    @Override
    public void testNoteToJson() {
        testNote = new Note("general note");
        testNote.addContent("general description");
        super.testNoteToJsonHelper(testNote.toJson());
    }

    @Override
    @Test
    public void testResetId() {
        assertEquals("Unnamed 0", testNote.getTitle());
        assertEquals("Unnamed 1", new Note().getTitle());
        Note.resetId();
        assertEquals("Unnamed 0", new Note().getTitle());
    }

    @Test
    public void testAddContent() {
        String testText = "Hello World!";
        genNoteTestNote.addContent(testText);

        assertEquals(testText, genNoteTestNote.getText());
    }

    @Test
    public void testChangeOrganizationColour() {
        assertEquals(Color.WHITE, testNote.getOrganizationColour());
        testNote.changeOrganization(Color.PINK);
        assertEquals(Color.PINK, testNote.getOrganizationColour());
    }

    @Test
    public void testChangeOrganizationFolder() {
        assertEquals(Note.CATEGORY_UNORGANIZED, testNote.getCategory());

        testNote.changeOrganization(Note.CATEGORY_GENERAL);
        assertEquals(Note.CATEGORY_GENERAL, testNote.getCategory());

        testNote.changeOrganization(Note.CATEGORY_HOMEWORK);
        assertEquals(Note.CATEGORY_HOMEWORK, testNote.getCategory());

        testNote.changeOrganization(Note.CATEGORY_MISC);
        assertEquals(Note.CATEGORY_MISC, testNote.getCategory());
    }

    @Test
    public void testGetNoteType() {
        assertEquals(NoteType.GENERAL_NOTE, testNote.getNoteType());
    }
}