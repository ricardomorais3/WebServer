import com.sun.corba.se.spi.activation.Server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by codecadet on 08/11/16.
 */
public class NewWebServer {

    static final String PATH = "resources/";
    static private File file;

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            Socket clientSocket;

            while (true) {

                clientSocket = serverSocket.accept();

                Thread thread = new Thread(new ClientConnection(clientSocket));
                thread.start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
