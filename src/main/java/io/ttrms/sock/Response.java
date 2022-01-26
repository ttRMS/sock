package io.ttrms.sock;

import lombok.Data;

@Data
public class Response {
    private final boolean success;
    private final String message;
}
