import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by codecadet on 08/11/16.
 */
public class NewWebServer {

    public static void main(String[] args) {

        ExecutorService pool = Executors.newFixedThreadPool(10);

        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            Socket clientSocket;

            while (true) {

                clientSocket = serverSocket.accept();

                pool.submit(new ClientConnection(clientSocket));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
