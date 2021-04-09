package persistence;

import model.*;

import org.json.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * JsonNotebookBuilder builds a CollectionOfNotes from a json file.
 *
 * It uses code from the JsonSerializationDemo project.
 */
public class JsonNotebookBuilder {
    private String dataFile;

    //EFFECTS: creates a builder object with the given source file to build from
    public JsonNotebookBuilder(String source) {
        dataFile = source;
    }

    //EFFECTS: returns a CollectionOfNotes built from the data file
    //         throws IOExeception if the file cannot be found
    public CollectionOfNotes read() throws IOException {
        String data = readFile();
        JSONObject jsonData = new JSONObject(data);
        return parseNotes(jsonData);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile() throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(dataFile), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    //EFFECTS: parses data from the JSONObject and creates a CollectionOfNotes object
    private CollectionOfNotes parseNotes(JSONObject data) {
        CollectionOfNotes notebook = new CollectionOfNotes(data.getString(PersistenceHelpers.NOTEBOOK_NAME));
        JSONArray notes = data.getJSONArray(PersistenceHelpers.NOTES);
        for (Object note : notes) {
            notebook.addNote(convertJsonToNote((JSONObject) note));
        }
        return notebook;
    }

    //EFFECTS: reads the given JSONObject and returns constructs a respective new NoteBase object
    private NoteBase convertJsonToNote(JSONObject jsonNote) {
        NoteType type = NoteType.valueOf(jsonNote.getString(PersistenceHelpers.NOTE_TYPE));
        switch (type) {
            case REMINDER_NOTE:
                return new ReminderNote(jsonNote);
            case TASKLIST_NOTE:
                return new TasklistNote(jsonNote);
            default :
                return new Note(jsonNote);
        }
    }
}
