package ru.yandex.javacourse.manager;

import ru.yandex.javacourse.tasks.*;

import java.util.List;

public interface TaskManager {
    Task addTask(Task task);
    Epic addEpic(Epic epic);
    Subtask addSubtask(Subtask subtask);

    List<Task> getAllTasks();
    List<Epic> getAllEpics();
    List<Subtask> getAllSubtasks();

    Task getTask(int id);
    Epic getEpic(int id);
    Subtask getSubtask(int id);

    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(Subtask subtask);

    void clearAllTasks();
    void clearAllEpics();
    void clearAllSubtasks();

    void deleteTask(int id);
    void deleteEpic(int id);
    void deleteSubtask(int id);

    List<Subtask> getSubtasksForEpic(int id);

    List<Task> getHistory();
    List<Task> getPrioritizedTasks();
}
