package ru.yandex.javacourse.manager;

import java.io.File;
import ru.yandex.javacourse.history.*;

public class Managers {

    public static TaskManager getDefault() {
        return new FileBackendTaskManager(new File("tasks.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }


}
