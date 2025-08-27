package main.java.eci.edu.arep.microspringboot.examples;

import main.java.eci.edu.arep.microspringboot.annotations.GetMapping;
import main.java.eci.edu.arep.microspringboot.annotations.RequestParam;
import main.java.eci.edu.arep.microspringboot.annotations.RestController;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@GetMapping("/greeting")
	public static String greeting() {
		return "Hola " ;//+ name;
	}
}
