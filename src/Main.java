package ru.yandex.javacourse;

import ru.yandex.javacourse.manager.*;
import ru.yandex.javacourse.tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task t1 = new Task(0, "Сделать домашку", "Написать отчет", TaskStatus.NEW);
        Task t2 = new Task(0, "Пойти в магазин", "Купить хлеб", TaskStatus.NEW);

        manager.addTask(t1);
        manager.addTask(t2);

        Epic epic = new Epic(0, "Проект", "Важный проект");
        manager.addEpic(epic);

        Subtask s1 = new Subtask(0, "Подзадача 1", "Описание", TaskStatus.NEW, epic.getId());
        Subtask s2 = new Subtask(0, "Подзадача 2", "Описание", TaskStatus.NEW, epic.getId());

        manager.addSubtask(s1);
        manager.addSubtask(s2);

        // просмотр задач для истории
        manager.getTask(t1.getId());
        manager.getEpic(epic.getId());
        manager.getSubtask(s1.getId());

        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Subtask subtask : manager.getSubtasksForEpic(epic.getId())) {
                System.out.println("--> " + subtask);
            }
        }
        System.out.println("Подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История просмотров:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
