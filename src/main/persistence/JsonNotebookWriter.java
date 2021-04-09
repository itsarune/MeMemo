package persistence;

import model.CollectionOfNotes;
import org.json.JSONObject;
import java.io.*;

/**
 * JsonNotebookWriter writes CollectionOfNotes data into storage using JSON.
 *
 * It uses a significant amount of code from the JsonSerializationDemo project.
 */
public class JsonNotebookWriter {
    public static final int TAB = 4;
    private PrintWriter writer;
    private String destination;

    //EFFECTS: creates a writer object with a given destination directory
    public JsonNotebookWriter(String destination) {
        this.destination = destination;
    }

    //MODIFIES: this;
    //EFFECTS: opens the file at a given source directory
    //         throws FileNotFoundException if the writer is unable to write into the file (most likely due to system
    //         permission errors
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(destination);
    }

    //REQUIRES: open() to be called first
    //MODIFIES: this
    //EFFECTS: writes the CollectionOfNotes into the file
    public void write(CollectionOfNotes notebook) {
        JSONObject json = notebook.toJson();
        saveToFile(json.toString(TAB));
    }

    //MODIFIES: this
    //EFFECTS: writes string to file
    public void saveToFile(String json) {
        writer.print(json);
    }

    //MODIFIES: this
    //EFFECTS: closes the stream and releases system resources
    public void close() {
        writer.close();
    }
}
