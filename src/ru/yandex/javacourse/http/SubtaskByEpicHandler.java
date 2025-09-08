package ru.yandex.javacourse.http;

import java.util.List;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacourse.manager.TaskManager;
import ru.yandex.javacourse.tasks.Epic;
import ru.yandex.javacourse.tasks.Subtask;
import http.HttpMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class SubtaskByEpicHandler extends BaseHttpHandler {

    public SubtaskByEpicHandler(TaskManager taskManager) {
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
                if (Pattern.matches("^/tasks/subtask/epic/\\d+$", path)) {
                    handleGetSubtasksByEpic(exchange, path);
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

    private void handleGetSubtasksByEpic(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");
        int epicId = parsePathId(pathParts[4]);

        if (epicId == -1) {
            sendBadRequest(exchange);
            return;
        }

        Epic epic = taskManager.getEpic(epicId);
        if (epic == null) {
            sendNotFound(exchange);
        } else {
            List<Subtask> subtasks = taskManager.getSubtasksForEpic(epicId);
            sendSuccess(exchange, gson.toJson(subtasks));
        }
    }

}