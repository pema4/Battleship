package battleship.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.net.Socket;

/**
 * Сервис, выполняющий подключение к серверу в отдельном потоке
 * Сервисы позволяют задавать действия, выполняемые в UI потоке после своего успешного завершения/ошибки
 */
public class ConnectionService extends Service<Socket> {
    private String host;
    private int port;

    public void connect(String host, int port) {
        this.host = host;
        this.port = port;
        restart();
    }

    @Override
    protected Task<Socket> createTask() {
        return new Task<>() {
            private final String host = ConnectionService.this.host;
            private final int port = ConnectionService.this.port;

            @Override
            protected Socket call() throws Exception {
                return new Socket(host, port);
            }
        };
    }
}
