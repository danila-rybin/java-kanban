package ru.yandex.javacourse.http;

import com.google.gson.Gson;
import ru.yandex.javacourse.manager.Managers;
import ru.yandex.javacourse.manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.http.HttpClient;

public class HttpTaskServerTestMain {
    protected TaskManager manager;
    protected HttpTaskServer taskServer;
    protected Gson gson;
    protected HttpClient client;

    @BeforeEach
    public void setUp() throws IOException {
        manager = Managers.getDefault();
        try {
            taskServer = new HttpTaskServer();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        gson = Managers.getGson();
        client = HttpClient.newHttpClient();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

}