package ru.yandex.javacourse.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.tasks.Task;
import ru.yandex.javacourse.tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task = new Task(1, "Test", "Details", TaskStatus.NEW);
    }

    @Test
    void addTaskToHistory() {
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void historyDoesNotExceed10() {
        for (int i = 1; i <= 15; i++) {
            historyManager.add(new Task(i, "Task" + i, "Details", TaskStatus.NEW));
        }
        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size());
        assertEquals(6, history.get(0).getId()); // первый элемент — 6, потому что 1..5 были удалены
    }
}
