package Visitors;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class NamesVisitor implements FileVisitor {

    private StringBuilder sb;
    private String path;

    public NamesVisitor(String path) {
        this.sb = new StringBuilder();
        this.path = path;
    }

    public String getNames() {
        return sb.toString();
    }

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        Path dirPath = (Path) dir;
        if (dirPath.getFileName().equals(Paths.get(path).getFileName())) {
            return FileVisitResult.CONTINUE;
        } else {
            sb.append(dirPath.getFileName()).append("\n");
            return FileVisitResult.SKIP_SUBTREE;
        }
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        Path filePath = (Path) file;
        sb.append(filePath.getFileName()).append("\n");
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        Path filePath = (Path) file;
        sb.append(filePath.getFileName()).append("\n");
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        return FileVisitResult.TERMINATE;
    }


}
