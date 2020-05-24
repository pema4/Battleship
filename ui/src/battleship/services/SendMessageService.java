package battleship.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Сервис, отправляющий объект сопернику в отдельном потоке и возвращающий отправленное сообщение
 * Сервисы позволяют задавать действия, выполняемые в UI потоке после своего успешного завершения/ошибки
 */
public class SendMessageService extends Service<Object> {
    private final ObjectOutputStream outputStream;
    private Object message;

    public SendMessageService(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void send(Object message) {
        this.message = message;
        restart();
    }

    @Override
    protected Task<Object> createTask() {
        return new Task<>() {
            private final Object message = SendMessageService.this.message;
            @Override
            protected Object call() throws Exception {
                outputStream.writeObject(message);
                return message;
            }
        };
    }
}
