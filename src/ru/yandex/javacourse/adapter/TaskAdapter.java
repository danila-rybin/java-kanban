package ru.yandex.javacourse.adapter;

import com.google.gson.*;
import ru.yandex.javacourse.tasks.Task;
import ru.yandex.javacourse.tasks.TaskStatus;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskAdapter implements JsonSerializer<Task>, JsonDeserializer<Task> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public JsonElement serialize(Task task, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", task.getId());
        jsonObject.addProperty("title", task.getTitle());
        jsonObject.addProperty("details", task.getDetails());
        jsonObject.addProperty("status", task.getStatus().name());

        if (task.getStartTime() != null) {
            jsonObject.addProperty("startTime", task.getStartTime().format(formatter));
        }

        if (task.getDuration() != null) {
            jsonObject.addProperty("duration", task.getDuration().toString()); // ISO-8601 format, e.g., "PT30M"
        }

        return jsonObject;
    }

    @Override
    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        int id = jsonObject.get("id").getAsInt();
        String title = jsonObject.get("title").getAsString();
        String details = jsonObject.get("details").getAsString();
        TaskStatus status = TaskStatus.valueOf(jsonObject.get("status").getAsString());

        LocalDateTime startTime = null;
        if (jsonObject.has("startTime") && !jsonObject.get("startTime").isJsonNull()) {
            startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString(), formatter);
        }

        Duration duration = null;
        if (jsonObject.has("duration") && !jsonObject.get("duration").isJsonNull()) {
            duration = Duration.parse(jsonObject.get("duration").getAsString());
        }

        return new Task(id, title, details, status, duration, startTime);
    }
}
