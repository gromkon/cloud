package Messages.Commands;

import java.io.Serializable;

public class CreateDirCommand extends Command implements Serializable {

    private String dirName;

    public CreateDirCommand(String dirName) {
        super(CommandType.CREATE_DIR);
        this.dirName = dirName;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }
}
