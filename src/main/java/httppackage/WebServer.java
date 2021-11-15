package httppackage;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class WebServer implements Runnable{
    private ServerSocket serverSocket;
    public final static int PORT = 8890;

    public WebServer(){
        try{
            serverSocket = new ServerSocket(PORT);
        }
        catch(Exception e){
            System.out.println("Unable to initialize HTTP server: " + e.getLocalizedMessage());
        }
        if(serverSocket == null){
            System.exit(1);
        }

        new Thread(this).start();

        System.out.println("HTTP server is LIVE on port: " + PORT);

        }

    /**
     * Start listening at PORT 8890
     */
    @Override
    public void run() {
        while(true){
            try {
                Socket client = serverSocket.accept();

                if(client != null){
                    System.out.println("\nClient connected to server: " + client);
                    InputStreamReader inStream = new InputStreamReader(client.getInputStream());
                    BufferedReader inReader = new BufferedReader(inStream);

                    String line = inReader.readLine();
                    String resource = this.getResource(line);
                    String method = this.getMethod(line);

                    while((line = inReader.readLine()) != null){
                        if (line.equals(" ")) break;
                    }

                    System.out.println("Resource client has requested: " + resource);
                    System.out.println("Type of request: " + method);
                    this.response(resource, method, client);
                }
            } catch(Exception e){
                System.out.println("HTTP server error: " + e.getLocalizedMessage());
            }
        }

    }



    /**
     * getResource() request resource from URL
     * @param line
     * @return UTF encoded HTTP Resource
     * @throws UnsupportedEncodingException
     */
    private String getResource(String line) throws UnsupportedEncodingException {
        String tempRes = line.substring(line.indexOf("/") + 1, line.lastIndexOf("/") - 5);
        tempRes = URLDecoder.decode(tempRes, "UTF-8");
        return tempRes;
    }

    /**
     * getMethod() returns HTTP method
     * @param line
     * @return
     */
    private String getMethod(String line) {
        String method = new StringTokenizer(line).nextElement().toString();
        return method;
    }

    private void response(String resource, String method, Socket client) throws IOException{
        String ROOT = this.getClass().getResource("resources/").getFile();
        switch(resource){
            case "index.html":
                String indexPath = URLDecoder.decode(ROOT + "webroot/index.html", "UTF-8");
                fileService(indexPath, client, "Content-Type: text/html;charset=UTF-8");
                break;
            case "json":
                String jsonPath = URLDecoder.decode(ROOT + "json.json", "UTF-8");
                fileService(jsonPath, client, "Content-Type: text/json;charset=UTF-8");
                break;
            default:
                String errorPath = URLDecoder.decode(ROOT + "error.html", "UTF-8");
                fileService(errorPath, client, "Content-Type: text/html;charset=UTF-8");
                break;
        }
        closeSocket(client);
    }
}
