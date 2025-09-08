package ru.yandex.javacourse.http;

import com.sun.net.httpserver.HttpExchange;
import http.HttpMethod;
import ru.yandex.javacourse.manager.TaskManager;
import ru.yandex.javacourse.tasks.Task;

import java.io.IOException;


public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            HttpMethod httpMethod;

            try {
                httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());
            } catch (IllegalArgumentException e) {
                sendNotFound(exchange);
                return;
            }

            switch (httpMethod) {
                case GET:
                    handleGetRequest(exchange, path, "/tasks/task/",
                            v -> taskManager.getAllTasks(),
                            taskManager::getTask);
                    break;
                case POST:
                    handlePostRequest(exchange, Task.class,
                            taskManager::addTask,
                            task -> {
                                taskManager.updateTask(task);
                                return task;
                            },
                            task -> task.getId() == 0);
                    break;
                case DELETE:
                    handleDeleteRequest(exchange, path, "/tasks/task/",
                            taskManager::clearAllTasks,
                            taskManager::getTask,
                            taskManager::deleteTask);
                    break;
            }
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }
}
