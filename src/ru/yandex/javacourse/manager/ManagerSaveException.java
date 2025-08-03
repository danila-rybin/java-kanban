package ru.yandex.javacourse.manager;

// Добавить своё unchecked исключение для ошибок сохранения
    public class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(String message, Throwable cause) {
            super(message, cause);
        }
    }

