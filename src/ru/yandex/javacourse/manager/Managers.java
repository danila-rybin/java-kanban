package ru.yandex.javacourse.manager;

import java.io.File;

public class Managers {

    private static final String DEFAULT_FILE_PATH = "tasks.csv";

    // Возвращает стандартный менеджер с памятью (InMemory)
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    // Возвращает менеджер с файловым бэкендом
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
}
