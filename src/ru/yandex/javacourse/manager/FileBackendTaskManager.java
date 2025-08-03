package ru.yandex.javacourse.manager;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import ru.yandex.javacourse.tasks.Epic;
import ru.yandex.javacourse.tasks.Subtask;
import ru.yandex.javacourse.tasks.Task;
import ru.yandex.javacourse.tasks.TaskStatus;


public class FileBackendTaskManager extends InMemoryTaskManager{

    private File file;

    public FileBackendTaskManager(File file) {
        this.file = file;
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            for (Task task : tasks.values()) {
                writer.write(toCsvString(task));
                writer.newLine();
            }

            for (Epic epic : epics.values()) {
                writer.write(toCsvString(epic));
                writer.newLine();
            }

            for (Subtask subtask : subtasks.values()) {
                writer.write(toCsvString(subtask));
                writer.newLine();
            }

            writer.newLine(); // пустая строка перед историей
            writer.write(historyToString(historyManager.getHistory()));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла", e);
        }
    }

    private String toCsvString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        if (task instanceof Epic) sb.append("EPIC");
        else if (task instanceof Subtask) sb.append("SUBTASK");
        else sb.append("TASK");
        sb.append(",");
        sb.append(task.getTitle()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDetails() != null ? task.getDetails() : "").append(",");
        if (task instanceof Subtask) {
            sb.append(((Subtask) task).getEpicId());
        }
        return sb.toString();
    }

    private Task fromCsvString(String line) {
        String[] fields = line.split(",", -1);
        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String title = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        switch (type) {
            case "TASK":
                return new Task(id, title, description, status);
            case "EPIC":
                return new Epic(id, title, description);
            case "SUBTASK":
                int epicId = Integer.parseInt(fields[5]);
                return new Subtask(id, title, description, status, epicId);
            default:
                throw new IllegalArgumentException("Unknown task type: " + type);
        }
    }

    public static FileBackendTaskManager loadFromFile(File file) {
        FileBackendTaskManager manager = new FileBackendTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            boolean historyBlock = false;
            for (int i = 1; i < lines.size(); i++) { // с пропуском заголовка
                String line = lines.get(i);
                if (line.isBlank()) {
                    historyBlock = true;
                    continue;
                }

                if (!historyBlock) {
                    Task task = manager.fromCsvString(line);
                    manager.currentId = Math.max(manager.currentId, task.getId());
                    switch (task) {
                        case Epic epic -> manager.epics.put(epic.getId(), epic);
                        case Subtask subtask -> {
                            manager.subtasks.put(subtask.getId(), subtask);
                            manager.epics.get(subtask.getEpicId()).addSubtask(subtask.getId());
                        }
                        case Task t -> manager.tasks.put(t.getId(), t);
                    }
                } else {
                    List<Integer> historyIds = historyFromString(line);
                    for (int id : historyIds) {
                        Task task = manager.tasks.get(id);
                        if (task == null) task = manager.subtasks.get(id);
                        if (task == null) task = manager.epics.get(id);
                        if (task != null) manager.historyManager.add(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки файла", e);
        }
        manager.currentId++;
        return manager;

    }

    private String historyToString(List<Task> history) {
        return history.stream()
                .map(t -> String.valueOf(t.getId()))
                .collect(Collectors.joining(","));
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> ids = new ArrayList<>();
        if (value != null && !value.isBlank()) {
            for (String idStr : value.split(",")) {
                ids.add(Integer.parseInt(idStr));
            }
        }
        return ids;
    }

    @Override
    public Task addTask(Task task) {
        Task result = super.addTask(task);
        save();
        return result;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic result = super.addEpic(epic);
        save();
        return result;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask result = super.addSubtask(subtask);
        save();
        return result;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        save();
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        save();
    }

}
