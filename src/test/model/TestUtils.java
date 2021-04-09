package model;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDateTime;

/**
 * Other functions designed for ease of testing.
 */
public class TestUtils {

    //EFFECTS: asserts that two notebooks are the same
    public static void assertNotebook(CollectionOfNotes expected, CollectionOfNotes actual) {
        Assertions.assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); ++i) {
            assertNote(expected.getNote(i), actual.getNote(i));
        }
    }

    //EFFECTS: asserts that two notes are the same
    public static void assertNote(NoteBase expected, NoteBase actual) {
        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
        Assertions.assertEquals(expected.getText(), actual.getText());
        NoteType type = expected.getNoteType();
        switch (type) {
            case TASKLIST_NOTE :
                TasklistNote tlnExpected = (TasklistNote) expected;
                TasklistNote tlnActual = (TasklistNote) actual;
                Assertions.assertEquals(tlnExpected.numberOfSubtasks(), tlnActual.numberOfSubtasks());
                for (int i = 0; i < tlnExpected.numberOfSubtasks(); i++) {
                    Subtask sExpected = tlnExpected.getSubtask(i);
                    Subtask sActual = tlnActual.getSubtask(i);
                    Assertions.assertEquals(sExpected.getTask(), sActual.getTask());
                }
            case REMINDER_NOTE :
                try {
                    Assertions.assertEquals(((ReminderNote) expected).getReminder(),
                            ((ReminderNote) actual).getReminder());
                } catch (JSONException ignored) {

                }
                break;
        }
    }

    //EFFECTS: creates a new TasklistNote and returns it
    public static TasklistNote initTasklistNote() {
        TasklistNote tln = new TasklistNote("Tasklist Note");
        tln.addContent("can i get a deadline extension?");
        tln.changeReminder(LocalDateTime.now().plusMinutes(15));
        tln.addSubtask(new Subtask("finish phase two in time"));
        return tln;
    }

    //EFFECTS: creates a new Note and returns it
    public static Note initGeneralNote() {
        Note n = new Note("General note");
        n.addContent("a general note");
        return n;
    }

    //EFFECTS: creates a new ReminderNote and returns it
    public static ReminderNote initReminderNote() {
        ReminderNote rn = new ReminderNote("This is a reminder note");
        rn.addContent("why did i procrastinate so much?");
        rn.changeReminder(LocalDateTime.now().plusHours(1));
        return rn;
    }
}
