package vertx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;

import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.WebApplication;

public class RestRequestHandler {

    /**
     * The data read from the HTTP request.
     */
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    
    /**
     * The HTTP server request that is currently being handled.
     */
    HttpServerRequest req;
    
    /**
     * The Jersey Web Application that will handle the request.
     */
    WebApplication app;
    
    /**
     * The base URI for the REST ws.
     */
    URI baseUri;
    
    public RestRequestHandler(URI baseUri, WebApplication app) {
        this.app = app;
        this.baseUri = baseUri;
    }
    
    public void handle(HttpServerRequest req) {
        this.req = req;
        req.dataHandler(new DataHandler());
        req.endHandler(new EndRequestHandler());
    }
    
    public class DataHandler implements Handler<Buffer> {

        @Override
        public void handle(Buffer buf) {
            byte[]data = buf.getBytes();
            try {
                stream.write(data);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        
    }
    
    public class EndRequestHandler implements Handler<Void> {

        @Override
        public void handle(Void event) {
            try {
                ContainerRequest creq = new ContainerRequest(
                        app, 
                        req.method, 
                        baseUri, 
                        new URI(req.uri),
                        getHeaders(req), 
                        new ByteArrayInputStream(stream.toByteArray())
                );

                app.handleRequest(creq, new RestResponseHandler(req));
            } catch (URISyntaxException | IOException e) {  
                e.printStackTrace();
            }
            
        }
        
    }
    
    private InBoundHeaders getHeaders(HttpServerRequest req) {
        InBoundHeaders headers = new InBoundHeaders();

        for (String name : req.getHeaderNames()) {
            List<String> value = new ArrayList<String>();
            value.add(req.getHeader(name));
            headers.put(name, value);
        }
    
        return headers;
    }
    
}
