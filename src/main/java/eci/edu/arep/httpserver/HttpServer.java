package eci.edu.arep.httpserver;

import eci.edu.arep.microspringboot.annotations.GetMapping;
import eci.edu.arep.microspringboot.annotations.RequestParam;
import eci.edu.arep.microspringboot.annotations.RestController;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

public class HttpServer {

    private static class ServiceDefinition {
        Object instance;
        Method method;

        ServiceDefinition(Object instance, Method method) {
            this.instance = instance;
            this.method = method;
        }
    }

    private static final Map<String, ServiceDefinition> services = new HashMap<>();

    public static void loadServices(String[] args) {
        try {
            Class<?> c = Class.forName(args[0]);
            if (c.isAnnotationPresent(RestController.class)) {
                Object controller = c.getDeclaredConstructor().newInstance();
                for (Method m : c.getDeclaredMethods()) {
                    if (m.isAnnotationPresent(GetMapping.class)) {
                        String mapping = m.getAnnotation(GetMapping.class).value();
                        services.put(mapping, new ServiceDefinition(controller, m));
                        System.out.println("Cargado servicio: " + mapping);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runServer(String[] args) throws IOException, URISyntaxException {
        loadServices(args);

        ServerSocket serverSocket = new ServerSocket(35000);
        while (true) {
            try (Socket clientSocket = serverSocket.accept();
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                String inputLine;
                boolean firstLine = true;
                URI requri = null;

                while ((inputLine = in.readLine()) != null) {
                    if (firstLine) {
                        requri = new URI(inputLine.split(" ")[1]);
                        firstLine = false;
                    }
                    if (!in.ready()) break;
                }

                String outputLine;
                if (requri != null && requri.getPath().startsWith("/app")) {
                    outputLine = invokeService(requri);
                } else {
                    outputLine = defaultResponse();
                }

                out.println(outputLine);
            }
        }
    }

    private static String invokeService(URI requri) {
        try {
            HttpRequest req = new HttpRequest(requri);
            HttpResponse res = new HttpResponse();

            String servicePath = requri.getPath().substring(4);
            ServiceDefinition def = services.get(servicePath);
            if (def == null) {
                res.setStatus("404 Not Found");
                res.setBody("<h1>404 - Not Found</h1>");
                return res.buildResponse();
            }

            Method m = def.method;
            Object[] args = new Object[m.getParameterCount()];

            Parameter[] params = m.getParameters();
            for (int i = 0; i < params.length; i++) {
                RequestParam rp = params[i].getAnnotation(RequestParam.class);
                if (rp != null) {
                    args[i] = req.getValue(rp.value(), rp.defaultValue());
                }
            }

            Object result = m.invoke(def.instance, args);
            res.setBody(result.toString());
            return res.buildResponse();

        } catch (Exception e) {
            e.printStackTrace();
            return "HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/html\r\n\r\n<h1>500 - Error interno</h1>";
        }
    }

    public static String defaultResponse() {
        return "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n" +
                "<h1>Servidor funcionando!</h1><p>Intenta ir a <a href=\"/app/greeting\">/app/greeting</a></p>";
    }
}
