package ru.yandex.javacourse;

import ru.yandex.javacourse.manager.*;
import ru.yandex.javacourse.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault(); // возвращает FileBackendTaskManager

        // Создаём задачи
        Task t1 = new Task(0, "Сделать домашку", "Написать отчет", TaskStatus.NEW,
                Duration.ofHours(2), LocalDateTime.now().plusHours(1));
        Task t2 = new Task(0, "Пойти в магазин", "Купить хлеб", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(4));

        manager.addTask(t1);
        manager.addTask(t2);

        // Создаём эпик
        Epic epic = new Epic(0, "Проект", "Важный проект");
        manager.addEpic(epic);

        // Создаём подзадачи для эпика
        Subtask s1 = new Subtask(0, "Подзадача 1", "Описание", TaskStatus.NEW,
                epic.getId(), Duration.ofHours(1), LocalDateTime.now().plusHours(2));
        Subtask s2 = new Subtask(0, "Подзадача 2", "Описание", TaskStatus.NEW,
                epic.getId(), Duration.ofHours(3), LocalDateTime.now().plusHours(5));

        manager.addSubtask(s1);
        manager.addSubtask(s2);

        // Просмотр задач для истории
        manager.getTask(t1.getId());
        manager.getEpic(epic.getId());
        manager.getSubtask(s1.getId());

        // Печать всех задач и истории
        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("=== Задачи ===");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\n=== Эпики ===");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Subtask subtask : manager.getSubtasksForEpic(epic.getId())) {
                System.out.println("--> " + subtask);
            }
        }

        System.out.println("\n=== Подзадачи ===");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("\n=== История просмотров ===");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
/

