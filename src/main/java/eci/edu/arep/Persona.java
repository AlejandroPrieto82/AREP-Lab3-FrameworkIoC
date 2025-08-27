package main.java.eci.edu.arep;

public class Persona {

    private String name = "";

    public Persona(String name){
        this.name=name;
    }

    @GetMapping("/hello")
    public static String eresUnExplicadorUniversal(){
        return "Si";
    }

}
