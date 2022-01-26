package io.ttrms.sock;

import lombok.Data;

import java.util.List;

@Data
public class Request {
    private final String route;
    private final String[] args;

    public Request(String... args) {
        this.route = args[0];
        this.args = args.length > 1 ? List.of(args).subList(1, args.length).toArray(new String[0]) : new String[0];
    }

    public Request(List<String> args) {
        this.route = args.get(0);
        this.args = args.size() > 1 ? args.subList(1, args.size()).toArray(new String[0]) : new String[0];
    }
}
