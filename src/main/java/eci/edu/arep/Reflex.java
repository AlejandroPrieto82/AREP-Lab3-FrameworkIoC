package main.java.eci.edu.arep;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class Reflex {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        Class c = Class.forName("main.java.eci.edu.arep.Persona");
        System.out.println("Lista de metodos de: "+ c.getCanonicalName());

        Method[] methods = c.getDeclaredMethods();

        for (Method m : methods) {
            System.out.println(m.getName() + ": ");
            Parameter[] parameters = m.getParameters();
            for(Parameter p : parameters){
                System.out.print(p.getType() + ", ");
            }
            System.out.println("\n\r");
        }
        
        Method m2 = c.getMethod("eresUnExplicadorUniversal");
        if(m2.isAnnotationPresent(GetMapping.class)){
            System.out.println("¿Eres un explicador Universal? "+ m2.invoke(null) );
            String mapping = m2.getAnnotation(GetMapping.class).value();
            System.out.println("El mapeo es: "+ mapping);
        }
        

    }


}
