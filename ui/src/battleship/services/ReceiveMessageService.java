package battleship.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Сервис, получающий объект от соперника в отдельном потоке
 * Сервисы позволяют задавать действия, выполняемые в UI потоке после своего успешного завершения/ошибки
 */
public class ReceiveMessageService extends Service<Object> {
    private final ObjectInputStream inputStream;

    public ReceiveMessageService(ObjectInputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    protected Task<Object> createTask() {
        return new Task<>() {
            @Override
            protected Object call() throws Exception {
                return inputStream.readObject();
            }
        };
    }
}
