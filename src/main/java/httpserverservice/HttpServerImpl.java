package httpserverservice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public interface HttpServerImpl {
    void fileService(String filePath, Socket client, String contentType) throws IOException;
    void closeSocket(Socket client);
    String getResource(String line) throws UnsupportedEncodingException;
    String getMethod(String line);
    void response(String resource, String method, Socket client) throws IOException;
}
