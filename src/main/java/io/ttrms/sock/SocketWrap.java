package io.ttrms.sock;

import lombok.Data;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Data
public class SocketWrap {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
}
