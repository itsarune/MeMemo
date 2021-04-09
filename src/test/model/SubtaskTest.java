package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A testing class for Subtask.
 */
public class SubtaskTest {
    private Subtask s;

    @BeforeEach
    public void setup() {
        s = new Subtask("My task!");
    }

    @Test
    public void testConstructor() {
        assertEquals("My task!", s.getTask());
    }

    @Test
    public void testChangeTaskName() {
        s.changeTaskName("MeMemo");
        assertEquals("MeMemo", s.getTask());
    }
}
