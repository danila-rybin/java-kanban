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
        assertEquals(6, history.get(0).getId());
    }

    @Test
    void removeFromHistoryMiddleStartEnd() {
        Task t1 = new Task(1, "T1", "D", TaskStatus.NEW);
        Task t2 = new Task(2, "T2", "D", TaskStatus.NEW);
        Task t3 = new Task(3, "T3", "D", TaskStatus.NEW);

        historyManager.add(t1);
        historyManager.add(t2);
        historyManager.add(t3);

        historyManager.remove(2);
        List<Task> hist = historyManager.getHistory();
        assertEquals(2, hist.size());
        assertEquals(t1, hist.get(0));
        assertEquals(t3, hist.get(1));

        historyManager.remove(1);
        hist = historyManager.getHistory();
        assertEquals(1, hist.size());
        assertEquals(t3, hist.get(0));

        historyManager.remove(3);
        hist = historyManager.getHistory();
        assertTrue(hist.isEmpty());
    }

    @Test
    void addDuplicateToHistory() {
        Task t = new Task(1, "T", "D", TaskStatus.NEW);
        historyManager.add(t);
        historyManager.add(t);
        historyManager.add(t);

        List<Task> hist = historyManager.getHistory();
        assertEquals(1, hist.size(), "Дубликаты должны удаляться");
    }
}
