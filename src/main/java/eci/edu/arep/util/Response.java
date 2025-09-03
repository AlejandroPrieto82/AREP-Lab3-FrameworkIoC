package eci.edu.arep.util;

import java.io.PrintWriter;

public class Response {
    private PrintWriter writer;

    public Response(PrintWriter writer) {
        this.writer = writer;
    }

    public void send(String body) {
        writer.println("HTTP/1.1 200 OK");
        writer.println("Content-Type: text/plain");
        writer.println("Content-Length: " + body.length());
        writer.println();
        writer.println(body);
    }
}

