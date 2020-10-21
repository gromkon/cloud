import Messages.Answers.Answer;
import Messages.Answers.AnswerType;
import Messages.Commands.*;
import Messages.File.TempFileMessage;
import Messages.Base.Message;
import Statuses.AuthStatus;
import Statuses.RegStatus;
import Utils.DBService;
import Visitors.DeleteDirVisitor;
import Visitors.NamesVisitor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.*;

public class ClientHandler extends Thread {

    private final static String FILE_UPLOAD = "Файл загружен на сервер!";
    private final static String FILE_UPLOAD_ERROR = "Произошла ошибка при записи файла на сервер, попробуйте еще раз!";
    private final static String FILE_NAME_ERROR = "Такого файла нету!";
    private final static String NO_FILES_IN_DIR = "В этой папке нет файлов";
    private final static String GO_TO_DIR = "Вы перешли в директорию ";
    private final static String GO_TO_DIR_ERROR = "Такой директории нету";
    private final static String GO_TO_PREV_DIR = "Вы перешли в директорию ";
    private final static String GO_TO_PREV_DIR_ERROR = "Вы находитесь в корневой директории";
    private final static String CREATE_DIR = "Директория создана!";
    private final static String CREATE_DIR_ERROR = "Такая директория уже есть";
    private final static String DELETE_FILE = "Удален файл ";
    private final static String DELETE_DIR = "Удалена директория ";
    private final static String DELETE_ERROR = "Такой директории/файла нету";

    private final static String ROOT_DIRECTORY_PATH = "./server/storage";

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private String clientName;
    private String clientDirectoryPath;
    private String currentDirectoryPath;

    public ClientHandler(Socket socket) {
        try {
            System.out.println("New client connected!");
            this.socket = socket;
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkDirectory() {
        return Files.exists(Paths.get(clientDirectoryPath));
    }

    private void createRootDirectory() {
        try {
            Files.createDirectory(Paths.get(clientDirectoryPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        handleMessages();
    }

    private void handleMessages() {
        try {
            while (true) {
                try {
                    if (!socket.isClosed()) {
                        Message m = (Message) in.readObject();
                        if (m.isFile()) {
                            uploadFile(m);
                        } else if (m.isCommand()) {
                            doCommand(m);
                        }
                    } else {
                        break;
                    }
                } catch (SocketException e) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void uploadFile(Message m) {
        try {
            TempFileMessage tmp = (TempFileMessage) m;
            Path path = Paths.get(currentDirectoryPath + "/" + tmp.getFileName());
            Files.write(path, tmp.getBytes());
        } catch (IOException e) {
            try {
                Answer answer = new Answer(FILE_UPLOAD_ERROR);
                out.writeObject(answer);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        Answer answer = new Answer(FILE_UPLOAD);
        try {
            out.writeObject(answer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doCommand(Message m) {
        Command c = (Command) m;
        if (c.isDownload()) {
            downloadFile(c);
        } else if (c.isGetFilesNames()) {
            getFilesNames();
        } else if (c.isGoToDir()) {
            goToDir(c);
        } else if (c.isGoToPrevDir()) {
            goToPrevDir();
        } else if (c.isCreateDir()) {
            createDir(c);
        } else if (c.isDelete()) {
            delete(c);
        } else if (c.isReg()) {
            reg(c);
        } else if (c.isAuth()) {
            auth(c);
        }
    }

    private void auth(Command c) {
        try {
            AuthCommand ac = (AuthCommand) c;
            AuthStatus status = DBService.auth(ac.getLogin(), ac.getPassword());
            if (status == AuthStatus.OK) {
                clientName = ac.getLogin();
                clientDirectoryPath = ROOT_DIRECTORY_PATH + "/" + clientName;
                currentDirectoryPath = clientDirectoryPath;
                if (!checkDirectory()) {
                    createRootDirectory();
                }
            }
            out.writeObject(new Answer(status, AnswerType.AUTH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reg(Command c) {
        try {
            RegCommand rc = (RegCommand) c;
            RegStatus status = DBService.reg(rc.getLogin(), rc.getPassword());
            out.writeObject(new Answer(status, AnswerType.REG));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadFile(Command c) {
        try {
            DownloadFileCommand dfc = (DownloadFileCommand) c;
            String fileName = dfc.getFileName();
            Path path = Paths.get(currentDirectoryPath + "/" + fileName);
            if (Files.exists(path)) {
                TempFileMessage tmp = new TempFileMessage(path, dfc.getSavePath());
                out.writeObject(tmp);
            } else {
                Answer answer = new Answer(FILE_NAME_ERROR);
                out.writeObject(answer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getFilesNames() {
        try {
            NamesVisitor namesVisitor = new NamesVisitor(currentDirectoryPath);
            Files.walkFileTree(Paths.get(currentDirectoryPath), namesVisitor);
            String names = namesVisitor.getNames();
            if (names.length() > 0) {
                names = names.substring(0, names.length() - 1);
            } else {
                names = NO_FILES_IN_DIR;
            }
            out.writeObject(new Answer(names));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToDir(Command c) {
        try {
            GoToDirCommand gtdc = (GoToDirCommand) c;
            String dirName = gtdc.getDirName();
            Path path = Paths.get(currentDirectoryPath + "/" + dirName);
            if (Files.exists(path)) {
                currentDirectoryPath = currentDirectoryPath + "/" + dirName;
                out.writeObject(new Answer(GO_TO_DIR + dirName));
            } else {
                out.writeObject(new Answer(GO_TO_DIR_ERROR));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToPrevDir() {
        try {
            if (Paths.get(currentDirectoryPath).toAbsolutePath().equals
                    (Paths.get(clientDirectoryPath).toAbsolutePath())) {
                out.writeObject(new Answer(GO_TO_PREV_DIR_ERROR));
            } else {
                Path path = Paths.get(currentDirectoryPath).getParent();
                currentDirectoryPath = path.toString();
                out.writeObject(new Answer(GO_TO_PREV_DIR + path.getFileName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createDir(Command c) {
        try {
            CreateDirCommand cdc = (CreateDirCommand) c;
            Path path = Paths.get(currentDirectoryPath + "/" + cdc.getDirName());
            if (Files.exists(path)) {
                out.writeObject(new Answer(CREATE_DIR_ERROR));
            } else {
                Files.createDirectory(path);
                out.writeObject(new Answer(CREATE_DIR));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void delete(Command c) {
        try {
            DeleteCommand dc = (DeleteCommand) c;
            Path path = Paths.get(currentDirectoryPath + "/" + dc.getName());
            if (Files.exists(path)) {
                if (Files.isDirectory(path)) {
                    DeleteDirVisitor ddv = new DeleteDirVisitor(dc.getName());
                    Files.walkFileTree(path, ddv);
                    out.writeObject(new Answer(DELETE_DIR + dc.getName()));
                } else if (Files.isRegularFile(path)) {
                    Files.delete(path);
                    out.writeObject(new Answer(DELETE_FILE + dc.getName()));
                }
            } else {
                out.writeObject(new Answer(DELETE_ERROR));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
