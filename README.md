This project provides a simple integration between Vert.x and Jersey (JAX-RS
implementation).

Here is an example of how you can use it. For instance, if you want to make your
REST resources in the package com.example.resources accessible from /api:

```java
public class Demo implements VertxApp {
    
    public void start() {
        RouteMatcher rm = new RouteMatcher();

        try {
            rm.all(
                "/api/.*", 
                new RestHandler(new URI("http://localhost:8080/api/")), 
                "com.example.resources");
        } catch (URISyntaxException e) {
            // Exceptions are currently not handled by the project.
            e.printStackTrace();
        }        
        
        server = new HttpServer().requestHandler(rm).listen(8080);
    }
}
```