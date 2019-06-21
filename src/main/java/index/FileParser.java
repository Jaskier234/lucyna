package main.java.index;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;

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

    public String getLanguage(String text) {
        LanguageDetector detector = new OptimaizeLangDetector();
        detector.addText(text.toCharArray(), 0, text.length());

        try {
            detector.loadModels();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return detector.detectAll().get(0).getLanguage();
    }

}
