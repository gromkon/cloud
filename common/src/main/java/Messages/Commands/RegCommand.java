package Messages.Commands;

import java.io.Serializable;

public class RegCommand extends Command implements Serializable {

    private String login;
    private long password;

    public RegCommand(String login, long password) {
        super(CommandType.REG);
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public long getPassword() {
        return password;
    }

    public void setPassword(long password) {
        this.password = password;
    }
}
