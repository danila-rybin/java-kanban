package ru.yandex.javacourse.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @Test
    void testAddAndRemoveSubtasks() {
        Epic epic = new Epic(1, "Epic", "Details");
        epic.addSubtask(10);
        epic.addSubtask(20);
        assertEquals(2, epic.getSubtaskIds().size());

        epic.removeSubtask(10);
        assertEquals(1, epic.getSubtaskIds().size());

        epic.removeAllSubtasks();
        assertTrue(epic.getSubtaskIds().isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        Epic epic1 = new Epic(1, "Epic", "Details");
        epic1.addSubtask(1);

        Epic epic2 = new Epic(1, "Epic other", "Other details");
        epic2.addSubtask(1);

        assertEquals(epic1, epic2);
        assertEquals(epic1.hashCode(), epic2.hashCode());
    }
}
