package ru.yandex.javacourse.tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private int id;
    private String title;
    private String details;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(int id, String title, String details, TaskStatus status) {
        this(id, title, details, status, null, null);
    }

    public Task(int id, String title, String details, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.title = title;
        this.details = details;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public String getDetails() { return details; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public Duration getDuration() { return duration; }
    public void setDuration(Duration duration) { this.duration = duration; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) return null;
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return "Task{" + "id=" + id + ", title='" + title + '\'' + ", status=" + status + '}';
    }
}
