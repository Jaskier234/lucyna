package main.java.test;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main
{
    public static void main(String[] args) throws IOException, TikaException {
        System.out.println(parseFile("test.txt"));
    }

    public static String parseFile(String s) throws IOException, TikaException {
        Tika tika = new Tika();
        Path path = Paths.get(s);
        System.out.println(path.toAbsolutePath().toString());
        try (InputStream stream = Files.newInputStream(path)) {
            return tika.parseToString(stream);
        }

    }
}