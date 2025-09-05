package ru.yandex.javacourse.manager;

import org.junit.jupiter.api.*;
import ru.yandex.javacourse.tasks.*;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackendTaskManagerTest {

    private FileBackendTaskManager manager;
    private File testFile;

    @BeforeEach
    public void setup() throws Exception {
        // Создаем временный файл для теста
        testFile = File.createTempFile("test_tasks", ".csv");
        // Очищаем содержимое
        Files.writeString(testFile.toPath(), "");
        manager = new FileBackendTaskManager(testFile);
    }

    @AfterEach
    public void cleanup() {
        // Удаляем файл после теста
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    public void testAddTaskAndSaveToFile() throws Exception {
        Task task = new Task(0, "Test Task", "Description", TaskStatus.NEW);
        task.setDuration(Duration.ofHours(1));
        task.setStartTime(LocalDateTime.of(2025, 9, 4, 22, 0));

        Task addedTask = manager.addTask(task);

        assertNotNull(addedTask);
        assertTrue(addedTask.getId() > 0);

        List<String> lines = Files.readAllLines(testFile.toPath());

        // В файле минимум 2 строки: заголовок и задача
        assertTrue(lines.size() >= 2);
        assertTrue(lines.get(0).contains("id,type,name,status,description,epic,duration,startTime"));

        boolean containsTaskLine = lines.stream()
                .anyMatch(line -> line.contains("Test Task") && line.contains("NEW"));
        assertTrue(containsTaskLine);
    }

    @Test
    public void testLoadFromFile() throws Exception {
        // Создаем CSV с задачей
        String content = """
                id,type,name,status,description,epic,duration,startTime
                1,TASK,Loaded Task,NEW,Loaded description,,PT1H,2025-09-04T22:00
                """;
        Files.writeString(testFile.toPath(), content);

        FileBackendTaskManager loadedManager = FileBackendTaskManager.loadFromFile(testFile);

        Task loadedTask = loadedManager.getTask(1);
        assertNotNull(loadedTask);
        assertEquals("Loaded Task", loadedTask.getTitle());
        assertEquals(TaskStatus.NEW, loadedTask.getStatus());
        assertEquals("Loaded description", loadedTask.getDetails());

        assertEquals(Duration.parse("PT1H"), loadedTask.getDuration());
        assertEquals(LocalDateTime.parse("2025-09-04T22:00"), loadedTask.getStartTime());
    }

    @Test
    public void testAddEpicAndSubtasksAndSave() throws Exception {
        Epic epic = new Epic(0, "Test Epic", "Epic Details");
        epic.setStartTime(LocalDateTime.of(2025, 9, 4, 22, 0));
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask(0, "Subtask 1", "Desc 1", TaskStatus.NEW, epic.getId());
        subtask1.setDuration(Duration.ofMinutes(60));
        subtask1.setStartTime(LocalDateTime.of(2025, 9, 4, 22, 0));

        Subtask subtask2 = new Subtask(0, "Subtask 2", "Desc 2", TaskStatus.DONE, epic.getId());
        subtask2.setDuration(Duration.ofMinutes(120));
        subtask2.setStartTime(LocalDateTime.of(2025, 9, 4, 23, 0));

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        List<String> lines = Files.readAllLines(testFile.toPath());

        assertTrue(lines.stream().anyMatch(line -> line.contains("Test Epic")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("Subtask 1")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("Subtask 2")));
    }

    @Test
    public void testEpicStatusCalculation() {
        Epic epic = new Epic(0, "Epic", "Details");
        manager.addEpic(epic);

        Subtask s1 = new Subtask(0, "S1", "Desc", TaskStatus.NEW, epic.getId());
        Subtask s2 = new Subtask(0, "S2", "Desc", TaskStatus.NEW, epic.getId());
        manager.addSubtask(s1);
        manager.addSubtask(s2);

        assertEquals(TaskStatus.NEW, manager.getEpic(epic.getId()).getStatus());

        s1.setStatus(TaskStatus.DONE);
        manager.updateSubtask(s1);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());

        s2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(s2);
        assertEquals(TaskStatus.DONE, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void testExceptionOnInvalidFile() {
        File invalidFile = new File("nonexistent.csv");
        assertThrows(Exception.class, () -> FileBackendTaskManager.loadFromFile(invalidFile));
    }
}
