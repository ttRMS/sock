package io.ttrms.sock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@Getter
@Setter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractClient implements IClient {
    protected final List<Consumer<Request>> listeners = new ArrayList<>();
    private final ConcurrentHashMap<String, Consumer<Response>> pendingRequests = new ConcurrentHashMap<>();
    protected Socket socket;
    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    private String prefix = "/";
    private String separator = " ";

    protected abstract void beforeStart();

    protected abstract void onClose(Throwable ex);

    @Override
    public void addListener(Consumer<Request> listener) {
        this.listeners.add(listener);
    }

    @Override
    public void makeRequest(Request request, Consumer<Response> onComplete) throws IOException {
        var prefixed = ((!request.getRoute().startsWith(prefix)) ? prefix : "").concat(request.getRoute());
        pendingRequests.put(prefixed, onComplete);
        out.writeObject(String.format("%s%s%s", prefixed, separator, String.join(separator, request.getArgs())));
    }

    @Override
    public void run() {
        beforeStart();
        while (this.socket.isConnected()) {
            try {
                var input = (String) this.in.readObject();
                var req = new Request(input.split(Pattern.quote(" ")));
                this.listeners.forEach(listener -> listener.accept(req));
            } catch (IOException | ClassNotFoundException ex) {
                onClose(ex);
                break;
            }
        }
    }
}
