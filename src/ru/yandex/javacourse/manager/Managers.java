package ru.yandex.javacourse.manager;

import java.io.File;
import ru.yandex.javacourse.adapter.EpicAdapter;
import ru.yandex.javacourse.adapter.SubtaskAdapter;
import ru.yandex.javacourse.adapter.TaskAdapter;
import ru.yandex.javacourse.tasks.Task;
import ru.yandex.javacourse.tasks.Epic;
import ru.yandex.javacourse.tasks.Subtask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Managers {

    private static final String DEFAULT_FILE_PATH = "tasks.csv";


    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }


    public static FileBackendTaskManager getFileBacked() {
        return getFileBacked(DEFAULT_FILE_PATH);
    }

    public static FileBackendTaskManager getFileBacked(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.length() > 0) {
            return FileBackendTaskManager.loadFromFile(file);
        } else {
            return new FileBackendTaskManager(file);
        }
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(Task.class, new TaskAdapter())
                .registerTypeAdapter(Epic.class, new EpicAdapter())
                .registerTypeAdapter(Subtask.class, new SubtaskAdapter())
                .create();
    }
}
