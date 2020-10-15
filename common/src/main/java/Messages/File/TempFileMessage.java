package Messages.File;

import Messages.Base.Message;
import Messages.Base.MessageType;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class TempFileMessage extends Message implements Serializable {

    private String fileName;
    private long size;
    private byte[] bytes;

    private String savePath;

    public TempFileMessage(Path path) {
        super(MessageType.FILE);
        try {
            this.fileName = path.getFileName().toString();
            this.size = Files.size(path);
            this.bytes = Files.readAllBytes(path);
            this.savePath = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TempFileMessage(Path path, String savePath) {
        super(MessageType.FILE);
        try {
            this.fileName = path.getFileName().toString();
            this.size = Files.size(path);
            this.bytes = Files.readAllBytes(path);
            this.savePath = savePath;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public boolean isSavePath() {
        return savePath == null;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    @Override
    public String toString() {
        return "Message.File.TempFileMessage{" +
                "fileName='" + fileName + '\'' +
                ", size=" + size +
                "}";
    }
}
