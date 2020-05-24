package battleship.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;
import java.io.ObjectInputStream;

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
                try {
                    return inputStream.readObject();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        };
    }
}
