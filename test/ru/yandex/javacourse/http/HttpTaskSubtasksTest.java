package ru.yandex.javacourse.http;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.tasks.Epic;
import ru.yandex.javacourse.tasks.Subtask;
import ru.yandex.javacourse.tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskSubtasksTest extends HttpTaskServerTestMain {

    private int createEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(0, "Test Epic", "Test Epic Description"); // id = 0
        String epicJson = gson.toJson(epic);

        HttpRequest epicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, epicResponse.statusCode(), "Не удалось создать эпик");

        Epic createdEpic = gson.fromJson(epicResponse.body(), Epic.class);
        return createdEpic.getId();
    }

    @Test
    void testAddSubtask() throws IOException, InterruptedException {
        int epicId = createEpic();
        Subtask subtask = new Subtask(0, "Test Subtask", "Test Description", TaskStatus.NEW,
                epicId, Duration.ofMinutes(60), LocalDateTime.now());
        String subtaskJson = gson.toJson(subtask);

        HttpResponse<String> response = createSubtask(subtaskJson);
        assertEquals(201, response.statusCode(), "Неверный статус код");

        Subtask[] subtasks = getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(1, subtasks.length, "Некорректное количество подзадач");
        assertEquals("Test Subtask", subtasks[0].getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    void testGetSubtaskById() throws IOException, InterruptedException {
        int epicId = createEpic();
        Subtask subtask = new Subtask(0, "Test Subtask", "Test Description", TaskStatus.NEW,
                epicId, Duration.ofMinutes(60), LocalDateTime.now());
        String subtaskJson = gson.toJson(subtask);

        HttpResponse<String> postResponse = createSubtask(subtaskJson);
        assertEquals(201, postResponse.statusCode(), "Неверный статус код при создании");

        Subtask createdSubtask = gson.fromJson(postResponse.body(), Subtask.class);
        int subtaskId = createdSubtask.getId();

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/" + subtaskId))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Неверный статус код");

        Subtask responseSubtask = gson.fromJson(getResponse.body(), Subtask.class);
        assertNotNull(responseSubtask, "Подзадача не вернулась");
        assertEquals(subtaskId, responseSubtask.getId(), "Неверный ID подзадачи");
    }

    private HttpResponse<String> createSubtask(String subtaskJson) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private Subtask[] getAllSubtasks() throws IOException, InterruptedException {
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), Subtask[].class);
    }
}
