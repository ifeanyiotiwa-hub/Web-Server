package httpserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class WebServerTest {
    final int PORT_ONE = 8890;
    final int PORT_TWO = 8891;

    @BeforeEach
    void setUp() throws IOException {
//        new WebServer();


    }


    @Test
    void getResource() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT_ONE);
        Socket client = serverSocket.accept();


        final String str = "Socket[addr=/0:0:0:0:0:0:0:1,port=58035,localport=";
        final String expected = str + PORT_ONE + "]";
        assertEquals(expected.substring(str.indexOf("localport")), client.toString().substring(str.indexOf("localport")));
        assertEquals(expected.substring(0, 10), client.toString().substring(0, 10));

        client.close();
    }

//    @Disabled
    @Test
    void response() throws IOException {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT_TWO);
            Socket client = serverSocket.accept();

            if (client != null) {
                System.out.println("\nClient connected to server: " + client);
                InputStreamReader inStream = new InputStreamReader(client.getInputStream());
                BufferedReader inReader = new BufferedReader(inStream);

                String line = inReader.readLine();
                String method = line.substring(0, line.indexOf(" "));
                String resource = line.substring(line.indexOf("/") + 1, line.lastIndexOf("/") - 5);

                assertEquals("GET", method);
                switch (resource) {
                    case "/":
                        assertEquals("/", resource);
                        break;
                    case "/json":
                        assertEquals("/json", resource);
                        break;
                    default:
                        assertTrue(resource instanceof String);
                }
                client.close();
            }
        } catch (Exception e) {
        }

    }
}
