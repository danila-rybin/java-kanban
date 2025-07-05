package ru.yandex.javacourse.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    @Test
    void testEqualsAndHashCode() {
        Subtask s1 = new Subtask(1, "Subtask", "Details", TaskStatus.NEW, 100);
        Subtask s2 = new Subtask(1, "Subtask different", "Other", TaskStatus.DONE, 100);

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void testEpicId() {
        Subtask subtask = new Subtask(1, "Subtask", "Details", TaskStatus.NEW, 123);
        assertEquals(123, subtask.getEpicId());
        subtask.setEpicId(456);
        assertEquals(456, subtask.getEpicId());
    }
}
