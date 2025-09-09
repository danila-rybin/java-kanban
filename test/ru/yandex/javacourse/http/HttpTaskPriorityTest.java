package ru.yandex.javacourse.http;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.tasks.Task;
import ru.yandex.javacourse.tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskPriorityTest extends HttpTaskServerTestMain {

    @Test
    void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task(0, "Task Early", "Early task", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.now().plusHours(1));
        Task task2 = new Task(0, "Task Late", "Late task", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(3));

        int taskId1 = createTaskAndGetId(task1);
        int taskId2 = createTaskAndGetId(task2);

        HttpRequest priorityRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/"))
                .GET()
                .build();

        HttpResponse<String> priorityResponse = client.send(priorityRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, priorityResponse.statusCode(), "Неверный статус код для приоритетных задач");

        Task[] prioritizedTasks = gson.fromJson(priorityResponse.body(), Task[].class);
        assertNotNull(prioritizedTasks, "Приоритетные задачи не вернулись");
        assertEquals(2, prioritizedTasks.length, "Должно быть 2 приоритетные задачи");

        assertEquals("Task Early", prioritizedTasks[0].getTitle(),
                "Задачи должны быть отсортированы по времени начала");
    }

    @Test
    void testGetEmptyPrioritizedTasks() throws IOException, InterruptedException {
        HttpRequest priorityRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/"))
                .GET()
                .build();

        HttpResponse<String> priorityResponse = client.send(priorityRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, priorityResponse.statusCode(), "Неверный статус код для пустого списка");

        Task[] prioritizedTasks = gson.fromJson(priorityResponse.body(), Task[].class);
        assertNotNull(prioritizedTasks, "Приоритетные задачи не вернулись");
        assertEquals(0, prioritizedTasks.length, "Список приоритетных задач должен быть пустым");
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
}
