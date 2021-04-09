package ui;

import model.CollectionOfNotes;
import model.*;
import persistence.JsonNotebookBuilder;
import persistence.JsonNotebookWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * MeMemoConsole is a console-based interface for the notes application.
 */
public class MeMemoConsole {
    private CollectionOfNotes notebook;
    private static final String src = "./data/";
    private String dir;

    //EFFECTS: starts the console interface
    public MeMemoConsole() {
        System.out.println("MeMemo is a memo application.");
        displayOrCreateNotebook();
    }

    //EFFECTS: displays user options on whether to load or create a new notebook.
    private void displayOrCreateNotebook() {
        while (true) {
            System.out.println("\nCreate a new notebook using n, load a new notebook with l or q to quit");
            String input = getValidInput(Arrays.asList("n", "l", "q"));
            switch (input) {
                case "n":
                    initNotebook();
                    break;
                case "l" :
                    selectNotebooks();
                    break;
                case "q" :
                    System.exit(0);
            }
            displayGeneralOptions();
        }
    }

    //MODIFIES: this
    //EFFECTS: asks the user for a notebook name and initializes this object's notebook
    private void initNotebook() {
        System.out.println("\nPlease enter a title for your notebook: ");
        while (true) {
            String notebookName = getUserInput();
            dir = src + notebookName + ".json";
            JsonNotebookWriter writer = new JsonNotebookWriter(src + notebookName + ".json");
            try {
                writer.open();
                notebook = new CollectionOfNotes(notebookName);
                writer.write(notebook);
                writer.close();
                return;
            } catch (FileNotFoundException e) {
                System.out.println("That is an invalid notebook name. "
                        + "Please only use uppercase, lowercase and numbers");
            }
        }
    }

    //MODIFIES: this
    //EFFECTS: asks the user to select a notebook and loads it
    private void selectNotebooks() {
        File [] files = filesInDirectory();
        System.out.println("Select which notebook you would like to load.");
        ArrayList<String> validInputs = new ArrayList<>();
        for (int i = 0; i < files.length; ++i) {
            String fileName = files[i].toString();
            System.out.println("\t" + i + ": " + fileName.substring(src.length(), fileName.length() - 5));
            validInputs.add(String.valueOf(i));
        }
        String input = getValidInput(validInputs);
        loadNotebook(files[(Integer.parseInt(input))].getPath());
    }

    //MODIFIES: this
    //EFFECTS: loads the selected json file path as this object's notebook
    private void loadNotebook(String path) {
        JsonNotebookBuilder reader = new JsonNotebookBuilder(path);
        try {
            notebook = reader.read();
        } catch (IOException ioe) {
            System.out.println("Unable to read the file. Cannot continue.");
            System.exit(1);
        }
        dir = path;
    }

    //EFFECTS: returns all the .json files in the data directory
    private File [] filesInDirectory() {
        File directoryPath = new File(src);
        FilenameFilter textNameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                return name.endsWith(".json");
            }
        };

        return directoryPath.listFiles(textNameFilter);
    }

    //EFFECTS: displays the main menu for the console interface
    private void displayGeneralOptions() {
        while (true) {
            System.out.println("\n---");
            System.out.println("Here are some things that you may do: ");
            System.out.println("Press v to view all your notes, a to add a note, c to change a note,"
                    + " s to save the notebook or q to quit to the main screen");
            String input = getValidInput(Arrays.asList("v", "a", "c", "s", "q"));
            if (controlNotebookInput(input)) {
                return;
            }
        }
    }

    //REQURIES: input must be one-of: "v", "a", "c", "s" or "q"
    //MODIFIES: this
    //EFFECTS: guides user to respective part of program depending on user input
    //         returns true if the user wants to quit the program and false otherwise
    private boolean controlNotebookInput(String input) {
        switch (input) {
            case "v":
                viewAllNotes();
                break;
            case "a":
                addNote();
                break;
            case "c":
                changeNote();
                break;
            case "s" :
                saveNotebook();
                break;
            default :
                quitSequence();
                return true;
        }
        return false;
    }

    //EFFECTS: prompts the user to save the notebook before quitting
    private void quitSequence() {
        System.out.println("Do you want to save the notebook before quitting (y/n)?");
        String input = getValidInput(Arrays.asList("y", "n"));
        if (input.equals("y")) {
            saveNotebook();
        }
    }

    //EFFECTS: display all notes on the console interface
    private void viewAllNotes() {
        List<NoteBase> notes = notebook.getAllNotes();
        if (notes.isEmpty()) {
            System.out.println("No notes to view in this notebook.");
            return;
        }
        for (NoteBase note : notes) {
            System.out.println("\n\n---BEGIN NOTE---\n");
            NoteType type = note.getNoteType();
            switch (type) {
                case REMINDER_NOTE :
                    displayNote((ReminderNote) note);
                    break;
                case TASKLIST_NOTE :
                    displayNote((TasklistNote) note);
                    break;
                default :
                    displayNote(note);
            }
            System.out.println("\n---END NOTE---\n\n");
        }
    }

    //EFFECTS: saves the notebook in the data directory
    private void saveNotebook() {
        JsonNotebookWriter writer = new JsonNotebookWriter(dir);
        try {
            writer.open();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to the computer. Check your permissions. Cannot continue");
            System.exit(1);
        }
        writer.write(notebook);
        writer.close();
        System.out.println("Success! File saved at: " + dir);
    }

    //EFFECTS: display all subtasks in the TasklistNote
    private void displaySubtasks(TasklistNote tln) {
        List<Subtask> subtasks = tln.getSubtasks();
        if (!subtasks.isEmpty()) {
            System.out.println("SUBTASKS:");
            for (int i = 0; i < subtasks.size(); ++i) {
                System.out.println((i + 1) + ": " + subtasks.get(i).getTask());
            }
        }
    }

    //EFFECTS: displays the ReminderNote on the console
    private void displayNote(ReminderNote rn) {
        displayNote((NoteBase) rn);
        System.out.println("---");
        if (rn.hasReminder()) {
            System.out.println("REMINDER: " + rn.getReminder());
        } else {
            System.out.println("No reminder set.");
        }
    }

    //EFFECTS: displays the note title and description on the console
    private void displayNote(NoteBase note) {
        System.out.println("NOTE TITLE: " + note.getTitle());
        System.out.println("---");
        System.out.println("NOTE DESCRIPTION: ");
        System.out.println(note.getText());
    }

    //EFFECTS: displays the Tasklist Note on the console
    private void displayNote(TasklistNote tln) {
        displayNote((ReminderNote) tln);
        System.out.println("---");
        displaySubtasks(tln);
    }

    //EFFECTS: returns an initialized note with a given NoteType.
    private NoteBase initNote(NoteType type) {
        NoteBase note;
        switch (type) {
            case REMINDER_NOTE:
                note = new ReminderNote();
                break;
            case TASKLIST_NOTE:
                note = new TasklistNote();
                break;
            default :
                note = new Note();
        }
        return note;
    }

    //MODIFIES: this
    //EFFECTS: adds a note to this collection of notes
    private void addNote() {
        NoteType type = chooseNoteType();
        NoteBase note = initNote(type);
        updateNewFields(type, note);
        notebook.addNote(note);
    }

    //MODIFIES: note
    //EFFECTS: updates the respective fields of the given note depending on the given note type
    private void updateFields(NoteType type, NoteBase note) {
        updateNoteTitleAndText(note);
        switch (type) {
            case TASKLIST_NOTE:
                addSubtasks((TasklistNote) note);
            case REMINDER_NOTE:
                addReminder((ReminderNote) note);
                break;
            default :
                addReminder((Note) note);
        }
    }

    //REQUIRES: the NoteBase object has just been initialized
    //MODFIES: note
    //EFFECTS: updates the respective fields of the given newly created note based on the given type
    private void updateNewFields(NoteType type, NoteBase note) {
        updateNoteTitleAndText(note);
        switch (type) {
            case TASKLIST_NOTE:
                addSubtasks((TasklistNote) note);
            case REMINDER_NOTE:
                addReminder((ReminderNote) note);
                break;
        }
    }

    //MODIFIES: tln
    //EFFECTS: add subtasks to the given Tasklist Note
    private void addSubtasks(TasklistNote tln) {
        System.out.println("Add a subtask by pressing a or change a subtask by pressing c. "
                + "If you've finished, press q");
        while (true) {
            String input = getValidInput(Arrays.asList("a", "c", "q"));
            switch (input) {
                case "a" :
                    addSubtask(tln);
                    break;
                case "c" :
                    changeSubtask(tln);
                    break;
                default:
                    return;
            }
        }
    }

    //MODIFIES: tln
    //EFFECTS: add one subtask to the given Tasklist Note
    private void addSubtask(TasklistNote tln) {
        System.out.println("Enter subtask: ");
        Subtask newSubtask = new Subtask(getUserInput());
        tln.addSubtask(newSubtask);
    }

    //MODIFIES: tln
    //EFFECTS: update the subtasks of the given Tasklist Note using the user interface
    private void changeSubtask(TasklistNote tln) {
        if (tln.numberOfSubtasks() > 0) {
            Subtask subtask = getSubtask(tln);
            System.out.println("Update the subtask: ");
            String newTask = getUserInput();
            subtask.changeTaskName(newTask);
        } else {
            System.out.println("No subtasks to edit.");
        }
    }

    //EFFECTS: get a particular subtask using the user interface
    private Subtask getSubtask(TasklistNote tln) {
        displaySubtasks(tln);
        System.out.println("Enter the number of the subtask you would like to modify.");
        List<String> validInputs = new ArrayList<>();
        List<Subtask> subtasks = tln.getSubtasks();
        for (int i = 1; i <= subtasks.size(); ++i) {
            validInputs.add(String.valueOf(i));
        }
        int input = new Integer(getValidInput(validInputs));
        return subtasks.get(input - 1);
    }

    //MODIFIES: rn
    //EFFECTS: add a reminder to the given ReminderNote
    private void addReminder(ReminderNote rn) {
        System.out.println("Set a reminder? (y/n): ");
        String input = getValidInput(Arrays.asList("y", "n"));
        if ("y".equals(input)) {
            setTime(rn);
        } else {
            rn.changeReminder(null);
        }
    }

    //MODIFIES: n
    //EFFECTS: adds a reminder to the given note
    private void addReminder(Note n) {
        System.out.println("Set a reminder? (y/n): ");
        String input = getValidInput(Arrays.asList("y", "n"));
        if ("y".equals(input)) {
            ReminderNote rn = new ReminderNote(n);
            setTime(rn);
            notebook.replaceNote(n, rn);
        }
    }

    //MODIFIES: n
    //EFFECTS: sets a reminder to the given note
    private void setTime(ReminderNote n) {
        LocalDateTime time = getDateTime();
        System.out.println("Setting reminder for " + time.toString());
        n.changeReminder(time);
    }

    //EFFECTS: returns an integer from the console interface
    private int getInteger() {
        Scanner s = new Scanner(System.in);
        while (true) {
            try {
                int i = s.nextInt();
                return i;
            } catch (InputMismatchException ime) {
                System.out.println("Please enter an integer.");
            }
        }
    }

    //EFFECTS: returns a time in the future from the user's console interface
    private LocalDateTime getDateTime() {
        LocalDateTime time = LocalDateTime.now();
        System.out.println("It is currently " + time.toString());
        System.out.println("Enter how many days from now ");
        time = time.plusDays(getInteger());
        System.out.println("Enter how many hours from now ");
        time = time.plusHours(getInteger());
        System.out.println("Enter how many minutes from now ");
        time = time.plusMinutes(getInteger());
        return time;
    }

    //EFFECTS: returns a type of note that user has chosen from console interface
    private NoteType chooseNoteType() {
        System.out.println("There are several different kinds of notes: ");
        NoteType[] allAvailableTypes = NoteType.values();
        ArrayList<String> validInputs = new ArrayList<>();
        for (int i = 0; i < NoteType.values().length; ++i) {
            System.out.println(i + ": " + allAvailableTypes[i].toString());
            validInputs.add(String.valueOf(i));
        }
        System.out.print("Enter a note type. ");
        String input = getValidInput(validInputs);
        return NoteType.values()[new Integer(input)];
    }

    //EFFECTS: gets a user's text input from the console
    private String getUserInput() {
        Scanner s = new Scanner(System.in);
        return s.nextLine();
    }

    //MODIFIES: this
    //EFFECTS: updates the fields of a note in the notebook using the user interface
    private void changeNote() {
        if (notebook.isEmpty()) {
            System.out.println("No notes exist");
            return;
        }
        NoteBase note = chooseNote();
        updateFields(note.getNoteType(), note);
    }

    //EFFECTS: returns a note from this notebook using the user interface
    private NoteBase chooseNote() {
        System.out.println("Choose the corresponding note to edit: ");
        List<NoteBase> allNotes = notebook.getAllNotes();
        ArrayList<String> validInputs = new ArrayList<>();
        for (int i = 0; i < allNotes.size(); ++i) {
            System.out.println(i + ": " + allNotes.get(i).getTitle());
            validInputs.add(String.valueOf(i));
        }
        System.out.println("Enter which note to edit: ");
        String input = getValidInput(validInputs);
        return allNotes.get(new Integer(input));
    }

    //MODIFIES: note
    //EFFECTS: updates the title and text fields of the note; the title is only updated if the user input is nonempty.
    private void updateNoteTitleAndText(NoteBase note) {
        System.out.println("Enter a note title: ");
        String prospectiveTitle = getUserInput();
        if (!prospectiveTitle.isEmpty()) {
            note.changeTitle(prospectiveTitle);
        }
        System.out.println("Add a description to your note ");
        String description = getUserInput();
        note.addContent(description);
    }

    //EFFECTS: returns a valid user input; the valid inputs is given by the list passed in as the parameter
    private String getValidInput(List<String> valids) {
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.print("Press ");
            for (int i = 0; i < valids.size() - 1; ++i) {
                System.out.print(valids.get(i) + ", ");
            }
            if (valids.size() > 1) {
                System.out.println("or " + valids.get(valids.size() - 1) + " and Enter: ");
            } else {
                System.out.println(valids.get(valids.size() - 1) + " and Enter: ");
            }
            String input = s.nextLine();
            if (valids.contains(input)) {
                return input;
            }
            System.out.println("Invalid input.");
        }
    }
}
