package main.java.index;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileParser {
    private Tika tika;

    public FileParser() {
        tika = new Tika();
    }

    public String parseFile(Path file) throws IOException, TikaException {
        InputStream inputStream = Files.newInputStream(file);
        return tika.parseToString(inputStream);
    }

}
