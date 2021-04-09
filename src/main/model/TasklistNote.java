package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.PersistenceHelpers;

import java.util.ArrayList;
import java.util.List;

/**
 * TasklistNote is a ReminderNote that contains multiple subtasks.
 */
public class TasklistNote extends ReminderNote {
    private List<Subtask> subtasks;

    //EFFECTS: creates a note with a default title and empty description
    public TasklistNote() {
        super();
        subtasks = new ArrayList<>();
    }

    //EFFECTS: creates a note with the given title and empty description
    public TasklistNote(String title) {
        super(title);
        subtasks = new ArrayList<>();
    }

    //EFFECTS: creates a TasklistNote from a given JSONObject
    public TasklistNote(JSONObject json) {
        super(json);
        subtasks = new ArrayList<>();
        JSONArray subtaskArray = json.getJSONArray(PersistenceHelpers.TASKLIST_SUBTASKS);
        for (Object s : subtaskArray) {
            String subtask = ((JSONObject) s).getString(PersistenceHelpers.SUBTASK);
            subtasks.add(new Subtask(subtask));
        }
    }

    //EFFECTS: Gets all subtasks in this tasklist
    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    //REQUIRES: the index is not larger than the number of subtasks in this Note
    //EFFECTS: Gets a particular subtask at the 0-based index
    public Subtask getSubtask(int index) {
        return subtasks.get(index);
    }

    //MODIFIES: this
    //EFFECTS: adds a subtask to this note's subtask list
    public void addSubtask(Subtask s) {
        subtasks.add(s);
    }

    //EFFECTS: returns true if the subtask exists in the task list
    public boolean containsSubtask(Subtask s) {
        return subtasks.contains(s);
    }

    //EFFECTS: returns the number of subtasks in this tasklist
    public int numberOfSubtasks() {
        return subtasks.size();
    }

    //EFFECTS: returns the note type as a tasklist note
    @Override
    public NoteType getNoteType() {
        return NoteType.TASKLIST_NOTE;
    }

    //EFFECTS: converts this object into a JSONObject
    @Override
    public JSONObject toJson() {
        JSONObject tasklistJsonNote = super.toJson();
        addJsonSubtasks(tasklistJsonNote);

        return tasklistJsonNote;
    }

    //MODIFIES: jsonNote
    //EFFECTS: adds this note's subtasks to the given JSONObject
    private void addJsonSubtasks(JSONObject jsonNote) {
        JSONArray subtasksJson = new JSONArray();
        for (Subtask subtask : subtasks) {
            subtasksJson.put(subtask.toJson());
        }

        jsonNote.put(PersistenceHelpers.TASKLIST_SUBTASKS, subtasksJson);
    }
}
