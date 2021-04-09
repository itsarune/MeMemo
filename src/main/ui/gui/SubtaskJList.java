package ui.gui;

import model.Subtask;
import model.TasklistNote;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * SubtaskJList is a JList for displaying subtasks.
 */
public class SubtaskJList extends JList implements ListSelectionListener {
    private DefaultListModel list;
    private MeMemoGUI gui;
    private TasklistNote selectedNote;
    private JButton addSubButton = new JButton("Add subtask");
    private JButton changeSubButton = new JButton("Change subtask");
    private int subtaskIndex = 0;

    //REQUIRES: noteIndex refers to a TasklistNote in gui.getNotebook()
    //EFFECTS: initializes the subtask list display
    public SubtaskJList(String [] elements, MeMemoGUI gui, int noteIndex) {
        super();
        this.gui = gui;
        list = new DefaultListModel();

        for (String s : elements) {
            list.addElement(s);
        }

        setModel(list);
        selectedNote = (TasklistNote) gui.getNotebook().getNote(noteIndex);
        initButtons();
        setSelectedIndex(subtaskIndex = 0);
        addListSelectionListener(this);
    }

    //EFFECTS: updates the index of the selected subtask internally and externally (on GUI)
    @Override
    public void setSelectedIndex(int index) {
        super.setSelectedIndex(index);
        subtaskIndex = index;
    }

    //MODIFIES: this
    //EFFECTS: initializes the buttons
    private void initButtons() {
        addSubButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String subtask = JOptionPane.showInputDialog("Enter a new subtask to add: ");
                add(new Subtask(subtask));
            }
        });
        changeSubButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newSubtask = JOptionPane.showInputDialog("Edit your subtask: ",
                        selectedNote.getSubtask(subtaskIndex).getTask());
                update(newSubtask);
            }
        });
    }

    //MODIFIES: this
    //EFFECTS: updates the subtask at the current index
    private void update(String updatedTaskName) {
        selectedNote.getSubtask(subtaskIndex).changeTaskName(updatedTaskName);
        list.setElementAt(updatedTaskName, subtaskIndex);
        gui.getContentPane().revalidate();
    }

    //MODIFIES: this
    //EFFECTS: adds a subtask to the JList
    public void add(Subtask s) {
        selectedNote.addSubtask(s);
        list.addElement(s.getTask());
        gui.getContentPane().revalidate();
        setSelectedIndex(list.size() - 1);
    }

    //MODIFIES: this
    //EFFECTS: updates the current index of this list
    @Override
    public void valueChanged(ListSelectionEvent e) {
        subtaskIndex = getSelectedIndex();
    }

    //getters
    public JButton getAddButton() {
        return addSubButton;
    }

    public JButton getChangeSubButton() {
        return changeSubButton;
    }
}
