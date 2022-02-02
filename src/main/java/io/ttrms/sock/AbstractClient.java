package io.ttrms.sock;

import lombok.*;

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
public abstract class AbstractClient implements IClient {
    protected final List<Consumer<Request>> listeners = new ArrayList<>();
    protected final ConcurrentHashMap<String, Consumer<Response>> pendingRequests = new ConcurrentHashMap<>();
    protected String prefix = "/";
    protected String separator = " ";
    protected SocketWrap socket;

    protected AbstractClient(Socket socket) throws IOException {
        setSocket(new SocketWrap(socket, new ObjectOutputStream(socket.getOutputStream()), new ObjectInputStream(socket.getInputStream())));
    }

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
        getSocket().getOut().writeObject(String.format("%s%s%s", prefixed, separator, String.join(separator, request.getArgs())));
    }

    @Override
    public void run() {
        beforeStart();
        while (getSocket().getSocket().isConnected()) {
            try {
                var input = (String) getSocket().getIn().readObject();
                var req = new Request(input.split(Pattern.quote(" ")));
                this.listeners.forEach(listener -> listener.accept(req));
            } catch (IOException | ClassNotFoundException ex) {
                onClose(ex);
                break;
            }
        }
    }
}
