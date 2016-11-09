import java.io.*;
import java.net.Socket;

/**
 * Created by codecadet on 09/11/16.
 */
public class ClientConnection implements Runnable {

    private static final String PATH = "resources/";
    private static final String ERROR_FILE = "404.html";
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

            System.out.println("O header do meu client é " + headerClient);
            if (headerClient == null) {
                clientSocket.close();
                return;
            }

            String fileName = getFileName(headerClient);

            File file = new File(PATH + fileName);
            boolean fileExists = file.exists();
            if (!fileExists) {
                file = new File(PATH + ERROR_FILE);
            }
            String header = buildHeader(file, fileExists);

            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            dataOutputStream.writeBytes(header);
            dataOutputStream.write(convertToBytes(file));

            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] convertToBytes(File file) {

        FileInputStream fileInputStream;
        ByteArrayOutputStream byteArrayOutputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, read);
            }

            fileInputStream.close();
            byteArrayOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(byteArrayOutputStream!=null){
            return byteArrayOutputStream.toByteArray();
        }
        return null;
    }

    private String getFileName(String headerClient) {
        String temp = headerClient.split(" ")[1].substring(1);
        if (temp.equals("")) {
            temp = "index.html";
        }
        return temp;
    }

    public String buildHeader(File file, boolean fileExists) {

        long fileSize = file.length();
        String statusCode = (fileExists) ? "200 Document Follows" : "404 Not Found";

        return "HTTP/1.1 " + statusCode + "\r\n" +
                "Content-Type: " + getFileType(file) + "; charset=UTF-8\r\n" +
                "Content-Length: " + fileSize + "\r\n" +
                " \n\n";
    }

    private String getFileExtension(File file) {
        return file.getName().split("\\.")[1];
    }

    private String getFileType(File file) {

        String fileExtension = getFileExtension(file);
        switch (fileExtension) {
            case "html":
                return "text/" + fileExtension;
            case "ico":
            case "png":
            case "jpg":
                return "image/" + fileExtension;
            default:
                System.out.println("Something went terribly wrong...");
                return null;
        }
    }

}
