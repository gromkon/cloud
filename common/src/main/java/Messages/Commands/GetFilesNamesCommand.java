package Messages.Commands;

import java.io.Serializable;

public class GetFilesNamesCommand extends Command implements Serializable {

    public GetFilesNamesCommand() {
        super(CommandType.GET_FILES_NAMES);
    }
}
