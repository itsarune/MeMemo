package model;

import org.json.JSONObject;
import persistence.PersistenceHelpers;

/**
 * Note is the most general note with a simple title and a description.
 */
public class Note extends NoteBase {

    //EFFECTS: creates a note with a default title and empty description
    public Note() {
        super();
    }

    //EFFECTS: creates a note with the given title and empty description
    public Note(String title) {
        super(title);
    }

    //EFFECTS: creates a note from the given JSONObject
    public Note(JSONObject json) {
        super(json);
    }

    @Override
    //EFFECTS: returns the note type as a general note
    public NoteType getNoteType() {
        return NoteType.GENERAL_NOTE;
    }
}
