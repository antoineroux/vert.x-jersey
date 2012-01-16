package vertx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.core.MultivaluedMap;

import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;

import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseWriter;

public class RestResponseHandler implements ContainerResponseWriter {

    /**
     * The request that will be responded.
     */
    HttpServerRequest req;
    
    /**
     * The body of the response.
     */
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    RestResponseHandler(HttpServerRequest req) {
        this.req = req;
    }
    
    @Override
    public OutputStream writeStatusAndHeaders(long contentLength,
            ContainerResponse response) throws IOException {
        
        // Set status
        req.response.statusCode = response.getStatusType().getStatusCode();
        req.response.statusMessage = response.getStatusType().getReasonPhrase();
        
        // Set headers
        MultivaluedMap<String, Object> headers = response.getHttpHeaders();
        for(String key : headers.keySet()) {
            for (Object value : headers.get(key)) {
                req.response.putHeader(key, value);
            }
        }
        req.response.putHeader("Connection", "keep-alive");
        
        return out;
    }

    @Override
    public void finish() throws IOException {
        req.response.end(Buffer.create(out.toByteArray()));
    }

}
