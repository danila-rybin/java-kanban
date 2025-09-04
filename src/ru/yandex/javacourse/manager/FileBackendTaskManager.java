package ru.yandex.javacourse.manager;

import ru.yandex.javacourse.exception.ManagerSaveException;
import ru.yandex.javacourse.tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FileBackendTaskManager extends InMemoryTaskManager {

    private final File file;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public FileBackendTaskManager(File file) {
        this.file = file;
    }

    // Сохранение данных
    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic,duration,startTime");
            writer.newLine();

            for (Task task : tasks.values()) writer.write(toCsvString(task) + "\n");
            for (Epic epic : epics.values()) writer.write(toCsvString(epic) + "\n");
            for (Subtask subtask : subtasks.values()) writer.write(toCsvString(subtask) + "\n");

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
        if (task instanceof Subtask) sb.append(((Subtask) task).getEpicId());
        sb.append(",");
        sb.append(task.getDuration() != null ? task.getDuration() : "").append(",");
        sb.append(task.getStartTime() != null ? task.getStartTime().format(formatter) : "");
        return sb.toString();
    }

    private Task fromCsvString(String line) {
        String[] fields = line.split(",", -1);
        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String title = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        Duration duration = fields[6].isBlank() ? null : Duration.parse(fields[6]);
        LocalDateTime startTime = fields[7].isBlank() ? null : LocalDateTime.parse(fields[7], formatter);

        return switch (type) {
            case "TASK" -> new Task(id, title, description, status, duration, startTime);
            case "EPIC" -> new Epic(id, title, description);
            case "SUBTASK" -> new Subtask(id, title, description, status, Integer.parseInt(fields[5]), duration, startTime);
            default -> throw new IllegalArgumentException("Unknown task type: " + type);
        };
    }

    public static FileBackendTaskManager loadFromFile(File file) {
        FileBackendTaskManager manager = new FileBackendTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            boolean historyBlock = false;
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) {
                    historyBlock = true;
                    continue;
                }

                if (!historyBlock) {
                    Task task = manager.fromCsvString(line);
                    manager.currentId = Math.max(manager.currentId, task.getId());
                    if (task instanceof Epic epic) {
                        manager.epics.put(epic.getId(), epic);
                    } else if (task instanceof Subtask subtask) {
                        manager.subtasks.put(subtask.getId(), subtask);
                        Epic epic = manager.epics.get(subtask.getEpicId());
                        if (epic != null) epic.addSubtask(subtask.getId());
                    } else {
                        manager.tasks.put(task.getId(), task);
                    }
                } else {
                    for (int id : historyFromString(line)) {
                        Task task = manager.tasks.get(id);
                        if (task == null) task = manager.subtasks.get(id);
                        if (task == null) task = manager.epics.get(id);
                        if (task != null) manager.historyManager.add(task);
                    }
                }
            }
            manager.currentId++;
            manager.epics.values().forEach(manager::refreshEpicTimesAndStatus);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки файла", e);
        }
        return manager;
    }

    private static List<Integer> historyFromString(String value) {
        if (value == null || value.isBlank()) return Collections.emptyList();
        return Arrays.stream(value.split(",")).map(Integer::parseInt).toList();
    }

    private String historyToString(List<Task> history) {
        return history.stream().map(Task::getId).map(String::valueOf).collect(Collectors.joining(","));
    }

    //Проверка пересечения времени
    private void checkTimeIntersection(Task newTask) {
        if (newTask.getStartTime() == null) return;

        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        allTasks.addAll(subtasks.values());

        for (Task t : allTasks) {
            if (t.getStartTime() == null || t.getId() == newTask.getId()) continue;

            LocalDateTime start1 = t.getStartTime();
            LocalDateTime end1 = t.getEndTime();
            LocalDateTime start2 = newTask.getStartTime();
            LocalDateTime end2 = newTask.getEndTime();

            if (end1 != null && end2 != null &&
                    (start1.isBefore(end2) && start2.isBefore(end1))) {
                throw new IllegalArgumentException("Время задачи пересекается с другой задачей!");
            }
        }
    }

    protected void refreshEpicTimesAndStatus(Epic epic) {
        List<Subtask> subtaskList = epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .toList();

        if (subtaskList.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            epic.setEndTime(null);
            return;
        }

        LocalDateTime start = subtaskList.stream()
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime end = subtaskList.stream()
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        Duration totalDuration = subtaskList.stream()
                .map(Task::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setStartTime(start);
        epic.setDuration(totalDuration);
        epic.setEndTime(end);

        boolean allNew = subtaskList.stream().allMatch(st -> st.getStatus() == TaskStatus.NEW);
        boolean allDone = subtaskList.stream().allMatch(st -> st.getStatus() == TaskStatus.DONE);

        if (allNew) epic.setStatus(TaskStatus.NEW);
        else if (allDone) epic.setStatus(TaskStatus.DONE);
        else epic.setStatus(TaskStatus.IN_PROGRESS);
    }

    public List<Task> getPrioritizedTasks() {
        List<Task> all = new ArrayList<>();
        all.addAll(tasks.values());
        all.addAll(subtasks.values());
        all.sort(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
        return all;
    }

    // Переопределённые методы для сохранения
    @Override
    public Task addTask(Task task) {
        checkTimeIntersection(task);
        Task t = super.addTask(task);
        save();
        return t;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic e = super.addEpic(epic);
        save();
        return e;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        checkTimeIntersection(subtask);
        Subtask s = super.addSubtask(subtask);
        refreshEpicTimesAndStatus(epics.get(subtask.getEpicId()));
        save();
        return s;
    }

    @Override
    public void updateTask(Task task) {
        checkTimeIntersection(task);
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        refreshEpicTimesAndStatus(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        checkTimeIntersection(subtask);
        super.updateSubtask(subtask);
        refreshEpicTimesAndStatus(epics.get(subtask.getEpicId()));
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
