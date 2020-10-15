package Visitors;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class DeleteDirVisitor implements FileVisitor {

    private String dirName;

    public DeleteDirVisitor(String dirName) {
        this.dirName = dirName;
    }

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        Path dirPath = (Path) dir;
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        Path filePath = (Path) file;
        Files.delete(filePath);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        Path filePath = (Path) file;
        Files.delete(filePath);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        Path dirPath = (Path) dir;
        Files.delete(dirPath);
        if (dirPath.getFileName().toString().equals(dirName)) {
            return FileVisitResult.TERMINATE;
        } else {
            return FileVisitResult.CONTINUE;
        }
    }
}
