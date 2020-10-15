package Messages.Commands;

import java.io.Serializable;

public class DownloadFileCommand extends Command implements Serializable {

    private String fileName;
    private String savePath;

    public DownloadFileCommand(String fileName, String savePath) {
        super(CommandType.DOWNLOAD);
        this.fileName = fileName;
        this.savePath = savePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }
}
