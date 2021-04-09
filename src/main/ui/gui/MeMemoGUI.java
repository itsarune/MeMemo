package ui.gui;

import model.*;
import observer.*;
import persistence.*;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * The Main GUI Class for MeMemo.
 */
public class MeMemoGUI extends JFrame implements Listener, NewNoteListener {
    public static final int HEIGHT = 540;
    public static final int WIDTH = 960;
    public static final int JFIELD_WIDTH = 15;
    public static final int JFIELD_HEIGHT = 10;
    private static final String MENU_OPTIONS_STRING = "Options";
    public static final String SOUND_CLIP_DIR = "./data/narcos.wav";
    private static final String INTRO_TEXT = "Load a notebook or create a new one in the Options menu!";
    private static final String ADD_NOTE_BUTTON = "Add a note";
    private static final int BUTTON_HEIGHT = 20;
    public static final String SUBTASKS_LABEL_TEXT = "Subtasks: ";

    private MeMemoGUI gui;
    private CollectionOfNotes notebook = new CollectionOfNotes("untitled");
    private ReminderManager reminderManager;
    private JSplitPane splitPane;
    private NotesJList noteJList;
    private JMenuBar menuBar = new JMenuBar();
    private JMenu menu;
    private JLabel infoDisplayData;
    private JPanel notePanePanel;
    private JScrollPane listNoteNames;
    private JScrollPane noteDataPane;
    private JPanel view = new JPanel(new GridBagLayout());
    private SubtaskJList subtaskJlist;
    private GridBagConstraints constraints = new GridBagConstraints();
    private JMenuItem loadNotebook  = new LoadNotebookButton("Load notebook");
    private JMenuItem saveNotebook  = new SaveNotebookButton("Save notebook");
    private JMenuItem quit          = new QuitButton("Quit program");
    private Dimension preferredPaneSize = new Dimension(WIDTH / 2 - 40, HEIGHT - 20);
    private final JFileChooser chooser = new JFileChooser(PersistenceHelpers.DATA_DIRECTORY);
    private NoteBase currentlyViewingNote;

    //EFFECTS: creates the GUI and starts the thread for managing reminders
    public MeMemoGUI() {
        super("MeMemo");

        initializeDisplay();
        initMenu();

        reminderManager = new ReminderManager(this);
        reminderManager.startTaskScheduler();

        gui = this;
        getContentPane().add(splitPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        pack();
        setVisible(true);

        loadNotes();
    }

    //MODIFIES: this
    //EFFECTS: initializes the menu bar
    private void initMenu() {
        menu = new JMenu(MENU_OPTIONS_STRING);
        menuBar.add(menu);
        menu.add(loadNotebook);
        menu.add(saveNotebook);
        menu.add(quit);

        setJMenuBar(menuBar);
    }

    //MODIFIES: this
    //EFFECTS: initializes the GUI display
    private void initializeDisplay() {
        listNoteNames = new JScrollPane(view);
        notePanePanel = new JPanel();
        noteDataPane = new JScrollPane(notePanePanel);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listNoteNames, noteDataPane);
        splitPane.setDividerLocation(WIDTH / 2);

        view.setPreferredSize(preferredPaneSize);
        listNoteNames.setPreferredSize(preferredPaneSize);
        noteDataPane.setPreferredSize(preferredPaneSize);
        splitPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        infoDisplayData = new JLabel(INTRO_TEXT);
        infoDisplayData.setHorizontalAlignment(JLabel.CENTER);
        notePanePanel.add(infoDisplayData);
    }

    //MODIFIES: this
    //EFFECTS: displays every note in the notebook
    public void loadNotes() {
        GridBagConstraints noteConstraints = new GridBagConstraints();
        noteConstraints.gridx = 0;
        noteConstraints.gridy = 0;

        if (!notebook.isEmpty()) {
            noteJList = new NotesJList();
            noteJList.setPreferredSize(new Dimension(view.getWidth(), HEIGHT - BUTTON_HEIGHT - 40));
            view.add(noteJList, noteConstraints);
        } else {
            view.add(new JLabel("No notes here."), noteConstraints);
        }

        noteConstraints.gridy++;
        JButton addNote = getAddNote();
        view.add(addNote, noteConstraints);

        getContentPane().revalidate();
        getContentPane().repaint();
    }

    //EFFECTS: returns a "Add note" button
    private JButton getAddNote() {
        JButton addNote = new JButton(ADD_NOTE_BUTTON);
        addNote.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new NoteCreator().addNewNoteObserver(gui);
            }
        });
        addNote.setPreferredSize(new Dimension(view.getWidth(), BUTTON_HEIGHT));
        return addNote;
    }

    //MODIFIES: this
    //EFFECTS: displays general note information
    private void createGeneralNote(JPanel notePanel, NoteBase note) {
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridx = 0;
        constraints.gridy = 0;
        notePanel.add(new JLabel("Note name: "), constraints);
        constraints.gridy++;
        notePanel.add(new JLabel("Description: "), constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_START;
        JTextField noteNameField = new JTextField(note.getTitle(), JFIELD_WIDTH);
        notePanel.add(noteNameField, constraints);
        constraints.gridy++;
        JTextArea noteDescriptionArea = new JTextArea(note.getText(), JFIELD_HEIGHT, JFIELD_WIDTH);
        notePanel.add(noteDescriptionArea, constraints);
    }

    //MODIFIES: this
    //EFFECTS: creates the display fields on the note panel
    private void createReminderField(JPanel notePanel, ReminderNote rn) {
        constraints.gridx = 0;
        constraints.gridy++;
        notePanel.add(new JLabel("Reminder: "), constraints);
        constraints.gridx++;
        notePanel.add(new JLabel(rn.toString()), constraints);
        constraints.gridx++;
        JButton changeReminder = new JButton("Change");
        changeReminder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateReminder(rn);
            }
        });
        notePanel.add(changeReminder, constraints);
    }

    //MODIFIES: this
    //EFFECTS: hides this window and creates a popup for changing the reminder
    private void updateReminder(ReminderNote rn) {
        DateAndTimeChooser chooser = new DateAndTimeChooser();
        chooser.addListenerObserver(this);
        currentlyViewingNote = rn;
        setVisible(false);
    }

    //MODIFIES: this
    //EFFECTS: creates the subtask layout
    private void createSubtasks(JPanel notePanePanel, TasklistNote note) {
        createSubtaskList(notePanePanel, note);
        addButtonsForSubtask(notePanePanel, note);
    }

    //MODIFIES: this
    //EFFECTS: creates the buttons for handling changing subtask information
    private void addButtonsForSubtask(JPanel notePanePanel, TasklistNote note) {
        JButton addNoteButton = subtaskJlist.getAddButton();
        JButton changeNoteButton = subtaskJlist.getChangeSubButton();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(addNoteButton);
        buttonPanel.add(changeNoteButton);

        constraints.gridx++;
        notePanePanel.add(buttonPanel, constraints);
    }

    //MODIFIES: this
    //EFFECTS: creates the subtask display
    private void createSubtaskList(JPanel notePanel, TasklistNote note) {
        constraints.gridx = 0;
        constraints.gridy++;
        JLabel subtaskLabel = new JLabel(SUBTASKS_LABEL_TEXT);
        notePanel.add(subtaskLabel, constraints);
        constraints.gridx++;

        String[] subtaskList = note.getSubtasks().stream()
                .map(s -> s.getTask())
                .collect(Collectors.toList())
                .toArray(new String[] {});
        JPanel flowScrollPaneView = new JPanel(new FlowLayout());
        JScrollPane subtaskPanel = new JScrollPane(flowScrollPaneView);
        subtaskJlist = new SubtaskJList(subtaskList, this, notebook.getIndex(note));
        constraints.anchor = GridBagConstraints.CENTER;
        subtaskJlist.setPrototypeCellValue(getPixelFieldPrototype());
        flowScrollPaneView.add(subtaskJlist);
        notePanel.add(subtaskPanel, constraints);
        constraints.anchor = GridBagConstraints.LINE_START;
    }

    //EFFECTS: gets a prototype string with the class's JFIELD_WIDTH
    private String getPixelFieldPrototype() {
        char[] m = new char[JFIELD_WIDTH];
        for (int i = 0; i < JFIELD_WIDTH; ++i) {
            m[i] = 'm';
        }
        return String.copyValueOf(m);
    }

    //MODIFIES: this
    //EFFECTS: updates note data pane based with given index
    private void updateNoteDataPane(int notebookIndex) {
        NoteBase note = notebook.getNote(notebookIndex);
        NoteType type = note.getNoteType();

        JPanel noteDataPanel = new JPanel(new GridBagLayout());
        createsNoteFields(note, type, noteDataPanel);

        notePanePanel.removeAll();
        notePanePanel.add(noteDataPanel);

        pack();
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    //MODIFIES: this
    //EFFECTS: creates general note fields
    private void createsNoteFields(NoteBase note, NoteType type, JPanel noteDataPanel) {
        createGeneralNote(noteDataPanel, note);

        switch (type) {
            case REMINDER_NOTE:
                createReminderField(noteDataPanel, (ReminderNote) note);
                break;
            case TASKLIST_NOTE:
                createReminderField(noteDataPanel, (TasklistNote) note);
                createSubtasks(noteDataPanel, (TasklistNote) note);
                break;
        }
    }

    //getter
    public CollectionOfNotes getNotebook() {
        return notebook;
    }

    //EFFECTS: displays a reminder notification
    public void receiveReminder(ReminderNote rn) {
        File in = new File(SOUND_CLIP_DIR);
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(in);
            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip audioLine = (Clip) AudioSystem.getLine(info);
            audioLine.open(audioInputStream);
            audioLine.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        JOptionPane.showMessageDialog(this, rn.getTitle() + " is due.\nReminder set for: " + rn.toString());
    }

    //MODIFIES: this
    //EFFECTS: updates the reminder on the currently viewed note and sets it to run
    @Override
    public void update(Object returnValue) {
        LocalDateTime reminder = (LocalDateTime) returnValue;
        System.out.println("MeMemoGUI Debug - currently set reminder time: " + returnValue.toString());
        ReminderNote rn = (ReminderNote) currentlyViewingNote;
        rn.changeReminder(reminder);
        rn.setHasRun(false);
        setVisible(true);
        updateNoteDataPane(notebook.getIndex(currentlyViewingNote));
        getContentPane().revalidate();
    }

    //MODIFIES: this
    //EFFECTS: adds notes to the notebook and GUI
    @Override
    public void newNoteCreated(NoteBase note) {
        addNote(note);
        setVisible(true);
    }

    //MODIFIES: this
    //EFFECTS: adds a note to the notebook display and GUI
    private void addNote(NoteBase note) {
        notebook.addNote(note);
        if (noteJList == null) {
            loadNotes();
        } else {
            noteJList.add(note.getTitle());
        }

        getContentPane().revalidate();
        getContentPane().repaint();
    }

    //MODIFIES: system
    //EFFECTS: saves the notebook to the drive
    private void saveNotebook(String dir) {
        JsonNotebookWriter writer = new JsonNotebookWriter(dir);
        try {
            writer.open();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("DEBUG - " + "Can't write to file");
        }
        writer.write(notebook);
        writer.close();
    }

    /**
     * A JMenuItem for loading a notebook.
     */
    class LoadNotebookButton extends JMenuItem implements ActionListener {
        public LoadNotebookButton(String name) {
            super(name);
            addActionListener(this);
        }

        //MODIFIES: MeMemoGUI
        //EFFECTS: displays a file chooser and loads notebook
        @Override
        public void actionPerformed(ActionEvent e) {
            int returnVal = chooser.showOpenDialog(splitPane);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                JsonNotebookBuilder reader = new JsonNotebookBuilder(file.getPath());
                try {
                    notebook = reader.read();
                    loadNotes();
                } catch (IOException ioException) {
                    chooser.showDialog(this, "Unable to open the file. "
                            + "Check your read/write permissions.");
                }
            }
        }
    }

    /**
     * A JMenuItem for saving the notebook.
     */
    class SaveNotebookButton extends JMenuItem implements ActionListener {
        public SaveNotebookButton(String name) {
            super(name);
            addActionListener(this);
        }

        //MODIFIES: system
        //EFFECTS: saves the notebook to the system
        @Override
        public void actionPerformed(ActionEvent e) {
            saveFromChooser();
        }
    }

    //MODIFIES: this, system
    //EFFECTS: prompts the user for a notebook name and saves notebook
    private void saveFromChooser() {
        String newName = JOptionPane.showInputDialog(gui, "Change notebook name", notebook.getNotebookName());
        notebook.changeNotebookName(newName);
        File f = new File(PersistenceHelpers.DATA_DIRECTORY + notebook.getNotebookName() + ".json");
        chooser.setSelectedFile(f);
        int option = chooser.showSaveDialog(gui);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            saveNotebook(file.getPath());
        }
    }

    /**
     * A JMenuItem for quitting the notebook.
     */
    class QuitButton extends JMenuItem implements ActionListener {
        public QuitButton(String name) {
            super(name);
            addActionListener(this);
        }

        //MODIFIES: this
        //EFFECTS: prompts the user to save before exiting.
        @Override
        public void actionPerformed(ActionEvent e) {
            int n = JOptionPane.showConfirmDialog(null,
                    "Save notebook?");
            if (n == JOptionPane.YES_OPTION) {
                saveFromChooser();
                System.exit(0);
            } else if (n == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
        }
    }

    /**
     * A JList for displaying the notes in the notebook.
     */
    class NotesJList extends JList<String> implements ListSelectionListener {
        private DefaultListModel listModel;

        //MODIFIES: MeMemoGUI
        //EFFECTS: clears the existing note panel and initializes the JList
        public NotesJList() {
            super();

            initializesNotesJList();
            view.removeAll();
        }

        //EFFECTS: initializes this JList
        private void initializesNotesJList() {
            listModel = new DefaultListModel<>();
            for (String s : notebook.getAllNoteNames().toArray(new String [] {})) {
                listModel.addElement(s);
            }
            setModel(listModel);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setSelectedIndex(0);
            updateNoteDataPane(0);

            setPreferredSize(new Dimension(view.getWidth(), HEIGHT - 40));
            addListSelectionListener(this);
        }

        //MODIFIES: this
        //EFFECTS: updates the note data pane whenever the list selection has changed
        @Override
        public void valueChanged(ListSelectionEvent e) {
            updateNoteDataPane(getSelectedIndex());
        }

        //EFFECTS: adds a title to this JList
        public void add(String note) {
            listModel.addElement(note);
        }
    }
}
