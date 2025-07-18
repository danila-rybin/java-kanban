package ru.yandex.javacourse.tasks;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String title, String details, TaskStatus status, int epicId) {
        super(id, title, details, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask subtask)) return false;
        return super.equals(o) && epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", epicId=" + epicId +
                '}';
    }
}
