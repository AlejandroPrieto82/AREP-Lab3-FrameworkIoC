// ---------------- HttpServer.java ----------------
package eci.edu.arep.httpserver;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import eci.edu.arep.microspringboot.annotations.GetMapping;
import eci.edu.arep.microspringboot.annotations.RequestParam;
import eci.edu.arep.microspringboot.annotations.RestController;
import eci.edu.arep.util.Service;

public class HttpServer {

    private static final Map<String, Method> services = new HashMap<>();
    private static final Map<String, Object> controllers = new HashMap<>();
    private static final Map<String, Service> getRoutes = new HashMap<>();
    private static final int PORT = 35000;
    private static String rootFiles = "www";

    // ----------- FRAMEWORK IoC -----------
    public static void loadServices(String[] args) {
        try {
            Class<?> c = Class.forName(args[0]);
            System.out.println("Inicio carga de componentes: " + args[0]);

            if (c.isAnnotationPresent(RestController.class)) {
                Object controller = c.getDeclaredConstructor().newInstance();
                controllers.put(c.getName(), controller);

                for (Method m : c.getDeclaredMethods()) {
                    if (m.isAnnotationPresent(GetMapping.class)) {
                        String mapping = m.getAnnotation(GetMapping.class).value();
                        System.out.println("Cargando método: " + m.getName() + " -> " + mapping);
                        services.put(mapping, m);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // ----------- API tipo Express -----------
    public static void get(String path, Service service) {
        getRoutes.put(path, service);
    }

    public static void staticfiles(String path) {
        rootFiles = path;
    }

    // ----------- CORE DEL SERVIDOR -----------
    public static void runServer(String[] args) throws IOException, URISyntaxException {
        loadServices(args);

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor escuchando en puerto " + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            handleRequest(clientSocket);
        }
    }

    private static void handleRequest(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        OutputStream rawOut = clientSocket.getOutputStream();
        PrintWriter out = new PrintWriter(rawOut, true);

        try {
            String inputLine = in.readLine();
            if (inputLine == null || inputLine.isEmpty()) return;

            String[] requestParts = inputLine.split(" ");
            String method = requestParts[0];
            String rawPath = requestParts[1];

            // Consumir headers
            while ((inputLine = in.readLine()) != null && !inputLine.isEmpty()) {}

            URI requri = URI.create(rawPath);
            HttpRequest req = new HttpRequest(requri);
            HttpResponse res = new HttpResponse();
            String route = requri.getPath();

            String responseText;
            if (services.containsKey(route)) {
                responseText = invokeService(requri);
                out.println(responseText);
            } else if (getRoutes.containsKey(route)) {
                String output = getRoutes.get(route).executeService(
                        new eci.edu.arep.util.Request(requri),
                        new eci.edu.arep.util.Response(out)
                );
                out.println("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n" + output);
            } else {
                if (route.equals("/")) route = "/index.html";
                serveStaticFile(route, rawOut, out);
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/html\r\n\r\n<h1>500 Error</h1>");
        } finally {
            out.close();
            in.close();
            clientSocket.close();
        }
    }

    // ----------- INVOCAR CON REFLEXIÓN -----------
    private static String invokeService(URI requri) throws Exception {
        HttpRequest req = new HttpRequest(requri);
        String servicePath = requri.getPath();

        Method m = services.get(servicePath);
        if (m == null) return "HTTP/1.1 404 Not Found\r\n\r\n<h1>404 Not Found</h1>";

        Object controller = controllers.get(m.getDeclaringClass().getName());

        Parameter[] methodParams = m.getParameters();
        Object[] params = new Object[methodParams.length];
        for (int i = 0; i < methodParams.length; i++) {
            if (methodParams[i].isAnnotationPresent(RequestParam.class)) {
                RequestParam rp = methodParams[i].getAnnotation(RequestParam.class);
                String val = req.getValue(rp.value(), rp.defaultValue());
                params[i] = val;
            }
        }

        String result = (String) m.invoke(controller, params);
        return "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n" + result;
    }

    // ----------- ARCHIVOS ESTÁTICOS -----------
    private static void serveStaticFile(String path, OutputStream rawOut, PrintWriter out) throws IOException {
        InputStream resourceStream = HttpServer.class.getClassLoader().getResourceAsStream(rootFiles + path);

        if (resourceStream != null) {
            String mimeType = guessMimeType(path);
            byte[] fileBytes = resourceStream.readAllBytes();

            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: " + mimeType);
            out.println("Content-Length: " + fileBytes.length);
            out.println();

            rawOut.write(fileBytes);
            rawOut.flush();
            resourceStream.close();
        } else {
            String errorMessage = "<h1>404 Not Found</h1>";
            out.println("HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\n\r\n" + errorMessage);
        }
    }

    private static String guessMimeType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        return "application/octet-stream";
    }
}
