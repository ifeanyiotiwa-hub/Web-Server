package httpserver;

import httpserverservice.HttpServerImpl;

import java.io.*;
import java.net.*;

public class WebServer implements Runnable, HttpServerImpl {
    private ServerSocket serverSocket;
    public final static int DEFAULT_PORT = 8890;

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
        Thread webServerThreadOne = new Thread(this);
        Thread webServerThreadTwo = new Thread(this);

        webServerThreadTwo.setName("WEB SERVER TWO");
        webServerThreadOne.setName("WEB SERVER ONE");

        webServerThreadOne.start();

//        if(webServerThreadOne.isAlive())
//            webServerThreadTwo.start();


        System.out.println("HTTP server is LIVE on port: " + port);

        }

    public WebServer (){
        this(DEFAULT_PORT);
        }

    /**
     * Start listening at PORT given Port;
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
                    String method = this.getMethod(line);
                    String resource = this.getResource(line);
//                    System.out.println(resource + " in run()");
//
//                    System.out.println(method + " is running");

                    while((line = inReader.readLine()) != null){
                        if (line.equals("")) break;
                    }

                    System.out.println(Thread.currentThread().getName() + " is Running");
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
    public String getResource(String line) throws UnsupportedEncodingException {
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
    public String getMethod(String line) {
        String method = line.substring(0, line.indexOf(" "));
        return method;
    }


    /***
     * checking for resource to parse
     * @param resource String
     * @param method (String)
     * @param client Socket
     * @throws IOException
     */
    public void response(String resource, String method, Socket client) throws IOException{
        System.out.println(Thread.currentThread().getName() + " is handling " + client);
        String temp = this.getClass().getResource("../").getFile();
        System.out.println(temp);
        String ROOT = temp.substring(0, temp.indexOf("/target/classes")) + "/resources/";
        System.out.println(ROOT);
        switch(resource){
            case "":
            case "/":
            case "index.html":
                String indexPath = URLDecoder.decode(ROOT + "webroot/index.html", "UTF-8");
                fileService(indexPath, client, "Content-Type: text/html;charset=UTF-8");
                break;
            case "json":
                String jsonPath = URLDecoder.decode(ROOT + "json.json", "UTF-8");
                fileService(jsonPath, client, "Content-Type: application/json;charset=UTF-8");
                break;
            default:
                String errorPath = URLDecoder.decode(ROOT + "error.html", "UTF-8");
                fileService(errorPath, client, "Content-Type: text/html;charset=UTF-8");
        }
        closeSocket(client);
    }



    /***
     * Closes Socket
     * @param client: Socket
     */
    public void closeSocket(Socket client) {
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
    public void fileService(String filePath, Socket client, String contentType) throws IOException{
        PrintStream outStream = new PrintStream(client.getOutputStream(), true);
        File fileToSend = new File(filePath);

        //Troubleshooting
        System.out.println(fileToSend);
        System.out.println(fileToSend.exists());

        if(fileToSend.exists() && !fileToSend.isDirectory()){
            outStream.println("HTTP/1.0 200 OK");
            outStream.println(contentType);
            outStream.println("Content-Length: " + fileToSend.length());
            outStream.println();

            FileInputStream fileInStream = new FileInputStream(fileToSend);
            byte[] bytes = new byte[fileInStream.available()];
            fileInStream.read(bytes);
            outStream.write(bytes);
            outStream.close();
            fileInStream.close();

        }
        else {
            String errorMessage = "HTTP/1.1 404 Not Found\r\n" +
                    "Content-Type: text/html\r\n" + "Content-Length: 23\r\n" + "\r\n" +
                    "<h1 style=\"font-size: 40px;\"> Error 404</h1>";
            outStream.write(errorMessage.getBytes());
            outStream.close();
        }
    }
}
