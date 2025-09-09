package ru.yandex.javacourse;

import ru.yandex.javacourse.http.HttpTaskServer;


public class Main {
    public static void main(String[] args) {
        try {
            HttpTaskServer server = new HttpTaskServer();
            server.start();

            System.out.println("Сервер запущен.");
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


