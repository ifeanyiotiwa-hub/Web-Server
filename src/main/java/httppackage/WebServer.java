package httppackage;
import jdk.jfr.ContentType;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

public class WebServer implements Runnable{
    private ServerSocket serverSocket;
    public final static int PORT = 8890;

    public WebServer(int port){
        try{
            serverSocket = new ServerSocket(port);
        }
        catch(Exception e){
            System.out.println("Unable to initialize HTTP server: " + e.getLocalizedMessage());
        }
        if(serverSocket == null){
            System.exit(1);
        }

        new Thread(this).start();

        System.out.println("HTTP server is LIVE on port: " + port);

        }

    public WebServer (){
        this(PORT);
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
                    System.out.println(resource + " in run()");
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
        System.out.println(tempRes);
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


    /***
     * checking for resource to parse
     * @param resource String
     * @param method (String)
     * @param client Socket
     * @throws IOException
     */
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



    /***
     * Closes Socket
     * @param client: Socket
     */
    private void closeSocket(Socket client) {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(client);
    }


    /***
     *
     * @param filePath
     * @param client
     * @param contentType
     */
    private void fileService(String filePath, Socket client, String contentType) throws IOException{
        PrintStream outStream = new PrintStream(client.getOutputStream(), true);
        File fileToSend = new File(filePath);

        //Troubleshooting
        System.out.println(fileToSend.exists() ? fileToSend : "error parsing file");

        if(fileToSend.exists()){
            outStream.println("HTTP/1.1 200 OK" + "\r\n");
            outStream.println(contentType + "\r\n");
            outStream.println("Content-Length: " + fileToSend.length() + "\r\n");

            FileInputStream fileInStream = new FileInputStream(fileToSend);
            byte[] bytes = new byte[fileInStream.available()];
            fileInStream.read(bytes);
            outStream.write(bytes);
            outStream.close();
            fileInStream.close();

        }
        else{
            String errorMessage = "HTTP/1.1 404 Not Found\r\n" +
                    "Content-Type: text/html\r\n\r\n" +
                    "<h1 style=\"font-size: 40px;\"> Error 404</h1>";
            outStream.write(errorMessage.getBytes(StandardCharsets.UTF_8));
            outStream.close();
        }


    }


    public static void main(String[] args) {
        new WebServer();
        new WebServer(8080);
    }
}
