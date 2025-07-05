package ru.yandex.javacourse.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.tasks.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void addAndGetTask() {
        Task task = new Task(0, "Test task", "Details", TaskStatus.NEW);
        manager.addTask(task);

        Task retrieved = manager.getTask(task.getId());
        assertNotNull(retrieved);
        assertEquals(task.getTitle(), retrieved.getTitle());
    }

    @Test
    void addAndGetEpicWithSubtasks() {
        Epic epic = new Epic(0, "Epic", "Details");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask(0, "Subtask 1", "Desc", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask(0, "Subtask 2", "Desc", TaskStatus.NEW, epic.getId());

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        List<Subtask> subtasks = manager.getSubtasksForEpic(epic.getId());
        assertEquals(2, subtasks.size());
        assertTrue(subtasks.stream().anyMatch(s -> s.getId() == subtask1.getId()));
        assertTrue(subtasks.stream().anyMatch(s -> s.getId() == subtask2.getId()));
    }

    @Test
    void epicStatusChangesAccordingToSubtasks() {
        Epic epic = new Epic(0, "Epic", "Details");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask(0, "Subtask 1", "Desc", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask(0, "Subtask 2", "Desc", TaskStatus.NEW, epic.getId());

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(TaskStatus.NEW, manager.getEpic(epic.getId()).getStatus());

        subtask1.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());

        subtask2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask2);

        assertEquals(TaskStatus.DONE, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void historyIsUpdatedOnGet() {
        Task task = new Task(0, "Task", "Desc", TaskStatus.NEW);
        manager.addTask(task);

        Epic epic = new Epic(0, "Epic", "Desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask(0, "Subtask", "Desc", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);

        manager.getTask(task.getId());
        manager.getEpic(epic.getId());
        manager.getSubtask(subtask.getId());

        List<Task> history = manager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task, history.get(0));
        assertEquals(epic, history.get(1));
        assertEquals(subtask, history.get(2));
    }
}
