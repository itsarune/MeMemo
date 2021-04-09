package model;

import org.json.JSONException;
import org.json.JSONObject;
import persistence.PersistenceHelpers;

import java.time.LocalDateTime;

/**
 * ReminderNote is a Note that has a reminder attached to it.
 */
public class ReminderNote extends NoteBase {
    public static final String NO_REMINDER_SET_STRING = "No reminder set.";
    private LocalDateTime reminder;
    private boolean hasRun = true;

    //EFFECTS: creates a note with a default title and empty description
    public ReminderNote() {
        super();
    }

    //EFFECTS: creates a note with the given title and empty description
    public ReminderNote(String title) {
        super(title);
    }

    //EFFECTS: converts a note to a general ReminderNote with a copied title and description
    public ReminderNote(NoteBase note) {
        super(note.getTitle());
        text = note.getText();
    }

    //EFFECTS: creates a note from a given JSONObject; creates a hasRun field if it doesn't exist for backwards
    //          compatibility
    public ReminderNote(JSONObject json) {
        super(json);
        try {
            reminder = LocalDateTime.parse(json.getString(PersistenceHelpers.REMINDER_NOTE_REMINDER));
        } catch (JSONException ignored) {
            ;
        }
        try {
            hasRun = Boolean.parseBoolean(json.getString(PersistenceHelpers.REMINDER_NOTE_HAS_RUN));
        } catch (JSONException noBooleanField) {
            hasRun = true;
        }
    }

    //MODIFIES: this
    //EFFECTS: changes this note's reminder
    public void changeReminder(LocalDateTime newDate) {
        reminder = newDate;
    }

    //EFFECTS: returns true if this note has a reminder, false otherwise
    public boolean hasReminder() {
        return (reminder != null);
    }

    //EFFECTS: gets the reminder associated with this note
    public LocalDateTime getReminder() {
        return reminder;
    }

    //EFFECTS: returns whether the reminder has run
    public boolean hasRun() {
        return hasRun;
    }

    //setter
    public void setHasRun(boolean hasRun) {
        this.hasRun = hasRun;
    }

    //EFFECTS: returns the note type as a reminder note
    @Override
    public NoteType getNoteType() {
        return NoteType.REMINDER_NOTE;
    }

    //EFFECTS: converts this object into a JSONObject
    @Override
    public JSONObject toJson() {
        JSONObject jsonReminderNote = super.toJson();
        if (hasReminder()) {
            jsonReminderNote.put(PersistenceHelpers.REMINDER_NOTE_REMINDER, reminder.toString());
        }
        jsonReminderNote.put(PersistenceHelpers.REMINDER_NOTE_HAS_RUN, String.valueOf(hasRun));

        return jsonReminderNote;
    }

    //EFFECTS: converts this object to a String
    @Override
    public String toString() {
        if (hasReminder()) {
            return reminder.toString();
        } else {
            return NO_REMINDER_SET_STRING;
        }
    }
}
