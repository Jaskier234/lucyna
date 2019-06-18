package main.java.index;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class IndexFileVisitor extends SimpleFileVisitor<Path> {

    private Writer index;

    public IndexFileVisitor(Writer i)
    {
        super();
        index = i;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//        System.out.println("dodaję do indeksu " + file.toString());
//        index.addFile(file);
        return FileVisitResult.CONTINUE; // super.visitFile(file, attrs);
    }
}
