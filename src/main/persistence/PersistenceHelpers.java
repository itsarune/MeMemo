package persistence;

/**
 * PersistenceHelpers is a set of constants that act as the "keys" for the JsonNotebookBuilder and JsonNotebookWriter.
 */
public class PersistenceHelpers {
    private PersistenceHelpers() { }

    public static final String NOTEBOOK_NAME            = "notebook name";
    public static final String NOTES                    = "notes";
    public static final String NOTE_NAME                = "note name";
    public static final String NOTE_DATA                = "note description";
    public static final String REMINDER_NOTE_REMINDER   = "reminder";
    public static final String TASKLIST_SUBTASKS        = "subtasks";
    public static final String SUBTASK                  = "subtask";
    public static final String NOTE_TYPE                = "type";
    public static final String REMINDER_NOTE_HAS_RUN    = "has run?";

    public static final String DATA_DIRECTORY           = "./data/";
}
