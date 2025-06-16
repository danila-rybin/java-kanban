package tracker;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task t1 = new Task(0, "Сделать домашку", "Написать отчёт", TaskStatus.NEW);
        Task t2 = new Task(0, "Пойти в магазин", "Купить хлеб", TaskStatus.NEW);

        manager.addTask(t1);
        manager.addTask(t2);

        Epic epic = new Epic(0, "Проект", "Важный проект");
        manager.addEpic(epic);

        Subtask s1 = new Subtask(0, "Подзадача 1", "Описание", TaskStatus.NEW, epic.getId());
        Subtask s2 = new Subtask(0, "Подзадача 2", "Описание", TaskStatus.NEW, epic.getId());

        manager.addSubtask(s1);
        manager.addSubtask(s2);

        System.out.println("Задачи: " + manager.getAllTasks());
        System.out.println("Эпики: " + manager.getAllEpics());
        System.out.println("Подзадачи: " + manager.getAllSubtasks());
    }
}
//
