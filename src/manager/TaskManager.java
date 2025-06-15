package tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private int currentId = 1;

    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    // Создаём обычную задачу
    public Task addTask(Task task) {
        task.setId(currentId++);
        tasks.put(task.getId(), task);
        return task;
    }

    // Создаём эпик (основная задача)
    public Epic addEpic(Epic epic) {
        epic.setId(currentId++);
        epics.put(epic.getId(), epic);
        return epic;
    }

    // Добавляем подзадачу к эпику
    public Subtask addSubtask(Subtask subtask) {
        subtask.setId(currentId++);
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask.getId());
            refreshEpicStatus(epic.getId());
        }
        return subtask;
    }

    // Получить списки всех задач
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Поиск по ID
    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    // Обновляем задачу, если есть
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    // Обновляем эпик, статус нельзя менять вручную
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            // статус эпика всегда рассчитываем заново
            epic.setStatus(epics.get(epic.getId()).getStatus());
            epics.put(epic.getId(), epic);
            refreshEpicStatus(epic.getId());
        }
    }

    // Обновляем подзадачу и обновляем статус эпика
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            refreshEpicStatus(subtask.getEpicId());
        }
    }

    // Удаляем все задачи
    public void clearAllTasks() {
        tasks.clear();
    }

    public void clearAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void clearAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            refreshEpicStatus(epic.getId());
        }
    }

    // Удаляем по ID
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subId : epic.getSubtaskIds()) {
                subtasks.remove(subId);
            }
        }
    }

    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                refreshEpicStatus(epic.getId());
            }
        }
    }

    // Получить все подзадачи у эпика
    public List<Subtask> getSubtasksForEpic(int epicId) {
        List<Subtask> result = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    result.add(subtask);
                }
            }
        }
        return result;
    }

    // Обновляем статус эпика на основе подзадач
    private void refreshEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Integer> subtaskIds = epic.getSubtaskIds();

        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (int subtaskId : subtaskIds) {
            Subtask st = subtasks.get(subtaskId);
            if (st != null) {
                TaskStatus status = st.getStatus();
                if (status != TaskStatus.DONE) allDone = false;
                if (status != TaskStatus.NEW) allNew = false;
            }
        }

        if (allNew) epic.setStatus(TaskStatus.NEW);
        else if (allDone) epic.setStatus(TaskStatus.DONE);
        else epic.setStatus(TaskStatus.IN_PROGRESS);
    }
}
