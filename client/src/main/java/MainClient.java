import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Paths;

public class MainClient {

    private static void serializable() {
        try (Socket socket = new Socket("localhost", 8085)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            TempFileMessage tmp = new TempFileMessage(Paths.get("client/files/example.txt"));
            out.writeObject(tmp);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private static void binary() {
//        try (Socket socket = new Socket("localhost", 8085)) {
//            byte[] bytes = {65, 66, 67, 49, 50, 51};
//            socket.getOutputStream().write(bytes);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) {
        serializable();
    }
}
