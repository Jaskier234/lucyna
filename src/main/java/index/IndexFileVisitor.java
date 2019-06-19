package main.java.index;

import org.apache.tika.exception.TikaException;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class IndexFileVisitor extends SimpleFileVisitor<Path> {

    private Writer index;
    private FileParser fileParser;

    public IndexFileVisitor(Writer i)
    {
        super();
        index = i;
        fileParser = new FileParser();
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        System.out.println("dodaję do indeksu " + file.toString());

        String fileContent;
        try {
            fileContent = fileParser.parseFile(file);
            index.addFile(file, fileContent);
        }
        catch (TikaException e) {
            System.out.println("Nie udało się odczytać treści pliku");
        }

        return FileVisitResult.CONTINUE;
    }
}
