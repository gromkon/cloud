package Messages.Commands;

import Messages.Base.Message;
import Messages.Base.MessageType;

import java.io.Serializable;

public class Command extends Message implements Serializable {

    private CommandType commandType;

    public Command(CommandType commandType) {
        super(MessageType.COMMAND);
        this.commandType = commandType;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public boolean isDownload() {
        return commandType == CommandType.DOWNLOAD;
    }

    public boolean isGetFilesNames() {
        return commandType == CommandType.GET_FILES_NAMES;
    }

    public boolean isGoToDir() {
        return commandType == CommandType.GO_TO_DIR;
    }

    public boolean isGoToPrevDir() {
        return commandType == CommandType.GO_TO_PREV_DIR;
    }

    public boolean isCreateDir() {
        return commandType == CommandType.CREATE_DIR;
    }

    public boolean isDelete() {
        return commandType == CommandType.DELETE;
    }

    public boolean isAuth() {
        return commandType == CommandType.AUTH;
    }

    public boolean isReg() {
        return commandType == CommandType.REG;
    }

}
