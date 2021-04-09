package model;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonNotebookBuilder;
import persistence.JsonNotebookWriter;
import persistence.PersistenceHelpers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static model.TestUtils.assertNote;

/**
 * A testing class for ReminderNote.
 */
public class ReminderNoteTest extends NoteBaseTest {
    private ReminderNote reminderTestNote;

    @BeforeEach
    public void setup() {
        super.setup();
        testNote = new ReminderNote();
        reminderTestNote = (ReminderNote) testNote;
    }

    @Override
    @Test
    public void testEmptyConstructorTwoNotes() {
        NoteBase testNote2 = new ReminderNote();
        testEmptyConstructorTwoNotesHelper(testNote2);
    }

    @Override
    @Test
    public void testOneParameterConstructor() {
        NoteBase noteWithGivenTitle = new ReminderNote("My title");
        testOneParameterConstructorHelper(noteWithGivenTitle);
    }

    @Override
    @Test
    public void testEmptyConstructorMixedParams() {
        NoteBase noteWithGivenTitle = new ReminderNote("My title");
        NoteBase anotherNote = new ReminderNote();
        testEmptyConstructorMixedParamsHelper(noteWithGivenTitle, anotherNote);
    }

    @Override
    @Test
    public void testOneParameterConstructorJsonNote() {
        ReminderNote expectedNote = new ReminderNote("json reminder");
        expectedNote.addContent("reminder description");
        reminderTestNote = new ReminderNote(expectedNote.toJson());
        assertNote(expectedNote, reminderTestNote);
    }

    @Test
    public void testOneParameterConstructorWithNoReminderField() {
        JsonNotebookBuilder builder = new JsonNotebookBuilder("./data/testBackwardsCompatibleReminderNote.json");assertDoesNotThrow(() -> {
            CollectionOfNotes notebook = builder.read();
            reminderTestNote = (ReminderNote) notebook.getNote(0);
            assertEquals("test reminder note", reminderTestNote.getTitle());
            assertEquals("", reminderTestNote.getText());
            LocalDateTime expectedTime = LocalDateTime.parse("2021-03-08T13:26:56.642");
            assertTrue(reminderTestNote.hasReminder());
            assertEquals(expectedTime, reminderTestNote.getReminder());
            assertTrue(reminderTestNote.hasRun());
        });
    }

    @Test
    public void testOneParameterConstructorJsonNoteWithReminder() {
        ReminderNote expectedNote = new ReminderNote("json reminder");
        expectedNote.addContent("reminder description");
        expectedNote.changeReminder(LocalDateTime.now().plusMinutes(4));
        reminderTestNote = new ReminderNote(expectedNote.toJson());
        assertNote(expectedNote, reminderTestNote);
    }

    @Override
    @Test
    public void testNoteToJson() {
        testNote.addContent("reminder desc");
        try {
            testNoteToJsonHelper(testNote.toJson());
        } catch (JSONException ignored) {

        }
    }

    @Test
    public void testNoteToJsonWithReminder() {
        testNote.addContent("has a reminder");
        reminderTestNote.changeReminder(LocalDateTime.now().plusMinutes(76));
        testNoteToJsonHelper(testNote.toJson());
    }

    //EFFECTS: tests that a JSONObject is the same as this's testNote
    @Override
    protected void testNoteToJsonHelper(JSONObject jsonNote) {
        super.testNoteToJsonHelper(jsonNote);
        assertEquals(reminderTestNote.getReminder(),
                LocalDateTime.parse(jsonNote.getString(PersistenceHelpers.REMINDER_NOTE_REMINDER)));
        assertEquals(reminderTestNote.hasRun(),
                Boolean.parseBoolean(jsonNote.getString(PersistenceHelpers.REMINDER_NOTE_HAS_RUN)));
    }

    @Override
    @Test
    public void testResetId() {
        assertEquals("Unnamed 0", testNote.getTitle());
        assertEquals("Unnamed 1", new ReminderNote().getTitle());
        Note.resetId();
        assertEquals("Unnamed 0", new ReminderNote().getTitle());
    }

    @Test
    public void testConstructorNoteParam() {
        NoteBase initialNote = new Note();
        ReminderNote reminderNote = new ReminderNote(initialNote);

        assertEquals(initialNote.getTitle(), reminderNote.getTitle());
        assertEquals(initialNote.getText(), reminderNote.getText());
        assertEquals(NoteType.REMINDER_NOTE, reminderNote.getNoteType());
    }

    @Test
    public void testGetNoteType() {
        assertEquals(NoteType.REMINDER_NOTE, reminderTestNote.getNoteType());
    }

    @Test
    public void testChangeReminder() {
        assertNull(reminderTestNote.getReminder());

        LocalDateTime timestamp = LocalDateTime.now().plusDays(1);
        reminderTestNote.changeReminder(timestamp);

        assertEquals(timestamp, reminderTestNote.getReminder());
    }

    @Test
    public void testHasReminder() {
        assertFalse(reminderTestNote.hasReminder());

        LocalDateTime timestamp = LocalDateTime.now().plusDays(1);
        reminderTestNote.changeReminder(timestamp);

        assertTrue(reminderTestNote.hasReminder());
    }

    @Test
    public void testHasRun() {
        assertTrue(reminderTestNote.hasRun());
        reminderTestNote.setHasRun(false);
        assertFalse(reminderTestNote.hasRun());
    }

    @Test
    public void testToString() {
        assertEquals(ReminderNote.NO_REMINDER_SET_STRING ,reminderTestNote.toString());
        LocalDateTime time = LocalDateTime.now().plusHours(1);
        reminderTestNote.changeReminder(time);
        assertEquals(time.toString(), reminderTestNote.toString());
    }
}
