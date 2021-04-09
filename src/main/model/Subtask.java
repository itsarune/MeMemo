package model;

import org.json.JSONObject;
import persistence.PersistenceHelpers;
import persistence.Writable;

/**
 * Subtask is a structure that contains a small description of a subtask for a Note.
 */
public class Subtask implements Writable {
    private String task;

    //EFFECTS: creates a subtask with the given task description
    public Subtask(String task) {
        this.task = task;
    }

    //EFFECTS: gets the task description for this task
    public String getTask() {
        return task;
    }

    //MODIFIES: this
    //EFFECTS: changes this subtask's task description
    public void changeTaskName(String task) {
        this.task = task;
    }

    //EFFECTS: converts this object into a JSONObject
    @Override
    public JSONObject toJson() {
        JSONObject jsonSubtask = new JSONObject();
        jsonSubtask.put(PersistenceHelpers.SUBTASK, task);

        return jsonSubtask;
    }
}
