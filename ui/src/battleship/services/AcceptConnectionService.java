package battleship.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Сервис, ожидающие подключения клиента в отдельном потоке
 * Сервисы позволяют задавать действия, выполняемые в UI потоке после своего успешного завершения/ошибки
 */
public class AcceptConnectionService extends Service<Socket> {
    private int port;
    private ServerSocket serverSocket;

    public void connect(int port) {
        this.port = port;
        restart();
    }

    @Override
    protected Task<Socket> createTask() {
        return new Task<>() {
            private final int port = AcceptConnectionService.this.port;
            private ServerSocket serverSocket;

            @Override
            protected Socket call() throws Exception {
                serverSocket = new ServerSocket(port);
                AcceptConnectionService.this.serverSocket = serverSocket;
                return serverSocket.accept();
            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                if (mayInterruptIfRunning)
                    try {
                        // закрытие сокета пробудит заблокированный поток
                        serverSocket.close();
                        return true;
                    } catch (IOException ignored) {
                    }

                return super.cancel(mayInterruptIfRunning);
            }
        };
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
}
