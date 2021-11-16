package httpserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class WebServerTest {

    @BeforeEach
    void setUp() throws IOException {
        ServerSocket ss = new ServerSocket(8080);
        Socket client = ss.accept();

    }

    @Test
    void run() {
    }

    @Test
    void getResource() {

    }

    @Test
    void getMethod() {
    }

    @Test
    void response() throws IOException {
        ServerSocket ss = new ServerSocket(8080);
        Socket client = ss.accept();
    }

    @Test
    void closeSocket() {
    }

    @Test
    void fileService() {
    }
}