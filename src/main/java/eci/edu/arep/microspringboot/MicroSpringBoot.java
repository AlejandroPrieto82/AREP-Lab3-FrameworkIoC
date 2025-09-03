/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package eci.edu.arep.microspringboot;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import eci.edu.arep.httpserver.HttpServer;

/**
 *
 * @author luisdanielbenavidesnavarro
 */
public class MicroSpringBoot {

    public static void main(String[] args) {
        System.out.println("Starting MicroSpringBoot!");
        
        try{
            HttpServer.runServer(args);
        }catch(IOException  | URISyntaxException ex){
            Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
