package Messages.Base;

import java.io.Serializable;

public class Message implements Serializable {

    private MessageType type;

    public Message(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public boolean isFile() {
        return type == MessageType.FILE;
    }

    public boolean isCommand() {
        return type == MessageType.COMMAND;
    }

    public boolean isAnswer() {
        return type == MessageType.ANSWER;
    }

}
