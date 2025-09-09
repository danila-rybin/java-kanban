package ru.yandex.javacourse.http;

import com.sun.net.httpserver.HttpExchange;
import http.HttpMethod;
import ru.yandex.javacourse.manager.TaskManager;
import ru.yandex.javacourse.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class TasksHandler extends BaseHttpHandler {

    public TasksHandler(TaskManager taskManager) {
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

            if (httpMethod == HttpMethod.GET) {
                if (Pattern.matches("^/tasks$", path)) {
                    handleGetTasks(exchange);
                } else {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        sendSuccess(exchange, gson.toJson(tasks));
    }
}
