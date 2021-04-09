package model;

import org.json.JSONObject;
import persistence.PersistenceHelpers;
import persistence.Writable;

import java.awt.*;

/**
 * A base note that all other notes are based off of.
 */
public abstract class NoteBase implements Writable {
    public static final int CATEGORY_UNORGANIZED = 0;
    public static final int CATEGORY_HOMEWORK = 1;
    public static final int CATEGORY_MISC = 2;
    public static final int CATEGORY_GENERAL = 3;

    protected static int id = 0;
    protected String text;
    protected String title;
    protected int folder = CATEGORY_UNORGANIZED;
    protected Color organizationColour = Color.WHITE;

    //EFFECTS: creates a note with a default title and empty description
    public NoteBase() {
        this.title = "Unnamed " + id;
        ++id;
        this.text = "";
    }

    //EFFECTS: creates a note with the given title and empty description
    public NoteBase(String title) {
        changeTitle(title);
        this.text = "";
    }

    //EFFECTS: constructs a note based on the given JSONObject
    public NoteBase(JSONObject json) {
        title = json.getString(PersistenceHelpers.NOTE_NAME);
        text = json.getString(PersistenceHelpers.NOTE_DATA);
    }

    //MODIFIES: this
    //EFFECTS: resets the internal naming scheme to 0
    public static void resetId() {
        id = 0;
    }

    //MODIFIES: this
    //EFFECTS: changes the title of this note
    public void changeTitle(String title) {
        this.title = title;
    }

    //EFFECTS: gets the title of the note
    public String getTitle() {
        return title;
    }

    //EFFECTS: gets the text of the note
    public String getText() {
        return text;
    }

    //EFFECTS: gets the organization colour of this note
    public Color getOrganizationColour() {
        return organizationColour;
    }

    //MODIFIES: this
    //EFFECTS: updates this note with a new description
    public void addContent(String text) {
        this.text = text;
    }

    //MODIFIES: this
    //EFFECTS: updates this note with an updated folder category using the CATEGORY_
    //         constants
    public void changeOrganization(int folder) {
        this.folder = folder;
    }

    //MODIFIES: this
    //EFFECTS: updates this note with an updated organization colour
    public void changeOrganization(Color c) {
        this.organizationColour = c;
    }

    //EFFECTS: gets the category of this note, as defined by the CATEGORY_ constants
    public int getCategory() {
        return folder;
    }

    //EFFECTS: gets the most specialized NoteType for this note
    public abstract NoteType getNoteType();

    @Override
    //EFFECTS: converts this object into a JSONObject
    public JSONObject toJson() {
        JSONObject jsonNote = new JSONObject();
        jsonNote.put(PersistenceHelpers.NOTE_NAME, title);
        jsonNote.put(PersistenceHelpers.NOTE_DATA, text);
        jsonNote.put(PersistenceHelpers.NOTE_TYPE, getNoteType().toString());

        return jsonNote;
    }
}
