package main.java.eci.edu.arep.httpServer;

public interface Service {
    
    public String invoke(HttpRequest req, HttpResponse res);
    
}
