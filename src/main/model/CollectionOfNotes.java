package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.PersistenceHelpers;
import persistence.Writable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CollectionOfNotes is a structure that contains all the notes that a user makes.
 */
public class CollectionOfNotes implements Writable {
    private Map<String, NoteBase> notes;
    private String notebookName;

    //EFFECTS: creates an empty notebook with the given notebook name
    public CollectionOfNotes(String notebookName) {
        notes = new LinkedHashMap<>();
        this.notebookName = notebookName;
    }


    //EFFECTS: returns all the notes that are in this notebook
    public List<NoteBase> getAllNotes() {
        List<NoteBase> notesList = new ArrayList<>();
        for (NoteBase note : notes.values()) {
            notesList.add(note);
        }
        return notesList;
    }

    //getter
    public List<String> getAllNoteNames() {
        return notes.values().stream().map(n -> n.getTitle()).distinct().collect(Collectors.toList());
    }

    //REQUIRES: note.getTitle() must be unique
    //MODIFIES: this
    //EFFECTS: adds the note to this notebook
    public void addNote(NoteBase note) {
        notes.put(note.getTitle(), note);
    }

    //MODIFIES: this
    //EFFECTS: updates the requested note with the new description, if the requested note doesn't exist, nothing
    //         happens
    public void updateNote(String noteName, String updatedText) {
        NoteBase note = getNote(noteName);
        if (note != null) {
            note.addContent(updatedText);
        }
    }

    //EFFECTS: gets the note from the notebook based on the note name, returns null if that note doesn't exist in the
    //         notebook
    public NoteBase getNote(String noteName) {
        return notes.get(noteName);
    }

    //EFFECTS: gets note at required 0-based index in notebook, returns null if out of bounds
    public NoteBase getNote(int index) {
        if (index >= size() || index < 0) {
            return null;
        }
        List<String> names = getAllNoteNames();
        return notes.get(names.get(index));
    }

    //EFFECTS: gets index of the NoteBase in the notebook, returns -1 if it's not in the notebook
    public int getIndex(NoteBase note) {
        List<NoteBase> allNotes = getAllNotes();
        return allNotes.indexOf(note);
    }

    //EFFECTS: returns true if this notebook is empty, false otherwise
    public boolean isEmpty() {
        return notes.isEmpty();
    }

    //MODIFIES: this
    //EFFECTS: replaces a note in the notebook with another note; DOES NOT preserve index of replaced note
    public void replaceNote(NoteBase replacedNote, NoteBase newNote) {
        notes.remove(replacedNote.getTitle());
        notes.put(newNote.getTitle(), newNote);
    }

    //EFFECTS: returns true if this notebook contains the given note
    public boolean contains(NoteBase note) {
        return notes.containsKey(note.getTitle());
    }

    //EFFECTS: returns the size of the notebook
    public int size() {
        return notes.size();
    }

    //EFFECTS: converts this object into a JSONObject
    @Override
    public JSONObject toJson() {
        JSONObject notebookJson = new JSONObject();
        notebookJson.put(PersistenceHelpers.NOTEBOOK_NAME, notebookName);
        notebookJson.put(PersistenceHelpers.NOTES, notesToJsonArray());

        return notebookJson;
    }

    //EFFECTS: returns the name of this notebook
    public String getNotebookName() {
        return notebookName;
    }

    //EFFECTS: converts the notes into this CollectionOfNotes into a JSONArray
    private JSONArray notesToJsonArray() {
        JSONArray notesArray = new JSONArray();
        for (NoteBase note : notes.values()) {
            notesArray.put(note.toJson());
        }

        return notesArray;
    }

    //setter for notebookName
    public void changeNotebookName(String newName) {
        this.notebookName = newName;
    }
}
