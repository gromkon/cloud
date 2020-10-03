import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainApp {

    private static void serializable() {
        try (ServerSocket sc = new ServerSocket(8085)) {
            System.out.println("Server listening");
            try (Socket socket = sc.accept();
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
            ) {
                TempFileMessage tmp = (TempFileMessage) in.readObject();
                Path path = Paths.get("server/files/" + tmp.getFileName());
                Files.write(path, tmp.getBytes());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

//    private static void binary() {
//        try (ServerSocket sc = new ServerSocket(8085)) {
//            System.out.println("Server listening");
//            try (Socket socket = sc.accept();
//                 BufferedInputStream in = new BufferedInputStream(socket.getInputStream())
//            ) {
//                System.out.println("Client is received");
//                int n;
//                while ((n = in.read()) != -1) {
//                    System.out.print((char)n);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) {
        serializable();
    }

}
