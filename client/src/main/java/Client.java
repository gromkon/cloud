import Messages.Answers.Answer;
import Messages.Commands.*;
import Messages.File.TempFileMessage;
import Messages.Base.Message;
import Statuses.AuthStatus;
import Statuses.RegStatus;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Client {

    private final static String AUTH_PLS = "Пожалуйста авторизуйтесь или зарегестрируйтесь\n" +
            "Для авторизации используйте \"/auth login password\"\n" +
            "Для регистрации используйте \"/reg login password passwordCheck\"";
    private final static String HELP = "/h";
    private final static String WELCOME = "Добро пожаловать!\nДля просмотра списка доступных комманд введите " + HELP;
    private final static String HELP_MESSAGE = "Загрузить файл \"/u путь_к_файлу\"\n" +
            "Скачать файл \"/d имя_файла путь_сохранения\"\n" +
            "Список файлов в папке \"/ls\"\n" +
            "Перейти в директорию \"/cd имя_директории\"\n" +
            "Перейти в предыдущую директорию \"/..\"\n" +
            "Создать директорию \"/touch имя_директории\"\n" +
            "Удалить файл \"/del имя_файла\"";
    private final static String PASSWORDS_NOT_EQUALS = "Пароли не совпадают!";
    private final static String AUTH_NO_LOGIN = "Такого логина не существует";
    private final static String AUTH_INCORRECT_PASSWORD = "Не правильный пароль";
    private final static String AUTH_OK = "Вы успешно авторизировались";
    private final static String AUTH_ERROR = "Произошла ошибка при авторизации, попробуйте еще раз";
    private final static String REG_LOGIN_USED = "Данный логин занят";
    private final static String REG_OK = "Вы успешно зарегестрировались. Пожалуйста, авторизуйтесь";
    private final static String REG_ERROR = "Произошла ошибка при регистрации, попробуйте еще раз";
    private final static String WRONG_COMMAND = "Некоректный запрос";
    private final static String WRONG_COMMAND_STYLE = "Не правильные аргументы команды. Для просмотра доступных комманд введите " + HELP;
    private final static String FILE_DOWNLOADED = "Файл скачан!";
    private final static String NO_FILE = "Такого файла нет. Проверьте путь до файла";

    private final static String AUTH_COMMAND = "/auth";
    private final static String REG_COMMAND = "/reg";
    private final static String UPLOAD_FILE = "/u";
    private final static String DOWNLOAD_FILE_COMMAND = "/d";
    private final static String GET_FILES_NAMES_COMMAND = "/ls";
    private final static String GO_TO_DIR_COMMAND = "/cd";
    private final static String GO_TO_PREV_DIR_COMMAND = "/..";
    private final static String CREATE_DIR_COMMAND = "/touch";
    private final static String DELETE_COMMAND = "/del";

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private boolean isAuth;

    public Client() {
        try {
            socket = new Socket(ConnectionData.getHOST(), ConnectionData.getPORT());
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            isAuth = false;
            handleInputMessages();
            authAndReg();
            handleConsole();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authAndReg() {
        try {
            System.out.println(AUTH_PLS);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String command;
            while (!isAuth) {
                if (reader.ready()) {
                    command = reader.readLine();
                    if (command.startsWith(AUTH_COMMAND)) {
                        sendAuthCommand(command);
                    } else if (command.startsWith(REG_COMMAND)) {
                        sendRegCommand(command);
                    } else {
                        System.out.println(WRONG_COMMAND);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendAuthCommand(String command) {
        try {
            String login = command.split(" ")[1];
            long password = command.split(" ")[2].hashCode();
            out.writeObject(new AuthCommand(login, password));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRegCommand(String command) {
        try {
            String login = command.split(" ")[1];
            long password = command.split(" ")[2].hashCode();
            long passwordCheck = command.split(" ")[3].hashCode();
            if (password == passwordCheck) {
                out.writeObject(new RegCommand(login, password));
            } else {
                System.out.println(PASSWORDS_NOT_EQUALS);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleInputMessages() {
        new Thread(() -> {
            try {
                while (true) {
                    Message m = (Message) in.readObject();
                    if (m.isAnswer()) {
                        Answer a = (Answer) m;
                        String content = a.getContent();
                        if (a.isAuth()) {
                            if (content.equals(AuthStatus.NO_LOGIN.toString())) {
                                System.out.println(AUTH_NO_LOGIN);
                            } else if (content.equals(AuthStatus.INCORRECT_PASSWORD.toString())) {
                                System.out.println(AUTH_INCORRECT_PASSWORD);
                            } else if (content.equals(AuthStatus.OK.toString())) {
                                System.out.println(AUTH_OK);
                                isAuth = true;
                            } else {
                                System.out.println(AUTH_ERROR);
                            }
                        } else if (a.isReg()) {
                            if (content.equals(RegStatus.LOGIN_USED.toString())) {
                                System.out.println(REG_LOGIN_USED);
                            } else if (content.equals(RegStatus.OK.toString())) {
                                System.out.println(REG_OK);
                            } else {
                                System.out.println(REG_ERROR);
                            }
                        } else {
                            System.out.println(a);
                        }
                    } else if (m.isFile()) {
                        TempFileMessage tmp = (TempFileMessage) m;
                        String pathString = (tmp.getSavePath() + "/" + tmp.getFileName()).replace("\\", "/");
                        Path path = Paths.get(pathString);
                        Files.createDirectories(path.getParent());
                        Files.write(path, tmp.getBytes());
                        System.out.println(FILE_DOWNLOADED);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleConsole() {
        Scanner reader = new Scanner(System.in);
        System.out.println(WELCOME);
        String command;
        while (true) {
            command = reader.nextLine();
            if (command.startsWith(UPLOAD_FILE)) {
                uploadFile(command);
            } else if (command.startsWith("/")) {
                sendCommand(command);
            } else {
                System.out.println(WRONG_COMMAND);
            }
        }

    }

    private void uploadFile(String command) {
        try {
            String pathString = command.substring(UPLOAD_FILE.length() + 1);
            Path path = Paths.get(pathString);
            if (Files.exists(path)) {
                TempFileMessage tmp = new TempFileMessage(path);
                out.writeObject(tmp);
            } else {
                System.out.println(NO_FILE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendCommand(String command) {
        String commandTypeString = command.split(" ")[0];
        switch (commandTypeString) {
            case DOWNLOAD_FILE_COMMAND:
                sendDownloadCommand(command);
                break;
            case GET_FILES_NAMES_COMMAND:
                sendGetFilesNamesCommand();
                break;
            case GO_TO_DIR_COMMAND:
                sendGoToDirCommand(command);
                break;
            case GO_TO_PREV_DIR_COMMAND:
                sendGoToPrevDirCommand();
                break;
            case CREATE_DIR_COMMAND:
                sendCreateDirCommand(command);
                break;
            case DELETE_COMMAND:
                sendDeleteCommand(command);
                break;
            case HELP:
                System.out.println(HELP_MESSAGE);
                break;
            default:
                System.out.println(WRONG_COMMAND);
                break;
        }
    }

    private void sendDownloadCommand(String command) {
        try {
            String fileName = command.split(" ")[1];
            String pathToSave = command.split(" ")[2];
            DownloadFileCommand dfc = new DownloadFileCommand(fileName, pathToSave);
            out.writeObject(dfc);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(WRONG_COMMAND_STYLE);
        }
    }

    private void sendCreateDirCommand(String command) {
        try {
            String dirName = command.split(" ")[1];
            out.writeObject(new CreateDirCommand(dirName));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(WRONG_COMMAND_STYLE);
        }
    }

    private void sendDeleteCommand(String command) {
        try {
            String name = command.split(" ")[1];
            out.writeObject(new DeleteCommand(name));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(WRONG_COMMAND_STYLE);
        }
    }

    private void sendGetFilesNamesCommand() {
        try {
            out.writeObject(new GetFilesNamesCommand());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendGoToDirCommand(String command) {
        try {
            String dir = command.split(" ")[1];
            out.writeObject(new GoToDirCommand(dir));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(WRONG_COMMAND_STYLE);
        }
    }

    private void sendGoToPrevDirCommand() {
        try {
            out.writeObject(new GoToPrevDirCommand());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
