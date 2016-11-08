import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by codecadet on 08/11/16.
 */
public class WebServer {

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(8001);
            Socket clientSocket;

            while (true) {
                clientSocket = serverSocket.accept();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String headerClient;

                headerClient = bufferedReader.readLine();
                System.out.println(headerClient);

                String[] getSpecification = headerClient.split(" ");
                String resource = getSpecification[1].substring(1);
                if (resource.equals("")) {
                    resource = "index.html";
                }

                DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
                String header;

                header = buildHeader("resources/" + resource);
                System.out.println(header);

                clientSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static byte[] getBytes(String path) throws FileNotFoundException {

        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new FileNotFoundException();

    }


    public static String buildHeader(String resourcePath) {

        String statusCode;
        String fileType = "";
        long fileSize = 0;

        File file = new File(resourcePath);

        if (!file.exists()) {
            statusCode = "404 Not Found";

        } else {
            statusCode = "200 Document Follows";
            fileSize = file.length();
            System.out.println(file.getName());

            String extension = file.getName().split("\\.")[1];
            System.out.println(extension);

            switch(extension){
                case "html":
                    fileType = "text/html";
                    break;
                case "ico":
                    fileType = "image/x-icon";
                    break;
                default:
                    System.out.println("something went terribly wrong...");
            }

        }

        return "HTTP/1.1 " + statusCode + "\\r\\n\n" +
                "Content-Type: " + fileType + "; charset=UTF-8\\r\\n\n" +
                "Content-Length: " + fileSize + " \\r\\n\n" +
                "\\r\\n\n\n";

    }

    /*
    switch (getSpecification[1]) {
        case "/":
            header = "HTTP/1.1 200 Document Follows\\r\\n\n" +
                    "Content-Type: text/html; charset=UTF-8\\r\\n\n" +
                    "Content-Length: <file_byte_size>\\r\\n\n" +
                    "\\r\\n\n\n";
            dataOutputStream.writeBytes(header);
            dataOutputStream.write(getBytes("resources/index.html"));
            break;
        case "/favicon.ico":
            header = "HTTP/1.1 200 Document Follows\\r\\n\n" +
                    "Content-Type: image/x-icon; charset=UTF-8\\r\\n\n" +
                    "Content-Length: <file_byte_size>\\r\\n\n" +
                    "\\r\\n\n\n";
            dataOutputStream.writeBytes(header);
            dataOutputStream.write(getBytes("resources/favicon.ico"));
            break;
        default:
            header = "HTTP/1.1 404 Not Found";
            dataOutputStream.writeBytes(header);
    }
    */


}
