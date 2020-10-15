package Messages.Answers;

import Messages.Base.Message;
import Messages.Base.MessageType;
import Statuses.AuthStatus;
import Statuses.RegStatus;

import java.io.Serializable;

public class Answer extends Message implements Serializable {

    private String content;
    private AnswerType type;

    public Answer(String content) {
        super(MessageType.ANSWER);
        type = AnswerType.ANSWER;
        this.content = content;
    }

    public Answer(AuthStatus as, AnswerType type) {
        super(MessageType.ANSWER);
        this.type = type;
        this.content = as.toString();
    }

    public Answer(RegStatus as, AnswerType type) {
        super(MessageType.ANSWER);
        this.type = type;
        this.content = as.toString();
    }

    public boolean isReg() {
        return type == AnswerType.REG;
    }

    public boolean isAuth() {
        return type == AnswerType.AUTH;
    }

    public boolean isAnswerMessage() {
        return type == AnswerType.ANSWER;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
