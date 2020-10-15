import Utils.DBService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public Server() {
        try (ServerSocket serverSocket = new ServerSocket(ConnectionData.getPORT())) {
            DBService.connect();
            System.out.println("Server online!");
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            DBService.disconnect();
        }
    }
}
