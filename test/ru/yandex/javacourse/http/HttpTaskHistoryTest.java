package ru.yandex.javacourse.http;

import ru.yandex.javacourse.tasks.TaskStatus;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskHistoryTest extends HttpTaskServerTestMain {

    @Test
    void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task(0, "Task 1", "Description 1", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.now());
        Task task2 = new Task(0, "Task 2", "Description 2", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(2));

        int taskId1 = createTaskAndGetId(task1);
        int taskId2 = createTaskAndGetId(task2);


        client.send(buildGetTaskRequest(taskId1), HttpResponse.BodyHandlers.ofString());
        client.send(buildGetTaskRequest(taskId2), HttpResponse.BodyHandlers.ofString());


        HttpRequest historyRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/history/"))
                .GET()
                .build();
        HttpResponse<String> historyResponse = client.send(historyRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, historyResponse.statusCode(), "Неверный статус код для истории");

        Task[] history = gson.fromJson(historyResponse.body(), Task[].class);
        assertNotNull(history, "История не вернулась");
        assertEquals(2, history.length, "В истории должно быть 2 задачи");

        Set<Integer> historyIds = Arrays.stream(history)
                .map(Task::getId)
                .collect(Collectors.toSet());
        assertTrue(historyIds.contains(taskId1), "В истории должна быть первая задача");
        assertTrue(historyIds.contains(taskId2), "В истории должна быть вторая задача");
    }

    @Test
    void testGetEmptyHistory() throws IOException, InterruptedException {
        HttpRequest historyRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/history/"))
                .GET()
                .build();

        HttpResponse<String> historyResponse = client.send(historyRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, historyResponse.statusCode(), "Неверный статус код для пустой истории");

        Task[] history = gson.fromJson(historyResponse.body(), Task[].class);
        assertNotNull(history, "История не вернулась");
        assertEquals(0, history.length, "История должна быть пустой");
    }

    private int createTaskAndGetId(Task task) throws IOException, InterruptedException {
        String taskJson = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный статус код при создании задачи");
        Task createdTask = gson.fromJson(response.body(), Task.class);
        return createdTask.getId(); // возвращаем int
    }

    private HttpRequest buildGetTaskRequest(int taskId) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/" + taskId))
                .GET()
                .build();
    }
}
