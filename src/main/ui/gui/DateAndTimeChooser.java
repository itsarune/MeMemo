package ui.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * DateAndTimeChooser creates a date and time picker GUI and notifies Listeners when it is picked.
 */
public class DateAndTimeChooser extends ObserverableFrame {
    private JPanel panel = new JPanel(new GridBagLayout());
    private GridBagConstraints constraints = new GridBagConstraints();
    private JButton confirm;
    private JSpinner timeSpinner;
    private JSpinner dateSpinner;
    private LocalDateTime confirmedDate;

    //MODIFIES: this
    //EFFECTS: creates the GUI
    public DateAndTimeChooser() {
        super();

        addLabels();
        addSpinners();
        addButton();
        this.add(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(MeMemoGUI.WIDTH / 2, MeMemoGUI.HEIGHT / 2);
        setVisible(true);
        pack();
    }

    //MODIFIES: this
    //EFFECTS: adds the "Confirm" button to the gui
    private void addButton() {
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 2;
        confirm = new JButton("Confirm");
        panel.add(confirm, constraints);
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cleanup();
            }
        });
    }

    //MODIFIES: this
    //EFFECTS: Gets date and time and closes the picker object
    private void cleanup() {
        notifyListenerObservers(getTimeFromSpinners());
        dispose();
    }

    //MODiFIES: this
    //EFFECTS: gets date and time values from the spinners
    private LocalDateTime getTimeFromSpinners() {
        Date date = (Date) dateSpinner.getValue();
        Date time = (Date) timeSpinner.getValue();
        LocalDateTime ldt = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        confirmedDate = ldt.withHour(calendar.get(Calendar.HOUR_OF_DAY))
                .withMinute(calendar.get(Calendar.MINUTE));
        System.out.println("DEBUG - Set date: " + confirmedDate);
        return confirmedDate;
    }

    //MODIFIES: this
    //EFFECTS: notifies listener observers with the given date
    private void notifyListenerObservers(LocalDateTime date) {
        returnValue = date;
        notifyListenerObservers();
    }

    //MODIFIES: this
    //EFFECTS: adds spinners to the screen
    private void addSpinners() {
        Calendar cal = Calendar.getInstance();
        Date initDate = cal.getTime();
        cal.add(Calendar.YEAR, -1);
        Date startTime = cal.getTime();
        cal.add(Calendar.YEAR, 100);
        Date finalDate = cal.getTime();

        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx = 1;
        constraints.gridy = 0;
        SpinnerModel dateModel = new SpinnerDateModel(initDate,
                startTime,
                finalDate,
                Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd:MM:yyyy"));
        panel.add(dateSpinner, constraints);

        constraints.gridy++;
        timeSpinner = new JSpinner(new SpinnerDateModel(initDate, null, null,
                Calendar.HOUR));
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
        panel.add(timeSpinner, constraints);
    }

    //MODIFIES: this
    //EFFECTS: adds labels to the screen
    private void addLabels() {
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridx = 0;
        constraints.gridy = 0;
        JLabel dateLabel = new JLabel("Date: ");
        JLabel timeLabel = new JLabel("Time: ");
        panel.add(dateLabel, constraints);
        constraints.gridy++;
        panel.add(timeLabel, constraints);
    }
}
