import httpserver.WebServer;
import httpserverservice.HttpServerImpl;

public class WebServerDemo {
    public static void main(String[] args) {

        HttpServerImpl myWebServer = new WebServer();
        System.out.println(myWebServer);
        System.out.println(Thread.activeCount());
    }
}
