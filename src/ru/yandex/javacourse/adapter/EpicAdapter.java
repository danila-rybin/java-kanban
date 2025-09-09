package ru.yandex.javacourse.adapter;

import com.google.gson.*;
import ru.yandex.javacourse.tasks.Epic;
import ru.yandex.javacourse.tasks.TaskStatus;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EpicAdapter implements JsonSerializer<Epic>, JsonDeserializer<Epic> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public JsonElement serialize(Epic epic, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", epic.getId());
        jsonObject.addProperty("title", epic.getTitle());
        jsonObject.addProperty("details", epic.getDetails());
        jsonObject.addProperty("status", epic.getStatus().name());

        if (epic.getStartTime() != null) {
            jsonObject.addProperty("startTime", epic.getStartTime().format(formatter));
        }

        if (epic.getEndTime() != null) {
            jsonObject.addProperty("endTime", epic.getEndTime().format(formatter));
        }

        if (epic.getDuration() != null) {
            jsonObject.addProperty("duration", epic.getDuration().toString());
        }

        // сериализуем список id подзадач
        JsonArray subtaskArray = new JsonArray();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtaskArray.add(new JsonPrimitive(subtaskId));

        }
        jsonObject.add("subtaskIds", subtaskArray);

        return jsonObject;
    }

    @Override
    public Epic deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        int id = jsonObject.get("id").getAsInt();
        String title = jsonObject.get("title").getAsString();
        String details = jsonObject.get("details").getAsString();
        TaskStatus status = TaskStatus.valueOf(jsonObject.get("status").getAsString());

        Epic epic = new Epic(id, title, details);
        epic.setStatus(status);

        if (jsonObject.has("startTime") && !jsonObject.get("startTime").isJsonNull()) {
            epic.setStartTime(LocalDateTime.parse(jsonObject.get("startTime").getAsString(), formatter));
        }

        if (jsonObject.has("endTime") && !jsonObject.get("endTime").isJsonNull()) {
            epic.setEndTime(LocalDateTime.parse(jsonObject.get("endTime").getAsString(), formatter));
        }

        if (jsonObject.has("duration") && !jsonObject.get("duration").isJsonNull()) {
            epic.setDuration(Duration.parse(jsonObject.get("duration").getAsString()));
        }

        if (jsonObject.has("subtaskIds") && jsonObject.get("subtaskIds").isJsonArray()) {
            JsonArray subtaskArray = jsonObject.getAsJsonArray("subtaskIds");
            for (JsonElement element : subtaskArray) {
                epic.addSubtask(element.getAsInt());
            }
        }

        return epic;
    }
}
