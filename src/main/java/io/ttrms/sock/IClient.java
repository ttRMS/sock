package io.ttrms.sock;

import java.io.IOException;
import java.util.function.Consumer;

public interface IClient extends Runnable {
    void addListener(Consumer<Request> listener);

    void makeRequest(Request request, Consumer<Response> onComplete) throws IOException;

    default void start(String threadName) {
        new Thread(this, threadName).start();
    }
}
