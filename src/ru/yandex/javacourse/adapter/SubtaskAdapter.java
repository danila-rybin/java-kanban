package ru.yandex.javacourse.adapter;

import com.google.gson.*;
import ru.yandex.javacourse.tasks.Subtask;
import ru.yandex.javacourse.tasks.TaskStatus;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubtaskAdapter implements JsonSerializer<Subtask>, JsonDeserializer<Subtask> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public JsonElement serialize(Subtask subtask, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", subtask.getId());
        jsonObject.addProperty("title", subtask.getTitle());
        jsonObject.addProperty("details", subtask.getDetails());
        jsonObject.addProperty("status", subtask.getStatus().name());
        jsonObject.addProperty("epicId", subtask.getEpicId());

        if (subtask.getStartTime() != null) {
            jsonObject.addProperty("startTime", subtask.getStartTime().format(formatter));
        }

        if (subtask.getDuration() != null) {
            jsonObject.addProperty("duration", subtask.getDuration().toString());
        }

        return jsonObject;
    }

    @Override
    public Subtask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        int id = jsonObject.get("id").getAsInt();
        String title = jsonObject.get("title").getAsString();
        String details = jsonObject.get("details").getAsString();
        TaskStatus status = TaskStatus.valueOf(jsonObject.get("status").getAsString());
        int epicId = jsonObject.get("epicId").getAsInt();

        LocalDateTime startTime = null;
        if (jsonObject.has("startTime") && !jsonObject.get("startTime").isJsonNull()) {
            startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString(), formatter);
        }

        Duration duration = null;
        if (jsonObject.has("duration") && !jsonObject.get("duration").isJsonNull()) {
            duration = Duration.parse(jsonObject.get("duration").getAsString());
        }

        return new Subtask(id, title, details, status, epicId, duration, startTime);
    }
}
