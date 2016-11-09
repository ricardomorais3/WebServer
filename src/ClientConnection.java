import sun.jvm.hotspot.runtime.Thread;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Created by codecadet on 09/11/16.
 */
public class ClientConnection implements Runnable {

    static final String PATH = "resources/";
    private File file;
    private Socket clientSocket;

    public ClientConnection(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        System.out.println(java.lang.Thread.currentThread().getName());

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String headerClient = bufferedReader.readLine();

            System.out.println("o pedido do meu client é "+headerClient);
            if (headerClient == null) {
                clientSocket.close();
                return;
            }
            String getFileName = headerClient.split(" ")[1].substring(1);

            if (getFileName.equals("")) {
                getFileName = "index.html";
            }
            file = new File(PATH + getFileName);
            String header = buildHeader();

            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            dataOutputStream.writeBytes(header);
            System.out.println("The following file will be sent: " + PATH + file.getName());
            dataOutputStream.write(Files.readAllBytes(Paths.get(PATH + file.getName())));

            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String buildHeader() {

        String statusCode;
        String fileType = "";
        long fileSize = file.length();

        if (!file.exists()) {
            file = new File(PATH + "404.html");
            statusCode = "404 Not Found";
        } else {
            statusCode = "200 Document Follows";
        }

        String fileExtension = file.getName().split("\\.")[1];

        switch (fileExtension) {
            case "html":
                fileType = "text/html";
                break;
            case "ico":
                fileType = "image/x-icon";
                break;
            case "png":
                fileType = "image/png";
                break;
            case "jpg":
                fileType = "image/jpg";
                break;
            default:
                System.out.println("Something went terribly wrong...");
        }

        return "HTTP/1.1 " + statusCode + "\\r\\n\n" +
                "Content-Type: " + fileType + "; charset=UTF-8\\r\\n\n" +
                "Content-Length: " + fileSize + "\\r\\n\n" +
                "\\r\\n\n\n";
    }
}
