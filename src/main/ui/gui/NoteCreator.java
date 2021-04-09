package ui.gui;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A NoteCreator window that returns a note via a Listener.
 */
public class NoteCreator extends ObserverableFrame implements ActionListener {
    private NoteBase note;
    private JPanel panel = new JPanel(new GridBagLayout());
    private GridBagConstraints constraints = new GridBagConstraints();
    private JTextField noteNameField;
    private JTextArea noteDescriptionArea;
    private JSpinner typeSpinner;

    //MODIFIES: this
    //EFFECTS: creates a NoteCreator windows
    public NoteCreator() {
        super("Note Creator");
        setSize(MeMemoGUI.WIDTH / 2, MeMemoGUI.HEIGHT / 2);

        createNote();

        pack();
        setVisible(true);
    }

    //MODIFIES: this
    //EFFECTS: initializes the display
    private void createNote() {
        add(panel);
        constraints.gridy = 0;
        addTypeSpinner();
        addNoteTitle();
        addNoteDescription();
        addConfirmButton();
    }

    //MODIFIES: this
    //EFFECTS: adds a spinner for the NoteType
    private void addTypeSpinner() {
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridx = 0;
        JLabel allTypes = new JLabel("Choose a note type: ");
        panel.add(allTypes, constraints);

        constraints.anchor = GridBagConstraints.LINE_START;
        SpinnerListModel noteTypes = new SpinnerListModel(NoteType.values());
        typeSpinner = new JSpinner(noteTypes);
        typeSpinner.setPreferredSize(new Dimension(MeMemoGUI.WIDTH / 4, MeMemoGUI.HEIGHT / 20));
        constraints.gridx++;
        panel.add(typeSpinner, constraints);
    }

    //MODIFIES: this
    //EFFECTS: creates a field for the note title
    private void addNoteTitle() {
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridy++;
        constraints.gridx = 0;
        JLabel noteTitle = new JLabel("Add title: ");
        panel.add(noteTitle, constraints);

        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx++;
        noteNameField = new JTextField("Untitled", MeMemoGUI.JFIELD_WIDTH);
        panel.add(noteNameField, constraints);
    }

    //MODIFIES: this
    //EFFECTS: creates a field for the note description
    private void addNoteDescription() {
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridy++;
        constraints.gridx = 0;
        panel.add(new JLabel("Note description: "), constraints);

        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx++;
        noteDescriptionArea = new JTextArea(MeMemoGUI.JFIELD_HEIGHT, MeMemoGUI.JFIELD_WIDTH);
        panel.add(noteDescriptionArea, constraints);
    }

    //MODIFIES: this
    //EFFECTS: adds a "Confirm" button
    private void addConfirmButton() {
        constraints.gridy++;
        constraints.gridwidth = 2;
        JButton confirmButton = new JButton("Confirm");
        panel.add(confirmButton, constraints);
        confirmButton.addActionListener(this);
    }

    //MODIFIES: this
    //EFFECTS: initializes new note and sends note to Listeners
    @Override
    public void actionPerformed(ActionEvent e) {
        NoteType type = (NoteType) typeSpinner.getValue();
        initializeNote(type);
        note.addContent(noteDescriptionArea.getText());
        notifyNewNoteObservers(note);
        dispose();
    }

    //MODIFIES: this
    //EFFECTS: initializes a note based on the given type
    private void initializeNote(NoteType type) {
        switch (type) {
            case GENERAL_NOTE:
                note = new Note(noteNameField.getText());
                break;
            case REMINDER_NOTE:
                note = new ReminderNote(noteNameField.getText());
                break;
            case TASKLIST_NOTE:
                note = new TasklistNote(noteNameField.getText());
                break;
        }
    }
}
