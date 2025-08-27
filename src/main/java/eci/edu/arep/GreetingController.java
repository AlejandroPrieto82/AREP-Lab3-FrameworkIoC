package main.java.eci.edu.arep;


import java.util.concurrent.atomic.AtomicLong;
import main.java.eci.edu.arep.GetMapping;
import main.java.eci.edu.arep.RequestParam;
import main.java.eci.edu.arep.RestController;


@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@GetMapping("/greeting")
	public static String greeting() {
		return "Hola " ;//+ name;
	}
}