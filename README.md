# AREP - Lab 3: MicroSpringBoot Framework IoC

Este proyecto implementa un **servidor web ligero en Java** (similar a Apache) que permite:
- Servir archivos estáticos (`.html`, `.css`, `.js`, `.png`, `.jpeg`, …).
- Exponer servicios REST a partir de **POJOs** usando un **framework IoC** construido con reflexión.
- Manejar anotaciones propias:
  - `@RestController` para identificar controladores.
  - `@GetMapping` para mapear métodos a endpoints HTTP.
  - `@RequestParam` para extraer parámetros de consulta (`query params`).

Se construyó un prototipo mínimo inspirado en Spring Boot, que permite cargar un componente (`GreetingController`) y derivar una aplicación web desde él.

Repositorio: [AREP-Lab3-FrameworkIoC](https://github.com/AlejandroPrieto82/AREP-Lab3-FrameworkIoC)

---

## Getting Started

Estas instrucciones te permitirán obtener una copia del proyecto y ejecutarlo localmente para propósitos de desarrollo y pruebas.

### Prerequisites

Asegúrate de tener instalado:

- [Java 17+](https://adoptium.net/)
- [Maven](https://maven.apache.org/)

Verifica las versiones con:

```bash
java -version
mvn -version
````

### Installing

1. Clona el repositorio:

```bash
git clone https://github.com/AlejandroPrieto82/AREP-Lab3-FrameworkIoC.git
cd AREP-Lab3-FrameworkIoC
```

2. Compila el proyecto:

```bash
mvn clean package
```

---

## Running the server

Ejecuta el servidor indicando el controlador que quieres cargar:

```bash
java -cp target/classes eci.edu.arep.microspringboot.MicroSpringBoot eci.edu.arep.microspringboot.examples.GreetingController
```

El servidor arrancará en el puerto `35000`.

### Ejemplos

* Servicio dinámico con `@GetMapping` y `@RequestParam`:

```
http://localhost:35000/greeting
→ Hola World

http://localhost:35000/greeting?name=Juan
→ Hola Juan
```

* Archivos estáticos:

Coloca tus archivos en `src/main/resources/www/`.

Ejemplo:
`src/main/resources/www/img/elefante.jpeg`

Acceso en el navegador:

```
http://localhost:35000/img/elefante.jpeg
```

---

## Deployment

Para desplegar en otro sistema basta con:

1. Clonar el repositorio.
2. Ejecutar `mvn package`.
3. Correr el servidor con `java -cp target/classes ...`.

El servidor funciona en cualquier sistema con Java 17+.

---

## Design and Architecture

* **HttpServer**: núcleo del servidor web (maneja sockets, peticiones y respuestas).
* **HttpRequest & HttpResponse**: abstraen el manejo de requests y respuestas HTTP.
* **IoC Framework**:

  * Escanea el classpath buscando clases con `@RestController`.
  * Carga dinámicamente los métodos anotados con `@GetMapping`.
  * Inyecta parámetros con `@RequestParam`.
* **Static Files**: soporta la entrega de recursos estáticos (HTML, JS, CSS, imágenes).
* **Example Controller**: `GreetingController` implementa un servicio REST con `@GetMapping`.

El diseño sigue la estructura estándar de Maven:

```
src/
 └── main/
      ├── java/eci/edu/arep/
      │     ├── httpserver/         # Servidor y núcleo HTTP
      │     ├── microspringboot/    # Framework IoC y anotaciones
      │     └── util/               # Utilidades (MimeTypes, Request, Response)
      └── resources/www/            # Archivos estáticos
```

---

## Built With

* [Java 17](https://openjdk.org/) - Lenguaje principal
* [Maven](https://maven.apache.org/) - Gestión de dependencias y ciclo de vida

---

## Authors

* **Alejandro Prieto** - [GitHub](https://github.com/AlejandroPrieto82)

---

## License

Este proyecto está bajo la licencia MIT - ver el archivo [LICENSE](/LICENSE.md) para más detalles.

---

## Acknowledgments

* Inspirado en el funcionamiento de Spring Boot.
* Construido como parte del curso **AREP - Arquitecturas Empresariales** en la Escuela Colombiana de Ingeniería Julio Garavito.
