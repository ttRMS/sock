package io.ttrms.sock;

import lombok.Data;

import java.util.List;

@Data
public class Request {
    private final String route;
    private final String[] args;

    /**
     * Create a new <code>Request</code> object.
     * The first argument is the request route, the rest are the request arguments.
     */
    public Request(String... args) {
        this(List.of(args));
    }

    /**
     * Create a new <code>Request</code> object.
     * The first argument is the request route, the rest are the request arguments.
     */
    public Request(List<String> args) {
        this.route = args.get(0);
        this.args = args.size() > 1 ? args.subList(1, args.size()).toArray(new String[0]) : new String[0];
    }
}
