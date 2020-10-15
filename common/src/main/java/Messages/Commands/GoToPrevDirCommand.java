package Messages.Commands;

import java.io.Serializable;

public class GoToPrevDirCommand extends Command implements Serializable {
    public GoToPrevDirCommand() {
        super(CommandType.GO_TO_PREV_DIR);
    }
}
