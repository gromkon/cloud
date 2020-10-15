package Messages.Commands;

import java.io.Serializable;

public class GoToDirCommand extends Command implements Serializable {

    private String dirName;

    public GoToDirCommand(String dirName) {
        super(CommandType.GO_TO_DIR);
        this.dirName = dirName;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }
}
