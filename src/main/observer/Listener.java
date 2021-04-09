package observer;

/**
 * A Listener for the ObservableFrame class.
 */
public interface Listener {
    //EFFECTS: responds to an event with the given return value
    void update(Object returnValue);
}
