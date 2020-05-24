package battleship.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;
import java.io.ObjectOutputStream;

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
                try {
                    outputStream.writeObject(message);
                    return message;
                } catch (IOException ex) {
                    throw ex;
                }
            }
        };
    }
}
