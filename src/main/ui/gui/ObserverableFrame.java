package ui.gui;

import model.NoteBase;
import observer.Listener;
import observer.NewNoteListener;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * ObservableFrame is a frame that can be observed to for events, allowing for the return of any Object or NoteBase
 * object.
 */
public abstract class ObserverableFrame extends JFrame {
    private Collection<Listener> listeners = new ArrayList<>();
    private Collection<NewNoteListener> noteListeners = new ArrayList<>();
    protected Object returnValue;

    public ObserverableFrame() {
        super();
    }

    //EFFECTS: creates a JFrame with the given name
    public ObserverableFrame(String name) {
        super(name);
    }

    //EFFECTS: notifies all Listener objects to an event
    public void notifyListenerObservers() {
        for (Listener listener : listeners) {
            listener.update(returnValue);
        }
    }

    //EFFECTS: notifies all NewNoteListeners to an event
    public void notifyNewNoteObservers(NoteBase note) {
        for (NewNoteListener nnl : noteListeners) {
            nnl.newNoteCreated(note);
        }
    }

    //MODIFIES: this
    //EFFECTS: adds a Listener object to the list of Listeners
    public void addListenerObserver(Listener l) {
        listeners.add(l);
    }

    //MODIFIES: this
    //EFFECTS: adds a NewNoteListener to the list of NewNoteListeners
    public void addNewNoteObserver(NewNoteListener nnl) {
        noteListeners.add(nnl);
    }
}
