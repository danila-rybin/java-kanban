package ru.yandex.javacourse.tasks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    void testEqualityById() {
        Task t1 = new Task(1, "Task 1", "Details", TaskStatus.NEW);
        Task t2 = new Task(1, "Task 2", "Other details", TaskStatus.DONE);
        assertEquals(t1, t2, "Задачи с одинаковым id должны быть равны");
    }

    @Test
    void testHashCodeConsistency() {
        Task t1 = new Task(1, "Task 1", "Details", TaskStatus.NEW);
        Task t2 = new Task(1, "Task 2", "Other details", TaskStatus.DONE);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        Task task = new Task(1, "Title", "Details", TaskStatus.IN_PROGRESS);
        String str = task.toString();
        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("Title"));
        assertTrue(str.contains("IN_PROGRESS"));
    }
}
