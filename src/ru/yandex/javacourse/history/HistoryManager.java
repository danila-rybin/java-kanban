package ru.yandex.javacourse.history;

import ru.yandex.javacourse.tasks.Task;
import java.util.List;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);
    List<Task> getHistory();
    void clear();
}
