package ui.gui;

import model.CollectionOfNotes;
import model.NoteBase;
import model.ReminderNote;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * ReminderManager is a system for managing ReminderNote reminders and notifications.
 */
public class ReminderManager {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ReminderNote nextReminder;
    private MeMemoGUI gui;

    public ReminderManager(MeMemoGUI gui) {
        this.gui = gui;
    }

    //MODIFIES: this
    //EFFECTS: updates the next reminder to display
    public void process() {
        CollectionOfNotes notebook = gui.getNotebook();
        if (notebook != null) {
            for (NoteBase n : notebook.getAllNotes()) {
                switch (n.getNoteType()) {
                    case REMINDER_NOTE:
                    case TASKLIST_NOTE:
                        ReminderNote rn = (ReminderNote) n;
                        boolean updateReminder = rn.hasReminder() && !rn.hasRun()
                                && ((nextReminder == null) || (rn.getReminder().isBefore(nextReminder.getReminder())));
                        if (updateReminder) {
                            nextReminder = rn;
                        }
                        break;
                }
            }
        }
    }

    //MODIFIES: this
    //EFFECTS: schedules a reminder checking background thread
    public void startTaskScheduler() {
        System.out.println("Debug - starting");
        final ScheduledFuture<?> taskHandle = scheduler.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Debug - processing;");
                        process();
                        checkIfReminderShouldBeSent();
                    }
                }, 0, 1, TimeUnit.MINUTES);
    }

    //MODIFIES: this
    //EFFECTS: checks if a reminder should be sent and sends it
    public void checkIfReminderShouldBeSent() {
        System.out.println("Debug - check firing");
        if (nextReminder != null && LocalDateTime.now().isAfter(nextReminder.getReminder())) {
            System.out.println("Debug - fire");
            gui.receiveReminder(nextReminder);
            nextReminder.setHasRun(true);
            nextReminder = null;
        }
    }
}
