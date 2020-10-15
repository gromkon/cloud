package Messages.Commands;

import java.io.Serializable;

public class DeleteCommand extends Command implements Serializable {

    private String name;

    public DeleteCommand(String name) {
        super(CommandType.DELETE);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
