package ru.yandex.javacourse.manager;


import org.junit.jupiter.api.*;
import ru.yandex.javacourse.tasks.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackendTaskManagerTest {

    private FileBackendTaskManager manager;
    private File testFile;

    @BeforeEach
    public void setup() throws Exception {
        // Создаем временный файл для теста
        testFile = File.createTempFile("test_tasks", ".csv");
        // Удаляем содержимое, чтобы файл был пустым
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
        Task addedTask = manager.addTask(task);

        // Проверяем, что задача добавлена и у неё id > 0
        assertNotNull(addedTask);
        assertTrue(addedTask.getId() > 0);

        // Проверяем, что в файле есть запись с этой задачей
        List<String> lines = Files.readAllLines(testFile.toPath());

        // В файле минимум 2 строки: заголовок и задача
        assertTrue(lines.size() >= 2);
        assertTrue(lines.get(0).contains("id,type,name,status,description,epic"));

        // Проверяем, что строка задачи содержит title и статус
        boolean containsTaskLine = lines.stream()
                .anyMatch(line -> line.contains("Test Task") && line.contains("NEW"));
        assertTrue(containsTaskLine);
    }

    @Test
    public void testLoadFromFile() throws Exception {
        // Создаем строку csv с задачей
        String content = """
                id,type,name,status,description,epic
                1,TASK,Loaded Task,NEW,Loaded description,
                """;
        Files.writeString(testFile.toPath(), content);

        FileBackendTaskManager loadedManager = FileBackendTaskManager.loadFromFile(testFile);

        Task loadedTask = loadedManager.getTask(1);
        assertNotNull(loadedTask);
        assertEquals("Loaded Task", loadedTask.getTitle());
        assertEquals(TaskStatus.NEW, loadedTask.getStatus());
        assertEquals("Loaded description", loadedTask.getDetails());
    }
}
