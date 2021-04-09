package observer;

import model.NoteBase;

/**
 * A Listener for the ObservableFrame class that responds to a note event.
 */
public interface NewNoteListener {
    //EFFECTS: responds to an event with the given note
    void newNoteCreated(NoteBase note);
}
