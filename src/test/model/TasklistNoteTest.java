package model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.PersistenceHelpers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static model.TestUtils.assertNote;

/**
 * A testing class for Tasklist Note.
 */
public class TasklistNoteTest extends NoteBaseTest {
    private TasklistNote tasklistTestNote;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        testNote = new TasklistNote();
        tasklistTestNote = (TasklistNote) testNote;
    }

    @Override
    @Test
    public void testEmptyConstructorTwoNotes() {
        NoteBase testNote2 = new TasklistNote();
        testEmptyConstructorTwoNotesHelper(testNote2);
    }

    @Override
    @Test
    public void testOneParameterConstructor() {
        NoteBase noteWithGivenTitle = new TasklistNote("My title");
        testOneParameterConstructorHelper(noteWithGivenTitle);
    }

    @Override
    @Test
    public void testEmptyConstructorMixedParams() {
        NoteBase noteWithGivenTitle = new TasklistNote("My title");
        NoteBase anotherNote = new TasklistNote();
        testEmptyConstructorMixedParamsHelper(noteWithGivenTitle, anotherNote);
    }

    @Override
    @Test
    public void testOneParameterConstructorJsonNote() {
        TasklistNote expected = new TasklistNote("tln 1");
        expected.addContent("tln desc");
        expected.changeReminder(LocalDateTime.now().plusMinutes(53));
        expected.addSubtask(new Subtask("subtask 1"));
        expected.addSubtask(new Subtask("subtask 2"));
        tasklistTestNote = new TasklistNote(expected.toJson());
        assertNote(expected, tasklistTestNote);
    }

    @Override
    @Test
    public void testNoteToJson() {
        tasklistTestNote.addContent("tasklist description");
        tasklistTestNote.changeReminder(LocalDateTime.now().plusWeeks(2));
        tasklistTestNote.addSubtask(new Subtask("subtask 1"));
        testNoteToJsonHelper(testNote.toJson());
    }

    //EFFECTS: tests that a JSONObject is the same as this's testNote
    @Override
    protected void testNoteToJsonHelper(JSONObject jsonNote) {
        super.testNoteToJsonHelper(jsonNote);
        JSONArray subtaskArray = jsonNote.getJSONArray(PersistenceHelpers.TASKLIST_SUBTASKS);
        assertEquals(tasklistTestNote.numberOfSubtasks(), subtaskArray.length());
        for (int i = 0; i < subtaskArray.length(); ++i) {
            JSONObject subtaskJson = subtaskArray.getJSONObject(i);
            assertEquals(tasklistTestNote.getSubtask(i).getTask(),
                    subtaskJson.getString(PersistenceHelpers.SUBTASK));
        }
    }

    @Override
    public void testResetId() {
        assertEquals("Unnamed 0", testNote.getTitle());
        assertEquals("Unnamed 1", new TasklistNote().getTitle());
        Note.resetId();
        assertEquals("Unnamed 0", new TasklistNote().getTitle());
    }

    @Test
    public void testGetNoteType() {
        assertEquals(NoteType.TASKLIST_NOTE, tasklistTestNote.getNoteType());
    }

    @Test
    public void testAddOneSubtask() {
        Subtask subtask1 = new Subtask("A");
        tasklistTestNote.addSubtask(subtask1);

        assertEquals(1, tasklistTestNote.numberOfSubtasks());
        assertTrue(tasklistTestNote.containsSubtask(subtask1));
    }

    @Test
    public void testAddMultipleSubtasks() {
        Subtask subtask1 = new Subtask("A");
        tasklistTestNote.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("B");
        tasklistTestNote.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("C");
        tasklistTestNote.addSubtask(subtask3);

        assertEquals(3, tasklistTestNote.numberOfSubtasks());
        assertTrue(tasklistTestNote.containsSubtask(subtask1));
        assertTrue(tasklistTestNote.containsSubtask(subtask2));
        assertTrue(tasklistTestNote.containsSubtask(subtask3));
    }

    @Test
    public void testGetSubtaskAtIndex() {
        Subtask subtask1 = new Subtask("A");
        tasklistTestNote.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("B");
        tasklistTestNote.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("C");
        tasklistTestNote.addSubtask(subtask3);

        assertEquals(subtask1, tasklistTestNote.getSubtask(0));
        assertEquals(subtask3, tasklistTestNote.getSubtask(2));
    }

    @Test
    public void testGetSubtasks() {
        Subtask subtask1 = new Subtask("A");
        tasklistTestNote.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("B");
        tasklistTestNote.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("C");
        tasklistTestNote.addSubtask(subtask3);

        List<Subtask> subtasks = tasklistTestNote.getSubtasks();
        assertEquals(3, subtasks.size());
        assertEquals(subtask1, subtasks.get(0));
        assertEquals(subtask2, subtasks.get(1));
        assertEquals(subtask3, subtasks.get(2));
    }
}
